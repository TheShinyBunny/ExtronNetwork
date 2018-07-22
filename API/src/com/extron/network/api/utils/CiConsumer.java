package com.extron.network.api.utils;

@FunctionalInterface
public interface CiConsumer<A, B, C> {

    void accept(A a, B b, C c);

}
