package com.pankaj.roi.services;

import com.pankaj.roi.entities.Photo;
import com.pankaj.roi.entities.PhotoAlbum;
import com.pankaj.roi.entities.PhotoReaction;
import com.pankaj.roi.entities.User;
import com.pankaj.roi.models.*;
import com.pankaj.roi.repositories.PhotoAlbumRepository;
import com.pankaj.roi.repositories.PhotoReactionRepository;
import com.pankaj.roi.repositories.PhotoRepository;
import com.pankaj.roi.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PhotoService {

    @Autowired
    private FBAPIClient apiClient;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoReactionRepository reactionRepository;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @Autowired
    private TransformationService transformationService;

    private <T> void loadNextPage(Paging page, Class<T> clazz, Consumer<T> thenConsumer) {
        CompletableFuture.supplyAsync(() -> apiClient.getNextPage(page, clazz)).thenAccept(thenConsumer);
    }

    private void loadNextPageOfPhotos(User user, FBPhotos userPhotoDetails) {
        try {
            if (Objects.nonNull(userPhotoDetails.getPaging()) && isNextPageAvailable(userPhotoDetails.getPaging()))
                loadNextPage(userPhotoDetails.getPaging(), FBPhotos.class, apiData -> loadUserPhotos(user, apiData));
        } catch (Exception e) {
            LOG.error("Error occurred while loading next page of photos for user id {} {} {}", user.getFbId(),
                    user.getName(), e);
        }
    }

    private void loadNextPageOfPhotosReactions(Photo photo, ReactionsData data) {
        try {
            if (Objects.nonNull(data.getPaging()) && isNextPageAvailable(data.getPaging()))
                loadNextPage(data.getPaging(), ReactionsData.class, apiData -> loadNextPageOfPhotosReactions(photo, apiData));
        } catch (Exception e) {
            LOG.error("Error occurred while loading next page of reactions for photo id {} {}", photo.getFbId(), e);
        }
    }

    public void startLoadingUserPhotos(FBUserCredentials cred, User user) {
        FBPhotos userPhotoDetails = apiClient.getUserPhotoDetails(cred);
        loadUserPhotos(user, userPhotoDetails);
    }

    private boolean isNextPageAvailable(Paging page) {
        String next = page.getNext();
        return Objects.nonNull(next) && !next.trim().isEmpty();
    }


    public void loadUserPhotos(User user, FBPhotos userPhotoDetails) {
        try {
            List<Pair<Photo, ReactionsData>> photoReactionData = transformationService.getPhotoReactionData(userPhotoDetails, user);
            Set<Photo> photos = photoReactionData.stream().map(Pair::getFirst).collect(Collectors.toSet());
            Set<PhotoAlbum> albums = transformationService.getSetOfAlbumFromPhotos(photos);

            photoAlbumRepository.saveAll(albums);
            photoRepository.saveAll(photos);

            loadNextPageOfPhotos(user, userPhotoDetails);
            loadNextPagesOfPhotosReactions(photoReactionData);
        } catch (Exception e) {
            LOG.error("Error occurred while loading user photos", e);
        }
    }

    private void loadNextPagesOfPhotosReactions(List<Pair<Photo, ReactionsData>> userPhotoDetails) {
        userPhotoDetails.stream().filter(Objects::nonNull)
                .filter(p -> Objects.nonNull(p.getSecond().getData()))
                .filter(p -> p.getSecond().getData().size() > 0)
                .forEach(p -> loadNextPageOfPhotosReactions(p.getFirst(), p.getSecond()));
    }

    public Optional<Photo> getPhotoByFbId(String fbId) {
        return photoRepository.findByFbId(fbId);
    }

    public Optional<PhotoReaction> getReactionByFbId(String fbId) {
        return reactionRepository.findByFbId(fbId);
    }

    public Optional<PhotoAlbum> getAlbumByFbId(String fbId) {
        return photoAlbumRepository.findByFbId(fbId);
    }

    public Optional<UserPhotoResponse> fetchUserPhotoDetails(String fbId) {
        Optional<User> user = usersRepository.findByFbId(fbId);
        return user.map(usr -> {
            UserPhotoResponse res = UserPhotoResponse.builder().photos(new ArrayList<>()).build();
            List<PhotoResponse> photos = usr.getPhotos().stream().map(photo -> {
                PhotoResponse build = PhotoResponse.builder().link(photo.getImageLink()).fbLink(photo.getFbLink())
                        .reactions(new ArrayList<>()).build();
                if (Objects.nonNull(photo.getAlbum())) build.setAlbumName(photo.getAlbum().getName());
                if (Objects.nonNull(photo.getPhotoReactions())) {
                    List<PhotoReactionResponse> reactRes = new ArrayList<>();
                    photo.getPhotoReactions().stream()
                            .collect(Collectors.groupingBy(PhotoReaction::getType, Collectors.counting()))
                            .forEach((type, count) -> reactRes.add(PhotoReactionResponse.builder().type(type.name())
                            .totalCount(count.toString()).build()));
                    build.getReactions().addAll(reactRes);
                }
                return build;
            }).collect(Collectors.toList());
            res.getPhotos().addAll(photos);
            return res;
        });
    }

    public void deletePhotoDataForUser(User user) {
        Set<Photo> photos = new HashSet<>() ;
        photos.addAll(user.getPhotos());

        photos.forEach(photo -> {
            photo.removeUser(user);
            //check if photos can be removed
            if(photo.getUsers().isEmpty()) {
                //photo can be deleted
                //check if albums can be removed
                if(photo.getAlbum().getPhotoList().size() == 1) {
                    PhotoAlbum album = photo.getAlbum();
                    album.removePhoto(photo);
                    photoAlbumRepository.delete(album);
                }
                Set<PhotoReaction> reactions = new HashSet<>() ;
                reactions.addAll(photo.getPhotoReactions());
                reactions.forEach(photo::removeReaction);
                photoRepository.delete(photo);
            }
        });
    }
}
