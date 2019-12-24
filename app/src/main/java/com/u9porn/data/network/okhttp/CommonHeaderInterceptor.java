package com.u9porn.data.network.okhttp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u9porn.data.network.Api;
import com.u9porn.data.prefs.PreferencesHelper;
import com.u9porn.eventbus.UrlRedirectEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author flymegoc
 * @date 2018/1/17
 */

@Singleton
public class CommonHeaderInterceptor implements Interceptor {

    private static final String TAG = CommonHeaderInterceptor.class.getSimpleName();
    private PreferencesHelper preferencesHelper;

    @Inject
    public CommonHeaderInterceptor(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        //统一设置请求头
        Request original = chain.request();
        String header = original.header("Domain-Name");

        //如果是可能被重定向的header
        if (!TextUtils.isEmpty(header) && Objects.equals(header, Api.PORN9_VIDEO_DOMAIN_NAME)) {
            //返回的地址
            Response response = chain.proceed(original);
            HttpUrl httpUrl = response.request().url();
            //读取本地地址
            String url = preferencesHelper.getPorn9VideoAddress();
            HttpUrl oldHttpUrl = HttpUrl.parse(url);
            //如果不相等则可能被重定向了
            if (oldHttpUrl != null && !oldHttpUrl.host().equals(httpUrl.host())) {
                HttpUrl newHttpUrl = new HttpUrl.Builder().scheme(httpUrl.scheme()).host(httpUrl.host()).build();
                String urlStr = newHttpUrl.toString();
                Logger.t(TAG).e("连接被重定向为:" + urlStr);
                //更新为最新地址
                RetrofitUrlManager.getInstance().putDomain(Api.PORN9_VIDEO_DOMAIN_NAME, urlStr);
                if (preferencesHelper.isShowUrlRedirectTipDialog()) {
                    EventBus.getDefault().post(new UrlRedirectEvent(url, urlStr, Api.PORN9_VIDEO_DOMAIN_NAME));
                }
            }
        } else if (!TextUtils.isEmpty(header) && Objects.equals(header, Api.PORN9_FORUM_DOMAIN_NAME)) {
            //返回的地址
            Response response = chain.proceed(original);
            HttpUrl httpUrl = response.request().url();
            //读取本地地址
            String url = preferencesHelper.getPorn9ForumAddress();
            HttpUrl oldHttpUrl = HttpUrl.parse(url);
            //如果不相等则可能被重定向了
            if (oldHttpUrl != null && !oldHttpUrl.host().equals(httpUrl.host())) {
                HttpUrl newHttpUrl = new HttpUrl.Builder().scheme(httpUrl.scheme()).host(httpUrl.host()).build();
                String urlStr = newHttpUrl.toString();
                Logger.t(TAG).e("连接被重定向为:" + urlStr);
                //更新为最新地址
                RetrofitUrlManager.getInstance().putDomain(Api.PORN9_FORUM_DOMAIN_NAME, urlStr);
                if (preferencesHelper.isShowUrlRedirectTipDialog()) {
                    EventBus.getDefault().post(new UrlRedirectEvent(url, urlStr, Api.PORN9_FORUM_DOMAIN_NAME));
                }
            }
        }

        Request.Builder requestBuilder = original.newBuilder();
        requestBuilder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        requestBuilder.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5");
        requestBuilder.header("Proxy-Connection", "keep-alive");
        requestBuilder.header("Cache-Control", "max-age=0");

        requestBuilder.method(original.method(), original.body());

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
