package com.u9porn.di.component;

import android.content.Context;

import com.google.gson.Gson;
import com.u9porn.MyApplication;
import com.u9porn.cookie.CookieManager;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.User;
import com.u9porn.di.ApplicationContext;
import com.u9porn.di.module.ApiServiceModule;
import com.u9porn.di.module.ApplicationModule;
import com.u9porn.utils.AddressHelper;
import com.u9porn.utils.DownloadManager;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
//@Singleton
//@Component(modules = {ApplicationModule.class, ApiServiceModule.class})
public interface ApplicationComponent {
    void inject(MyApplication myApplication);

    void inject(DownloadManager downloadManager);

    //@ApplicationContext
    Context getContext();

    DataManager getDataManager();
}
