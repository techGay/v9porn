package com.u9porn.ui.porn9video.play;

import android.arch.lifecycle.Lifecycle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.di.PerFragment;
import com.u9porn.ui.porn9video.author.AuthorFragment;
import com.u9porn.ui.porn9video.comment.CommentFragment;
import com.u9porn.ui.porn9video.favorite.FavoriteFragment;
import com.u9porn.ui.porn9video.index.IndexFragment;
import com.u9porn.ui.porn9video.videolist.VideoListFragment;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class JiaoZiVideoPlayerModule {
    @PerFragment
    @ContributesAndroidInjector
    abstract AuthorFragment authorFragment();


    @PerFragment
    @ContributesAndroidInjector
    abstract FavoriteFragment favoriteFragment();

    @PerFragment
    @ContributesAndroidInjector
    abstract CommentFragment commentFragment();

    @PerFragment
    @ContributesAndroidInjector
    abstract IndexFragment indexFragment();

    @PerFragment
    @ContributesAndroidInjector
    abstract VideoListFragment videoListFragment();

    @Provides
    static AppCompatActivity provideAppCompatActivity(JiaoZiVideoPlayerActivity jiaoZiVideoPlayerActivity){
        return jiaoZiVideoPlayerActivity;
    }

    @Provides
    static LifecycleProvider<Lifecycle.Event> providerLifecycleProvider(AppCompatActivity mAppCompatActivity) {
        return AndroidLifecycle.createLifecycleProvider(mAppCompatActivity);
    }

    @Provides
    static FragmentManager providesSupportFragmentManager(AppCompatActivity mAppCompatActivity) {
        return mAppCompatActivity.getSupportFragmentManager();
    }
}
