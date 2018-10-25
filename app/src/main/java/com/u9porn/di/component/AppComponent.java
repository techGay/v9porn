package com.u9porn.di.component;

import android.app.Application;

import com.u9porn.MyApplication;
import com.u9porn.di.module.ActivityBindingModule;
import com.u9porn.di.module.ApiServiceModule;
import com.u9porn.di.module.ApplicationModule;
import com.u9porn.di.module.ServiceBindingModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component( modules = {ApplicationModule.class, ApiServiceModule.class, ActivityBindingModule.class,ServiceBindingModule.class, AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<MyApplication> {

    @Override
    void inject(MyApplication instance);

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
