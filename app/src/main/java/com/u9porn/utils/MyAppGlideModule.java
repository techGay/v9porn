package com.u9porn.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author flymegoc
 * @date 2018/1/14
 */

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 250;
        builder.setDiskCache(new DiskLruCacheFactory(AppCacheUtils.getGlideDiskCacheDir(context).getAbsolutePath(), diskCacheSizeBytes));
    }
}
