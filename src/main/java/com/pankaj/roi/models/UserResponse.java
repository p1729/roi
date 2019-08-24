package com.pankaj.roi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String name;
    private String gender;
    private String email;
    @JsonProperty("profile_pic_url")
    private String profilePicUrl;
}
