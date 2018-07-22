package com.extron.network.api.utils;

public interface Savable<T> extends Loadable<T> {

    void save();

}
