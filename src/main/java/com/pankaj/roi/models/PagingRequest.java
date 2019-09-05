package com.pankaj.roi.models;

import com.pankaj.roi.entities.Photo;
import com.pankaj.roi.entities.User;
import lombok.Data;

@Data
public class PagingRequest<T extends FBPhotos> extends Paging {
    private Class<T> clazz;
    private User persistedUser;
    private Photo persistedPhoto;

    public PagingRequest(Class<T> clazz, User user, Photo photo, Paging page) {
        super(page.getNext(), page.getPrevious());
        this.clazz = clazz;
        this.persistedPhoto = photo;
        this.persistedUser = user;
    }
}
