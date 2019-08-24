package com.pankaj.roi.services;

import com.pankaj.roi.entities.User;
import com.pankaj.roi.enums.FBPermissions;
import com.pankaj.roi.exceptions.MissingRequiredFBPermissions;
import com.pankaj.roi.exceptions.UserAlreadyPresent;
import com.pankaj.roi.models.FBUser;
import com.pankaj.roi.models.FBUserCredentials;
import com.pankaj.roi.models.TokenPermissions;
import com.pankaj.roi.models.UserResponse;
import com.pankaj.roi.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.pankaj.roi.enums.FBPermissions.*;
import static com.pankaj.roi.utils.LambdaUtils.expValIfExpWrapper;
import static com.pankaj.roi.validators.NotNullValidators.validateNotNull;

@Slf4j
@Service
public class UserService {

    @Autowired
    private FBAPIClient apiClient;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private TransformationService transformationService;


    private static final String ACCESS_GRANTED = "granted";

    public void validateAccessTokenPermissions(final FBUserCredentials cred,
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
    }

    public EnumSet<FBPermissions> getAllFBPermissionsForLoadingUserNPhotosDetails() {
        return EnumSet.of(PUBLIC_PROFILE, EMAIL, USER_PHOTOS, USER_GENDER);
    }

    public void loadFBUser(FBUserCredentials cred) throws UserAlreadyPresent, MissingRequiredFBPermissions {

        EnumSet<FBPermissions> requiredPermissions = getAllFBPermissionsForLoadingUserNPhotosDetails();
        validateAccessTokenPermissions(cred, requiredPermissions);

        if (getUserByFbId(cred.getFbId()).isPresent()) throw new UserAlreadyPresent("User already present");

        FBUser userDetails = apiClient.getUserDetails(cred);
        User user = User.of(userDetails);
        usersRepository.save(user);

        CompletableFuture.runAsync(() -> photoService.startLoadingUserPhotos(cred, user));
    }

    public Optional<UserResponse> fetchUserDetails(String fbId) {
        Optional<User> user = getUserByFbId(fbId);
        return user.map(u -> transformationService.getUserResponseFromUser(u));
    }

    public Optional<User> getUserByFbId(String fbId) {
        return usersRepository.findByFbId(fbId);
    }

    @Transactional
    public Optional<Boolean> deleteUserData(String fbId) {
        Optional<User> user = getUserByFbId(fbId);
        return user.map(u -> {
            photoService.deletePhotoDataForUser(u);
            usersRepository.delete(u);
            return true;
        });
    }
}