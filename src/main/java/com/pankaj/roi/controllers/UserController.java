package com.pankaj.roi.controllers;

import com.pankaj.roi.entities.User;
import com.pankaj.roi.enums.FBPermissions;
import com.pankaj.roi.models.FBUserCredentials;
import com.pankaj.roi.repositories.UsersRepository;
import com.pankaj.roi.services.UserService;
import com.sun.net.httpserver.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UsersRepository usersRepository;

    @PostMapping(path = "users")
    public ResponseEntity<?> postUser(@RequestBody FBUserCredentials user) {
        EnumSet<FBPermissions> requiredPermissions = userService.getAllFBPermissionsForLoadingUserNPhotosDetails();
        boolean isGood = userService.checkAccessTokenPermissions(user, requiredPermissions);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", user.getFbId());
        userService.loadFBUser(user);
        return new ResponseEntity<>(isGood, headers, HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "users/{id}")
    public ResponseEntity<?> getUser(@RequestParam String id) {
        User user = usersRepository.findById(1L).orElse(null);
        return new ResponseEntity<>(true, new HttpHeaders(), HttpStatus.OK);
    }
}
