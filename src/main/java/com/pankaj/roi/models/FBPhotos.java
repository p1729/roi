package com.pankaj.roi.models;

import com.pankaj.roi.entities.Photo;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class FBPhotos {
    private List<FBPhotoData> data = new ArrayList<>();
    private Paging paging;

    public void setData(List<FBPhotoData> data) {
        if(!Objects.isNull(data))
            this.data.addAll(data);
    }


}