package com.pankaj.roi.models;

import lombok.Data;

@Data
public class FBUser {
    private String id;
    private String email;
    private String name;
    private String gender;
    private Picture picture;
}
