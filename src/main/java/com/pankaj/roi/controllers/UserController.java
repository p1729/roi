package com.pankaj.roi.controllers;

import java.util.EnumSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pankaj.roi.entities.User;
import com.pankaj.roi.enums.FBPermissions;
import com.pankaj.roi.exceptions.MissingRequiredFBPermissions;
import com.pankaj.roi.exceptions.UserAlreadyPresent;
import com.pankaj.roi.models.FBUserCredentials;
import com.pankaj.roi.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;
	

    @PostMapping(path = "users")
    public ResponseEntity<?> postUser(@RequestBody FBUserCredentials user) throws MissingRequiredFBPermissions, UserAlreadyPresent {
        EnumSet<FBPermissions> requiredPermissions = userService.getAllFBPermissionsForLoadingUserNPhotosDetails();
        userService.validateAccessTokenPermissions(user, requiredPermissions);
        HttpHeaders headers = new HttpHeaders();
    	headers.set("Location", user.getFbId());
    	userService.loadFBUser(user);
        return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "users/{id}")
    public ResponseEntity<?> getUser(@RequestParam String id) {
    	Optional<User> user = userService.getUserByFbId(id);
    	if(user.isPresent())
    		return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.OK);
    	return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}