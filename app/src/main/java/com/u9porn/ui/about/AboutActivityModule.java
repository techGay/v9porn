package com.u9porn.ui.about;

import android.arch.lifecycle.Lifecycle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.ui.download.DownloadActivity;

import dagger.Module;
import dagger.Provides;
@Module
public class AboutActivityModule {

    @Provides
    AppCompatActivity provideAppCompatActivity(AboutActivity aboutActivity){
        return aboutActivity;
    }

    @Provides
    static LifecycleProvider<Lifecycle.Event> providerLifecycleProvider(AppCompatActivity mAppCompatActivity) {
        return AndroidLifecycle.createLifecycleProvider(mAppCompatActivity);
    }
}
