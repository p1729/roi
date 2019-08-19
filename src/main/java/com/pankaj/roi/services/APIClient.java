package com.pankaj.roi.services;

import com.pankaj.roi.models.FBPhotos;
import com.pankaj.roi.models.FBUserCredentials;
import com.pankaj.roi.models.FBUser;
import com.pankaj.roi.models.TokenPermissions;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class APIClient {

    private static final String FB_GRAPH_API_URI = "https://graph.facebook.com";

    private static final String PERMISSIONS = "permissions";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String GENDER = "gender";
    private static final String PICTURE = "picture";
    private static final String FIELDS = "fields";
    public static final String ID = "id";
    private static final String ACCESS_TOKEN = "access_token";
    public static final String LINK = "link";
    public static final String ALBUM = "album";

    private final RestTemplate restTemplate = new RestTemplate();

    public TokenPermissions getTokenPermissions(FBUserCredentials cred) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_API_URI)
                .pathSegment(cred.getFbId())
                .queryParam(FIELDS, PERMISSIONS)
                .queryParam(ACCESS_TOKEN, cred.getAccessToken());

        return restTemplate.getForObject(builder.toUriString(), TokenPermissions.class);
    }

    public FBUser getUserDetails(FBUserCredentials user) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_API_URI)
                .pathSegment(user.getFbId())
                .queryParam(FIELDS, String.join(",", ID,NAME, EMAIL, GENDER, PICTURE))
                .queryParam(ACCESS_TOKEN, user.getAccessToken());

        return restTemplate.getForObject(builder.toUriString(), FBUser.class);
    }

    public FBPhotos getUserPhotoDetails(FBUserCredentials cred) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_API_URI)
                .pathSegment(cred.getFbId(),"photos")
                .queryParam(FIELDS, String.join(",", LINK, ALBUM, PICTURE))
                .queryParam(ACCESS_TOKEN, cred.getAccessToken());

        return restTemplate.getForObject(builder.toUriString(), FBPhotos.class);
    }
}