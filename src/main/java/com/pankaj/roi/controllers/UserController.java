package com.pankaj.roi.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pankaj.roi.exceptions.APIClientException;
import com.pankaj.roi.exceptions.MissingRequiredFBPermissions;
import com.pankaj.roi.exceptions.UserAlreadyPresent;
import com.pankaj.roi.models.FBError;
import com.pankaj.roi.models.FBUserCredentials;
import com.pankaj.roi.models.UserPhotoResponse;
import com.pankaj.roi.models.UserResponse;
import com.pankaj.roi.services.PhotoService;
import com.pankaj.roi.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PhotoService photoService;

    @PostMapping(path = "users")
    public ResponseEntity<?> postUser(@RequestBody FBUserCredentials user) throws MissingRequiredFBPermissions, UserAlreadyPresent, APIClientException {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", user.getFbId());
            userService.loadFBUser(user);
            return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
        } catch(HttpClientErrorException e) {
            String message = getAPIErrorMessage(
                    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false),
                    e.getResponseBodyAsString());
            throw new APIClientException(message);
        }
    }

    @GetMapping(path = "users/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
    	Optional<UserResponse> user = userService.fetchUserDetails(id);
    	if(user.isPresent())
    		return new ResponseEntity<>(user.get(), new HttpHeaders(), HttpStatus.OK);
    	return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "users/{id}/photos")
    public ResponseEntity<?> getPhotos(@PathVariable String id) {
        Optional<UserPhotoResponse> photo = photoService.fetchUserPhotoDetails(id);
        if(photo.isPresent())
            return new ResponseEntity<>(photo.get(), new HttpHeaders(), HttpStatus.OK);
        return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "users/{id}")
    public ResponseEntity<?> deleteUserData(@PathVariable String id) {
        Optional<Boolean> isDeleted = userService.deleteUserData(id);
        if(isDeleted.isPresent())
            return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.OK);
        return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    private String getAPIErrorMessage(ObjectMapper objectMapper, String responseBodyAsString) {
        try {
            FBError fbError = objectMapper.readValue(responseBodyAsString, FBError.class);
            return fbError.getError().getMessage();
        } catch(Exception e) {
            return "Some error occurred while calling Facebook API";
        }
    }
}