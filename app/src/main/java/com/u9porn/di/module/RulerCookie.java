package com.u9porn.di.module;

import android.text.TextUtils;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RulerCookie extends PersistentCookieJar {
    private String VIDEO_PATH = "/view_video.php";
    //登录信息cookie
    private List<String> BLACK_COOKIE = Arrays.asList("USERNAME","user_level","level","EMAILVERIFIED","DUID");

    public RulerCookie(CookieCache cache, CookiePersistor persistor) {
        super(cache, persistor);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        String host = url.host();
        String path = url.encodedPath();
        if (!TextUtils.equals(VIDEO_PATH, path)) {
            return super.loadForRequest(url);
        }else {
            //请求视频信息的时候去除登录信息
            List<Cookie> requestCookies = new ArrayList<>();
            List<Cookie> cookies = super.loadForRequest(url);
            for (Cookie cookie : cookies) {
                String value = cookie.toString();
                boolean useful = true;
                for (String blackName: BLACK_COOKIE) {
                    if (value.contains(blackName)){
                        useful = false;
                        break;
                    }
                }
                if (useful){
                    requestCookies.add(cookie);
                }
            }
            return requestCookies;
        }
    }
}
