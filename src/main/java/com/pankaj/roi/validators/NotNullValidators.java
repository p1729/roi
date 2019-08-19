package com.pankaj.roi.validators;

import java.util.Objects;

public class NotNullValidators {

    public static void validateNotNull(final Object value, final String name) {
        if(Objects.isNull(value))
            throw new IllegalArgumentException(name + " must not be null");
    }
}
