package com.pankaj.roi.exceptions;

import java.util.EnumSet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pankaj.roi.enums.FBPermissions;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingRequiredFBPermissions extends Exception {

	public MissingRequiredFBPermissions() {
	}

	public MissingRequiredFBPermissions(EnumSet<FBPermissions> requiredPermissions) {
		super("Need all of the required permission" + requiredPermissions.toString());
	}
	
	public MissingRequiredFBPermissions(String message) {
		super(message);
	}

	public MissingRequiredFBPermissions(Throwable cause) {
		super(cause);
	}

	public MissingRequiredFBPermissions(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingRequiredFBPermissions(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
