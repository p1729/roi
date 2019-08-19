package com.pankaj.roi.enums;

public enum FBPermissions {
    EMAIL, USER_GENDER, USER_PHOTOS, PUBLIC_PROFILE;

    public static FBPermissions of(String permissionName) {
        switch(permissionName.toUpperCase()) {
            case "EMAIL": return EMAIL;
            case "USER_GENDER": return USER_GENDER;
            case "USER_PHOTOS": return USER_PHOTOS;
            case "PUBLIC_PROFILE": return PUBLIC_PROFILE;
            default: throw new IllegalArgumentException(permissionName + " can't be mapped to FBPermissions");
        }
    }
}
