package com.u9porn.di.module;

import com.u9porn.di.PerActivity;
import com.u9porn.ui.about.AboutActivity;
import com.u9porn.ui.about.AboutActivityModule;
import com.u9porn.ui.axgle.play.AxglePlayActivity;
import com.u9porn.ui.axgle.play.AxglePlayActivityModule;
import com.u9porn.ui.axgle.search.SearchAxgleVideoActivity;
import com.u9porn.ui.axgle.search.SearchAxgleVideoActivityModule;
import com.u9porn.ui.download.DownloadActivity;
import com.u9porn.ui.download.DownloadActivityModule;
import com.u9porn.ui.google.GoogleRecaptchaVerifyActivity;
import com.u9porn.ui.google.GoogleRecaptchaVerifyActivityModule;
import com.u9porn.ui.images.viewimage.PictureViewerActivity;
import com.u9porn.ui.images.viewimage.PictureViewerActivityModule;
import com.u9porn.ui.main.MainActivity;
import com.u9porn.ui.main.MainActivityModule;
import com.u9porn.ui.porn9forum.browse9forum.Browse9PForumActivity;
import com.u9porn.ui.porn9forum.browse9forum.Browse9PForumActivityModule;
import com.u9porn.ui.porn9video.author.AuthorActivity;
import com.u9porn.ui.porn9video.author.AuthorActivityModule;
import com.u9porn.ui.porn9video.favorite.FavoriteActivity;
import com.u9porn.ui.porn9video.favorite.FavoriteActivityModule;
import com.u9porn.ui.porn9video.history.HistoryActivity;
import com.u9porn.ui.porn9video.history.HistoryActivityModule;
import com.u9porn.ui.porn9video.play.ExoMediaPlayerActivity;
import com.u9porn.ui.porn9video.play.ExoPlayerVideoModule;
import com.u9porn.ui.porn9video.play.JiaoZiVideoPlayerActivity;
import com.u9porn.ui.porn9video.play.JiaoZiVideoPlayerModule;
import com.u9porn.ui.porn9video.search.SearchActivity;
import com.u9porn.ui.porn9video.search.SearchActivityModule;
import com.u9porn.ui.porn9video.user.UserLoginActivity;
import com.u9porn.ui.porn9video.user.UserLoginActivityModule;
import com.u9porn.ui.porn9video.user.UserRegisterActivity;
import com.u9porn.ui.porn9video.user.UserRegisterActivityModule;
import com.u9porn.ui.proxy.ProxySettingActivity;
import com.u9porn.ui.proxy.ProxySettingActivityModule;
import com.u9porn.ui.pxgav.playpxgav.PlayPxgavActivity;
import com.u9porn.ui.pxgav.playpxgav.PlayPxgavActivityModule;
import com.u9porn.ui.setting.SettingActivity;
import com.u9porn.ui.setting.SettingActivityModule;
import com.u9porn.ui.splash.SplashActivity;
import com.u9porn.ui.splash.SplashActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * @author megoc
 */
@Module
public abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity mainActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = SplashActivityModule.class)
    abstract SplashActivity splashActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = DownloadActivityModule.class)
    abstract DownloadActivity downloadActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = SettingActivityModule.class)
    abstract SettingActivity settingActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = AboutActivityModule.class)
    abstract AboutActivity aboutActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = FavoriteActivityModule.class)
    abstract FavoriteActivity favoriteActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = SearchActivityModule.class)
    abstract SearchActivity searchActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = ExoPlayerVideoModule.class)
    abstract ExoMediaPlayerActivity exoMediaPlayerActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = JiaoZiVideoPlayerModule.class)
    abstract JiaoZiVideoPlayerActivity jiaoZiVideoPlayerActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = UserLoginActivityModule.class)
    abstract UserLoginActivity userLoginActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = UserRegisterActivityModule.class)
    abstract UserRegisterActivity userRegisterActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = AuthorActivityModule.class)
    abstract AuthorActivity authorActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = ProxySettingActivityModule.class)
    abstract ProxySettingActivity proxySettingActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = PlayPxgavActivityModule.class)
    abstract PlayPxgavActivity playPavActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = PictureViewerActivityModule.class)
    abstract PictureViewerActivity pictureViewerActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = Browse9PForumActivityModule.class)
    abstract Browse9PForumActivity browse9PForumActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = GoogleRecaptchaVerifyActivityModule.class)
    abstract GoogleRecaptchaVerifyActivity googleRecaptchaVerifyActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = HistoryActivityModule.class)
    abstract HistoryActivity historyActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = AxglePlayActivityModule.class)
    abstract AxglePlayActivity axglePlayActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = SearchAxgleVideoActivityModule.class)
    abstract SearchAxgleVideoActivity searchAxgleVideoActivity();
}
