package com.extron.network.api.utils;

@FunctionalInterface
public interface CiPredicate<T,A,B> {

    boolean test(T t, A a, B b);

}
