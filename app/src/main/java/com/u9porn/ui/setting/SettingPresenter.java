package com.u9porn.ui.setting;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.cookie.CookieManager;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.User;
import com.u9porn.data.network.Api;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RxSchedulersHelper;
import com.u9porn.ui.MvpBasePresenter;
import com.u9porn.ui.porn9video.search.SearchPresenter;
import com.u9porn.utils.SDCardUtils;
import com.u9porn.utils.UserHelper;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cn.qqtheme.framework.util.FileUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

/**
 * @author flymegoc
 * @date 2018/2/6
 */

public class SettingPresenter extends MvpBasePresenter<SettingView> implements ISetting {

    private static final String TAG = SearchPresenter.class.getSimpleName();
    private DataManager dataManager;

    @Inject
    public SettingPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        super(provider);
        this.dataManager = dataManager;
    }

    @Override
    public void test9PornVideo(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        // 全局 BaseUrl 的优先级低于 Domain-Name header 中单独配置的,其他未配置的接口将受全局 BaseUrl 的影响
        RetrofitUrlManager.getInstance().putDomain(Api.PORN9_VIDEO_DOMAIN_NAME, baseUrl);
        dataManager.testPorn9VideoAddress()
                .compose(RxSchedulersHelper.<Boolean>ioMainThread())
                .compose(provider.<Boolean>bindToLifecycle())
                .subscribe(new CallBackWrapper<Boolean>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTestingAddressDialog(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final Boolean s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                if (s) {
                                    view.testNewAddressSuccess("测试成功", qmuiCommonListItemView, key);
                                } else {
                                    view.testNewAddressSuccess("测试失败，可以访问，但无法获取正确的数据", qmuiCommonListItemView, key);
                                }

                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }


    @Override
    public void test9PornForum(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        RetrofitUrlManager.getInstance().putDomain(Api.PORN9_FORUM_DOMAIN_NAME, baseUrl);
        dataManager.testPorn9ForumAddress()
                .compose(RxSchedulersHelper.<Boolean>ioMainThread())
                .compose(provider.<Boolean>bindToLifecycle())
                .subscribe(new CallBackWrapper<Boolean>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTestingAddressDialog(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final Boolean s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                if (s) {
                                    view.testNewAddressSuccess("测试成功", qmuiCommonListItemView, key);
                                } else {
                                    view.testNewAddressSuccess("测试失败，可以访问，但无法获取正确的数据", qmuiCommonListItemView, key);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }

    @Override
    public void testPav(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        RetrofitUrlManager.getInstance().putDomain(Api.PA_DOMAIN_NAME, baseUrl);
        dataManager.testPavAddress(baseUrl)
                .compose(RxSchedulersHelper.<Boolean>ioMainThread())
                .compose(provider.<Boolean>bindToLifecycle())
                .subscribe(new CallBackWrapper<Boolean>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTestingAddressDialog(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final Boolean s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                if (s) {
                                    view.testNewAddressSuccess("测试成功", qmuiCommonListItemView, key);
                                } else {
                                    view.testNewAddressSuccess("测试失败，可以访问，但无法获取正确的数据", qmuiCommonListItemView, key);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }

    @Override
    public void testAxgle(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        RetrofitUrlManager.getInstance().putDomain(Api.AXGLE_DOMAIN_NAME, baseUrl);
        dataManager.testAxgle()
                .compose(RxSchedulersHelper.<Boolean>ioMainThread())
                .compose(provider.<Boolean>bindToLifecycle())
                .subscribe(new CallBackWrapper<Boolean>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTestingAddressDialog(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final Boolean s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                if (s) {
                                    view.testNewAddressSuccess("测试成功", qmuiCommonListItemView, key);
                                } else {
                                    view.testNewAddressSuccess("测试失败，可以访问，但无法获取正确的数据", qmuiCommonListItemView, key);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }

    @Override
    public boolean isHaveUnFinishDownloadVideo() {
        return dataManager.loadDownloadingData().size() != 0;
    }

    @Override
    public boolean isHaveFinishDownloadVideoFile() {
        if (!TextUtils.isEmpty(dataManager.getCustomDownloadVideoDirPath())) {
            File file = new File(dataManager.getCustomDownloadVideoDirPath());
            return file.listFiles() != null && file.listFiles().length != 0;
        }
        File file = new File(SDCardUtils.DOWNLOAD_VIDEO_PATH);
        //检查是否有MP4文件
        for (File file1 : file.listFiles()) {
            if (file1.getName().endsWith(".mp4")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void moveOldDownloadVideoToNewDir(final String newDirPath, final QMUICommonListItemView qmuiCommonListItemView) {
        Observable.fromCallable(new Callable<File[]>() {
            @Override
            public File[] call() throws Exception {
                File file = new File(dataManager.getCustomDownloadVideoDirPath());
                return file.listFiles();
            }
        }).flatMap(new Function<File[], ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(File[] files) throws Exception {
                return Observable.fromArray(files);
            }
        }).filter(new Predicate<File>() {
            @Override
            public boolean test(File file) throws Exception {
                return file.getName().endsWith(".mp4");
            }
        }).map(new Function<File, String>() {
            @Override
            public String apply(File file) throws Exception {
                FileUtils.move(file, new File(newDirPath, file.getName()));
                return file.getAbsolutePath();
            }
        }).delay(1, TimeUnit.SECONDS)
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.beginMoveOldDirDownloadVideoToNewDir();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        Logger.t(TAG).d("正在移动到：" + s);
                    }

                    @Override
                    public void onError(String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.setNewDownloadVideoDirError("移动文件失败，无法设置新目录");
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                dataManager.setCustomDownloadVideoDirPath(newDirPath);
                                qmuiCommonListItemView.setDetailText(newDirPath);
                                view.setNewDownloadVideoDirSuccess("移动文件完成,设置新目录成功");
                            }
                        });
                    }
                });
    }

    @Override
    public boolean isUserLogin() {
        return dataManager.isUserLogin();
    }

    @Override
    public void existLogin() {
        dataManager.existLogin();
    }

    @Override
    public int getPlaybackEngine() {
        return dataManager.getPlaybackEngine();
    }

    @Override
    public void setPlaybackEngine(int playbackEngine) {
        dataManager.setPlaybackEngine(playbackEngine);
    }

    @Override
    public void setPorn9VideoAddress(String porn9VideoAddress) {
        dataManager.setPorn9VideoAddress(porn9VideoAddress);
    }

    @Override
    public void setPorn9ForumAddress(String porn9ForumAddress) {
        dataManager.setPorn9ForumAddress(porn9ForumAddress);
    }

    @Override
    public void setPavAddress(String pavAddress) {
        dataManager.setPavAddress(pavAddress);
    }

    @Override
    public void setCustomDownloadVideoDirPath(String newDirPath) {
        dataManager.setCustomDownloadVideoDirPath(newDirPath);
    }

    @Override
    public String getCustomDownloadVideoDirPath() {
        return dataManager.getCustomDownloadVideoDirPath();
    }

    @Override
    public boolean isForbiddenAutoReleaseMemory() {
        return dataManager.isForbiddenAutoReleaseMemory();
    }

    @Override
    public void setForbiddenAutoReleaseMemory(boolean forbiddenAutoReleaseMemory) {
        dataManager.setForbiddenAutoReleaseMemory(forbiddenAutoReleaseMemory);
    }

    @Override
    public boolean isDownloadVideoNeedWifi() {
        return dataManager.isDownloadVideoNeedWifi();
    }

    @Override
    public void setDownloadVideoNeedWifi(boolean downloadVideoNeedWifi) {
        dataManager.setDownloadVideoNeedWifi(downloadVideoNeedWifi);
    }

    @Override
    public boolean isOpenSkipPage() {
        return dataManager.isOpenSkipPage();
    }

    @Override
    public void setOpenSkipPage(boolean openSkipPage) {
        dataManager.setOpenSkipPage(openSkipPage);
    }

    @Override
    public String getVideo9PornAddress() {
        return dataManager.getPorn9VideoAddress();
    }

    @Override
    public String getForum9PornAddress() {
        return dataManager.getPorn9ForumAddress();
    }

    @Override
    public String getPavAddress() {
        return dataManager.getPavAddress();
    }

    @Override
    public boolean isShowUrlRedirectTipDialog() {
        return dataManager.isShowUrlRedirectTipDialog();
    }

    @Override
    public void setShowUrlRedirectTipDialog(boolean showUrlRedirectTipDialog) {
        dataManager.setShowUrlRedirectTipDialog(showUrlRedirectTipDialog);
    }

    @Override
    public void setAxgleAddress(String address) {
        dataManager.setAxgleAddress(address);
    }

    @Override
    public String getAxgleAddress() {
        return dataManager.getAxgleAddress();
    }
}
