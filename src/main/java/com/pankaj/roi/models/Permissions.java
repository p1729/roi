package com.pankaj.roi.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Permissions {
    private List<Permission> data = new ArrayList<>();

    public void setData(List<Permission> data) {
        if(!Objects.isNull(data))
            this.data.addAll(data);
    }
}
