package com.pankaj.roi.services;

import com.pankaj.roi.entities.Photo;
import com.pankaj.roi.entities.PhotoAlbum;
import com.pankaj.roi.entities.User;
import com.pankaj.roi.enums.FBPermissions;
import com.pankaj.roi.models.*;
import com.pankaj.roi.repositories.PhotoAlbumRepository;
import com.pankaj.roi.repositories.PhotoRepository;
import com.pankaj.roi.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.pankaj.roi.enums.FBPermissions.*;
import static com.pankaj.roi.utils.LambdaUtils.expValIfExpWrapper;
import static com.pankaj.roi.validators.NotNullValidators.validateNotNull;

@Slf4j
@Service
public class UserService {

    @Autowired
    private APIClient apiClient;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    private static final String ACCESS_GRANTED = "granted";

    public boolean checkAccessTokenPermissions(final FBUserCredentials cred, final EnumSet<FBPermissions> requiredPermissions) {
        TokenPermissions tokenPermissions = apiClient.getTokenPermissions(cred);

        validateNotNull(tokenPermissions, "tokenPermission");
        validateNotNull(tokenPermissions.getPermissions(), "tokenPermission.permissions");
        validateNotNull(tokenPermissions.getPermissions().getData(), "tokenPermission.permissions.data");

        long requiredTokenPermissionCount = tokenPermissions.getPermissions().getData().stream()
                .filter(p -> ACCESS_GRANTED.equals(p.getStatus()))
                .map(expValIfExpWrapper(p -> of(p.getPermission()), IllegalArgumentException.class, null))
                .filter(p -> !Objects.isNull(p))
                .filter(requiredPermissions::contains)
                .count();

        return requiredTokenPermissionCount == requiredPermissions.size();
    }

    public EnumSet<FBPermissions> getAllFBPermissionsForLoadingUserNPhotosDetails() {
        return EnumSet.of(PUBLIC_PROFILE, EMAIL, USER_PHOTOS, USER_GENDER);
    }

    public void loadFBUser(FBUserCredentials cred) {
        Optional<User> found = usersRepository.findByFbId(cred.getFbId());
        LOG.info("found {}", found.isPresent());
        FBUser userDetails = apiClient.getUserDetails(cred);
        User user = User.of(userDetails);
        usersRepository.save(user);
        FBPhotos userPhotoDetails = apiClient.getUserPhotoDetails(cred);
        List<Photo> ph = Photo.of(userPhotoDetails, user);
        List<PhotoAlbum> al = PhotoAlbum.of(ph);
        photoAlbumRepository.saveAll(al);
        photoRepository.saveAll(ph);
        LOG.info("fb user {}", user);
        LOG.info("fb photos list {}", ph);
    }

}