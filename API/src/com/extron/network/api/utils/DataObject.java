package com.extron.network.api.utils;

public interface DataObject {

    void set(String path, Object value);

    Object get(String path, Object def);

    String getString(String path);
}
