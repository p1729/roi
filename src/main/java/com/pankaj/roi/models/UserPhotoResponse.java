package com.pankaj.roi.models;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class UserPhotoResponse {

    private List<PhotoResponse> photos = new ArrayList<>();

    public void setPhotos(List<PhotoResponse> photos) {
        if (Objects.nonNull(photos))
            this.photos.addAll(photos);
    }
}
