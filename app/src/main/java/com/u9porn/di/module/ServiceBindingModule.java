package com.u9porn.di.module;

import com.u9porn.di.PerService;
import com.u9porn.service.DownloadVideoModule;
import com.u9porn.service.DownloadVideoService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBindingModule {
    @PerService
    @ContributesAndroidInjector(modules = DownloadVideoModule.class)
    abstract DownloadVideoService downloadVideoService();
}
