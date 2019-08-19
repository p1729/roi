package com.pankaj.roi.services;

import com.pankaj.roi.entities.Photo;
import com.pankaj.roi.entities.PhotoAlbum;
import com.pankaj.roi.entities.PhotoReaction;
import com.pankaj.roi.entities.User;
import com.pankaj.roi.enums.FBPermissions;
import com.pankaj.roi.exceptions.MissingRequiredFBPermissions;
import com.pankaj.roi.exceptions.UserAlreadyPresent;
import com.pankaj.roi.models.*;
import com.pankaj.roi.repositories.PhotoAlbumRepository;
import com.pankaj.roi.repositories.PhotoReactionRepository;
import com.pankaj.roi.repositories.PhotoRepository;
import com.pankaj.roi.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.pankaj.roi.enums.FBPermissions.*;
import static com.pankaj.roi.utils.LambdaUtils.expValIfExpWrapper;
import static com.pankaj.roi.validators.NotNullValidators.validateNotNull;

@Slf4j
@Service
public class UserService {

    @Autowired
    private APIClient apiClient;

    @Autowired
    private PagingService pagingService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoReactionRepository reactionRepository;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    private static final String ACCESS_GRANTED = "granted";

    public boolean validateAccessTokenPermissions(final FBUserCredentials cred,
                                                  final EnumSet<FBPermissions> requiredPermissions) throws MissingRequiredFBPermissions {
        TokenPermissions tokenPermissions = apiClient.getTokenPermissions(cred);

        validateNotNull(tokenPermissions, "tokenPermission");
        validateNotNull(tokenPermissions.getPermissions(), "tokenPermission.permissions");
        validateNotNull(tokenPermissions.getPermissions().getData(), "tokenPermission.permissions.data");

        long requiredTokenPermissionCount = tokenPermissions.getPermissions().getData().stream()
                .filter(p -> ACCESS_GRANTED.equals(p.getStatus()))
                .map(expValIfExpWrapper(p -> of(p.getPermission()), IllegalArgumentException.class, null))
                .filter(p -> !Objects.isNull(p)).filter(requiredPermissions::contains).count();

        if (requiredTokenPermissionCount != requiredPermissions.size())
            throw new MissingRequiredFBPermissions(requiredPermissions);
        return true;
    }

    public EnumSet<FBPermissions> getAllFBPermissionsForLoadingUserNPhotosDetails() {
        return EnumSet.of(PUBLIC_PROFILE, EMAIL, USER_PHOTOS, USER_GENDER);
    }

    public boolean loadFBUser(FBUserCredentials cred) throws UserAlreadyPresent {

        if (getUserByFbId(cred.getFbId()).isPresent())
            throw new UserAlreadyPresent("User already present");

        FBUser userDetails = apiClient.getUserDetails(cred);
        User user = User.of(userDetails);
        usersRepository.save(user);

        startLoadingUserPhotos(cred, user); // Computation in new Thread for photos
        return true;
    }

    public Optional<User> getUserByFbId(String fbId) {
        return usersRepository.findByFbId(fbId);
    }

    @Async
    public void startLoadingUserPhotos(FBUserCredentials cred, User user) {
        FBPhotos userPhotoDetails = apiClient.getUserPhotoDetails(cred);
        loadUserPhotos(user, userPhotoDetails);
    }

    private void loadNextPageOfPhotos(User user, FBPhotos userPhotoDetails) {
        try {
            if (Objects.isNull(userPhotoDetails.getPaging()))
                return;
            if (isNextPageAvailable(userPhotoDetails.getPaging()))
				loadNextPhotoPage(userPhotoDetails.getPaging(), user);
            	//pagingService.processPagingRequest(new PagingRequest(FBPhotos.class, user, null, userPhotoDetails.getPaging()));
        } catch (Exception e) {
            LOG.error("Error occured while loading next page of photos for user id {} {} {}", user.getFbId(),
                    user.getName(), e);
        }
    }

    private boolean isNextPageAvailable(Paging page) {
        String next = page.getNext();
        return !Objects.isNull(next) && !next.trim().isEmpty();
    }

