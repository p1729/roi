package com.pankaj.roi.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class FBPhotos {
    private List<FBPhotoData> data = new ArrayList<>();
    private Paging paging;

    public void setData(List<FBPhotoData> data) {
        if(!Objects.isNull(data))
            this.data.addAll(data);
    }


}