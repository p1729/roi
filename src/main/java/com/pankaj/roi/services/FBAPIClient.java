package com.pankaj.roi.services;

import com.pankaj.roi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@Slf4j
@Service
public class FBAPIClient {

    private static final String FB_GRAPH_API_URI = "https://graph.facebook.com";

    private static final String PERMISSIONS = "permissions";
    private static final String PHOTOS = "photos";

    private static final String FIELDS = "fields";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String GENDER = "gender";
    private static final String PICTURE = "picture";
    private static final String ID = "id";
    private static final String LINK = "link";
    private static final String ALBUM = "album";
    private static final String REACTIONS = "reactions";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String LIMIT = "limit";
    private static final String TYPE = "type";
    private static final String UPLOADED = "uploaded";
    private static final String ONE = "1";


    private RestTemplate restTemplate = new RestTemplate();

    public TokenPermissions getTokenPermissions(FBUserCredentials cred) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_API_URI)
                .pathSegment(cred.getFbId())
                .queryParam(FIELDS, PERMISSIONS)
                .queryParam(ACCESS_TOKEN, cred.getAccessToken());

        return get(builder.toUriString(), TokenPermissions.class);
    }

    public FBUser getUserDetails(FBUserCredentials user) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_API_URI)
                .pathSegment(user.getFbId())
                .queryParam(FIELDS, String.join(",", ID, NAME, EMAIL, GENDER, PICTURE))
                .queryParam(ACCESS_TOKEN, user.getAccessToken());

        return get(builder.toUriString(), FBUser.class);
    }

    public FBPhotos getUserPhotoDetails(FBUserCredentials cred) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FB_GRAPH_API_URI)
                .pathSegment(cred.getFbId(), PHOTOS)
                .queryParam(FIELDS, String.join(",", LINK, ALBUM, PICTURE, REACTIONS))
//                .queryParam(LIMIT, ONE)
//                .queryParam(TYPE, UPLOADED)
                .queryParam(ACCESS_TOKEN, cred.getAccessToken());

        return get(builder.toUriString(), FBPhotos.class);
    }

    public <T> T getNextPage(Paging request, Class<T> clazz) {
        return get(UriUtils.decode(request.getNext(), "UTF-8"), clazz);
    }

    private <T> T get(String uri, Class<T> clazz) {
        return restTemplate.getForObject(uri, clazz);
    }
}