package com.pankaj.roi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyPresent extends Exception {

	public UserAlreadyPresent() {
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyPresent(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyPresent(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyPresent(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyPresent(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
