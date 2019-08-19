package com.pankaj.roi.models;

import com.pankaj.roi.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class PagingRequest<T extends User> extends Paging {
    private Class<T> clazz;
}
