package com.u9porn.utils;

import com.danikula.videocache.headers.HeaderInjector;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyHeaderInjector implements HeaderInjector {

    private HashMap<String,String> hashMap;

    @Inject
    public MyHeaderInjector() {
        this.hashMap = new HashMap<>();
    }

    @Override
    public Map<String, String> addHeaders(String url) {
        return hashMap;
    }

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }
}
