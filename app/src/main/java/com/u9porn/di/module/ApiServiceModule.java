package com.u9porn.di.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.orhanobut.logger.Logger;
import com.u9porn.cookie.SetCookieCache;
import com.u9porn.cookie.SharedPrefsCookiePersistor;
import com.u9porn.data.network.Api;
import com.u9porn.data.network.apiservice.*;
import com.u9porn.data.network.okhttp.CommonHeaderInterceptor;
import com.u9porn.data.network.okhttp.MyProxySelector;
import com.u9porn.di.ApplicationContext;
import com.u9porn.utils.AddressHelper;
import dagger.Module;
import dagger.Provides;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.inject.Singleton;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2018/2/10
 */
@Module
public class ApiServiceModule {

    private static final String TAG = ApiServiceModule.class.getSimpleName();

    @Singleton
    @Provides
    SharedPrefsCookiePersistor providesSharedPrefsCookiePersistor(@ApplicationContext Context context) {
        return new SharedPrefsCookiePersistor(context);
    }

    @Singleton
    @Provides
    SetCookieCache providesSetCookieCache() {
        return new SetCookieCache();
    }

    @Singleton
    @Provides
    PersistentCookieJar providesPersistentCookieJar(SharedPrefsCookiePersistor sharedPrefsCookiePersistor, SetCookieCache setCookieCache) {
        return new PersistentCookieJar(setCookieCache, sharedPrefsCookiePersistor);
    }

    @Singleton
    @Provides
    RulerCookie providesRuler(SharedPrefsCookiePersistor sharedPrefsCookiePersistor, SetCookieCache setCookieCache){
        return new RulerCookie(setCookieCache,sharedPrefsCookiePersistor);
    }

    @Singleton
    @Provides
    HttpLoggingInterceptor providesHttpLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Logger.t(TAG).d("HttpLog:" + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return logging;
    }

    @Singleton
    @Provides
    List<Proxy> providesListProxy() {
        return new ArrayList<>();
    }

    @Singleton
    @Provides
    OkHttpClient providesOkHttpClient(CommonHeaderInterceptor commonHeaderInterceptor, HttpLoggingInterceptor httpLoggingInterceptor, RulerCookie rulerCookie, MyProxySelector myProxySelector, AddressHelper addressHelper) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(commonHeaderInterceptor);
        builder.addInterceptor(httpLoggingInterceptor);
        builder.cookieJar(rulerCookie);
        builder.proxySelector(myProxySelector);
        //动态baseUrl
        RetrofitUrlManager.getInstance().putDomain(Api.GITHUB_DOMAIN_NAME, Api.APP_GITHUB_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(Api.MEI_ZI_TU_DOMAIN_NAME, Api.APP_MEIZITU_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(Api.MM_99_DOMAIN_NAME, Api.APP_99_MM_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(Api.XICI_DAILI_DOMAIN_NAME, Api.APP_PROXY_XICI_DAILI_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(Api.HUA_BAN_DOMAIN_NAME, Api.APP_HUA_BAN_DOMAIN);
        if (!TextUtils.isEmpty(addressHelper.getVideo9PornAddress())) {
            RetrofitUrlManager.getInstance().putDomain(Api.PORN9_VIDEO_DOMAIN_NAME, addressHelper.getVideo9PornAddress());
        }
        if (!TextUtils.isEmpty(addressHelper.getForum9PornAddress())) {
            RetrofitUrlManager.getInstance().putDomain(Api.PORN9_FORUM_DOMAIN_NAME, addressHelper.getForum9PornAddress());
        }
        if (!TextUtils.isEmpty(addressHelper.getPavAddress())) {
            RetrofitUrlManager.getInstance().putDomain(Api.PA_DOMAIN_NAME, addressHelper.getPavAddress());
        }
        if (!TextUtils.isEmpty(addressHelper.getAxgleAddress())) {
            RetrofitUrlManager.getInstance().putDomain(Api.AXGLE_DOMAIN_NAME, addressHelper.getAxgleAddress());
        }
        return RetrofitUrlManager.getInstance().with(builder).build();
    }

    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Api.APP_GITHUB_DOMAIN)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    GitHubServiceApi providesGitHubServiceApi(Retrofit retrofit) {
        return retrofit.create(GitHubServiceApi.class);
    }

    @Singleton
    @Provides
    MeiZiTuServiceApi providesMeiZiTuServiceApi(Retrofit retrofit) {
        return retrofit.create(MeiZiTuServiceApi.class);
    }

    @Singleton
    @Provides
    Mm99ServiceApi providesMm99ServiceApi(Retrofit retrofit) {
        return retrofit.create(Mm99ServiceApi.class);
    }

    @Singleton
    @Provides
    V9PornServiceApi provides91PornVideoServiceApi(Retrofit retrofit) {
        return retrofit.create(V9PornServiceApi.class);
    }

    @Singleton
    @Provides
    Forum9PronServiceApi provides91PornForumServiceApi(Retrofit retrofit) {
        return retrofit.create(Forum9PronServiceApi.class);
    }

    @Singleton
    @Provides
    PavServiceApi providesPigAvServiceApi(Retrofit retrofit) {
        return retrofit.create(PavServiceApi.class);
    }

    @Singleton
    @Provides
    ProxyServiceApi providesProxyServiceApi(Retrofit retrofit) {
        return retrofit.create(ProxyServiceApi.class);
    }

    @Singleton
    @Provides
    HuaBanServiceApi providesHuaBanServiceApi(Retrofit retrofit) {
        return retrofit.create(HuaBanServiceApi.class);
    }

    @Singleton
    @Provides
    AxgleServiceApi providesAxgleServiceApi(Retrofit retrofit) {
        return retrofit.create(AxgleServiceApi.class);
    }
}