    private void loadNextPhotoPage(Paging page, User user) {
		CompletableFuture.runAsync(() -> {
			try {
				FBPhotos userPhotoDetails = apiClient.getNextPhotoPage(page, FBPhotos.class);
				loadUserPhotos(user, userPhotoDetails);
			} catch(Exception e) {
				LOG.error("Error occured while asking for next page {}", e);
			}
		});
	}

	private void loadNextReactionsPage(Paging page, Photo photo) {
		CompletableFuture.runAsync(() -> {
			try {
				ReactionsData reactionsData = apiClient.getNextReactionPage(page, ReactionsData.class);
				loadNextPageOfPhotosReactions(photo, reactionsData);
			} catch(Exception e) {
				LOG.error("Error occured while asking for next page {}", e);
			}
		});
	}

    public void loadUserPhotos(User user, FBPhotos userPhotoDetails) {
        List<Pair<Photo, ReactionsData>> photoReactionData = getPhotoReactionData(userPhotoDetails, user);
        List<Photo> photos = photoReactionData.stream().map(Pair::getFirst).collect(Collectors.toList());
        //Get the ids of the albums first .....
        List<PhotoAlbum> albums = PhotoAlbum.of(photos);
        List<PhotoReaction> photoReactions = PhotoReaction.of(photos);

        //Only persist non persisted
        List<PhotoAlbum> updatedAlbums = getUpdatedAlbumWithPersistanceIdentifier(albums);
        updatePhotosWithPersistedAlbum(photos, updatedAlbums);
        photoAlbumRepository.saveAll(updatedAlbums);
        //boolean magic = false;
//          photos.stream().forEach(p -> p.setAlbum(updatedAlbums.get(0)));
        photoRepository.saveAll(photos);
        reactionRepository.saveAll(photoReactions);

        loadNextPageOfPhotos(user, userPhotoDetails);
        loadNextPagesOfPhotosReactions(photoReactionData);
    }

    private void updatePhotosWithPersistedAlbum(List<Photo> photos, List<PhotoAlbum> updatedAlbums) {
        photos.forEach(p -> p.setAlbum(updatedAlbums.stream().filter(a -> a.equals(p.getAlbum())).findFirst().orElse(p.getAlbum())));
    }

    private void loadNextPagesOfPhotosReactions(List<Pair<Photo, ReactionsData>> userPhotoDetails) {
        userPhotoDetails.stream()
                .filter(Objects::nonNull)
                .filter(p -> Objects.nonNull(p.getSecond().getData()))
                .filter(p -> p.getSecond().getData().size() > 0)
                .forEach(p -> {
                    loadNextPageOfPhotosReactions(p.getFirst(), p.getSecond());
                });
    }

    private void loadNextPageOfPhotosReactions(Photo photo, ReactionsData data) {
        try {
            if (Objects.isNull(data.getPaging()))
                return;
            if (isNextPageAvailable(data.getPaging()))
				loadNextReactionsPage(data.getPaging(), photo);
        } catch (Exception e) {
            LOG.error("Error occured while loading next page of reactions for photo id {} {}", photo.getFbId(), e);
        }
    }

    private List<Pair<Photo, ReactionsData>> getPhotoReactionData(FBPhotos photos, User user) {
        List<Pair<Photo, ReactionsData>> data = new ArrayList<>();
        photos.getData().forEach(photo -> {
            if(Objects.isNull(photo.getReactions()))
                data.add(Pair.of(Photo.of(photo, user), new ReactionsData()));
            else
                data.add(Pair.of(Photo.of(photo, user), photo.getReactions()));
        });
        return data;
    }

    public List<PhotoAlbum> getUpdatedAlbumWithPersistanceIdentifier(List<PhotoAlbum> albums) {
        List<String> albumFbIds = albums.stream().map(a -> a.getFbId()).collect(Collectors.toList());
        List<PhotoAlbum> persistedAlbums = photoAlbumRepository.findAlbumByFbId(albumFbIds);
        List<PhotoAlbum> nonPersistedAlbums = albums.stream().filter(a -> !persistedAlbums.contains(a))
                .collect(Collectors.toList());
        persistedAlbums.addAll(nonPersistedAlbums);
        return persistedAlbums;
    }
}