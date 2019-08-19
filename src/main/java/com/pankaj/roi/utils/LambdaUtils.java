package com.pankaj.roi.utils;


import java.util.function.Function;

public class LambdaUtils {

    public static <T, R, E extends Exception> Function<T, R>
    expValIfExpWrapper(Function<T,R> f, Class<E> clazz, R expVal) {
        return arg -> {
            try {
                return f.apply(arg);
            } catch(Exception e) {
                try {
                    clazz.cast(e);
                    return expVal;
                } catch (ClassCastException ccExp) {
                    throw e;
                }
            }
        };
    }
}