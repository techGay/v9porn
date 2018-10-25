package com.u9porn.ui.porn9forum.browse9forum;

import android.arch.lifecycle.Lifecycle;
import android.support.v7.app.AppCompatActivity;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.ui.about.AboutActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class Browse9PForumActivityModule {
    @Provides
    AppCompatActivity provideAppCompatActivity(Browse9PForumActivity browse9PForumActivity){
        return browse9PForumActivity;
    }

    @Provides
    static LifecycleProvider<Lifecycle.Event> providerLifecycleProvider(AppCompatActivity mAppCompatActivity) {
        return AndroidLifecycle.createLifecycleProvider(mAppCompatActivity);
    }
}
