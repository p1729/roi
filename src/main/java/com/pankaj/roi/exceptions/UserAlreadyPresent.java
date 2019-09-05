package com.pankaj.roi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyPresent extends Exception {

	public UserAlreadyPresent(String message) {
		super(message);
	}
}
