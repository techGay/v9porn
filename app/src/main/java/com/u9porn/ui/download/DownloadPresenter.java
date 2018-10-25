package com.u9porn.ui.download;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.db.entity.VideoResult;
import com.u9porn.di.ApplicationContext;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RxSchedulersHelper;
import com.u9porn.utils.AppCacheUtils;
import com.u9porn.utils.DownloadManager;
import com.u9porn.utils.VideoCacheFileNameGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.common.io.FileUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public class DownloadPresenter extends MvpBasePresenter<DownloadView> implements IDownload {

    private DataManager dataManager;
    private LifecycleProvider<Lifecycle.Event> provider;
    private Context context;

    @Inject
    public DownloadPresenter(DataManager dataManager, LifecycleProvider<Lifecycle.Event> provider, @ApplicationContext Context context) {
        this.dataManager = dataManager;
        this.provider = provider;
        this.context = context;
    }

    @Override
    public void favorite(String uId, String videoId, String ownnerId) {

    }

    @Override
    public void downloadVideo(V9PornItem v9PornItem, boolean isForceReDownload) {
        downloadVideo(v9PornItem, isForceReDownload, null);
    }

    @Override
    public void downloadVideo(V9PornItem v9PornItem, boolean isForceReDownload, DownloadListener downloadListener) {
        V9PornItem tmp = dataManager.findV9PornItemByViewKey(v9PornItem.getViewKey());
        if (tmp == null || tmp.getVideoResultId() == 0) {
            if (downloadListener != null) {
                downloadListener.onError("还未解析成功视频地址");
            } else {
                ifViewAttached(new ViewAction<DownloadView>() {
                    @Override
                    public void run(@NonNull DownloadView view) {
                        view.showMessage("还未解析成功视频地址", TastyToast.WARNING);
                    }
                });
            }
            return;
        }
        VideoResult videoResult = tmp.getVideoResult();
        //先检查文件
        File toFile = new File(tmp.getDownLoadPath(getCustomDownloadVideoDirPath()));
        if (toFile.exists() && toFile.length() > 0) {
            if (downloadListener != null) {
                downloadListener.onError("已经下载过了，请查看下载目录");
            } else {
                ifViewAttached(new ViewAction<DownloadView>() {
                    @Override
                    public void run(@NonNull DownloadView view) {
                        view.showMessage("已经下载过了，请查看下载目录", TastyToast.INFO);
                    }
                });
            }
            return;
        }
        //如果已经缓存完成，直接使用缓存代理完成
        if (dataManager.isVideoCacheByProxy(videoResult.getVideoUrl())) {
            try {
                copyCacheFile(AppCacheUtils.getVideoCacheDir(context), tmp, downloadListener);
            } catch (IOException e) {
                if (downloadListener != null) {
                    downloadListener.onError("缓存文件错误，无法拷贝");
                } else {
                    ifViewAttached(new ViewAction<DownloadView>() {
                        @Override
                        public void run(@NonNull DownloadView view) {
                            view.showMessage("缓存文件错误，无法拷贝", TastyToast.ERROR);
                        }
                    });
                }
                e.printStackTrace();
            }
            return;
        }
        //检查当前状态
        if (tmp.getStatus() == FileDownloadStatus.progress && tmp.getDownloadId() != 0 && !isForceReDownload) {
            if (downloadListener != null) {
                downloadListener.onError("已经在下载了");
            } else {
                ifViewAttached(new ViewAction<DownloadView>() {
                    @Override
                    public void run(@NonNull DownloadView view) {
                        view.showMessage("已经在下载了", TastyToast.SUCCESS);
                    }
                });
            }
            return;
        }
        Logger.d("视频连接：" + videoResult.getVideoUrl());
        String path = v9PornItem.getDownLoadPath(getCustomDownloadVideoDirPath());
        Logger.d(path);
        boolean isDownloadNeedWifi = dataManager.isDownloadVideoNeedWifi();
        int id = DownloadManager.getImpl().startDownload(videoResult.getVideoUrl(), path, isDownloadNeedWifi, isForceReDownload);
        if (tmp.getAddDownloadDate() == null) {
            tmp.setAddDownloadDate(new Date());
        }
        tmp.setDownloadId(id);
        dataManager.updateV9PornItem(tmp);
        if (downloadListener != null) {
            downloadListener.onSuccess("开始下载");
        } else {
            ifViewAttached(new ViewAction<DownloadView>() {
                @Override
                public void run(@NonNull DownloadView view) {
                    view.showMessage("开始下载", TastyToast.SUCCESS);
                }
            });
        }
    }

    @Override
    public void loadDownloadingData() {

        Observable
                .create(new ObservableOnSubscribe<List<V9PornItem>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<V9PornItem>> emitter) throws Exception {
                        List<V9PornItem> v9PornItems = dataManager.loadDownloadingData();
                        emitter.onNext(v9PornItems);
                        emitter.onComplete();
                    }
                })
                .compose(RxSchedulersHelper.<List<V9PornItem>>ioMainThread())
                .compose(provider.<List<V9PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<V9PornItem>>() {
                    @Override
                    public void onSuccess(final List<V9PornItem> v9PornItemList) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.setDownloadingData(v9PornItemList);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });

    }

    @Override
    public void loadFinishedData() {

        Observable
                .create(new ObservableOnSubscribe<List<V9PornItem>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<V9PornItem>> emitter) throws Exception {
                        List<V9PornItem> v9PornItems = dataManager.loadFinishedData();
                        emitter.onNext(v9PornItems);
                        emitter.onComplete();
                    }
                })
                .compose(RxSchedulersHelper.<List<V9PornItem>>ioMainThread())
                .compose(provider.<List<V9PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<V9PornItem>>() {
                    @Override
                    public void onSuccess(final List<V9PornItem> v9PornItemList) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.setFinishedData(v9PornItemList);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void deleteDownloadingTask(V9PornItem v9PornItem) {
        FileDownloader.getImpl().clear(v9PornItem.getDownloadId(), v9PornItem.getDownLoadPath(getCustomDownloadVideoDirPath()));
        v9PornItem.setDownloadId(0);
        dataManager.updateV9PornItem(v9PornItem);
    }

    @Override
    public void deleteDownloadedTask(V9PornItem v9PornItem, boolean isDeleteFile) {
        if (!isDeleteFile) {
            deleteWithoutFile(v9PornItem);
        } else {
            deleteWithFile(v9PornItem);
        }
    }

    @Override
    public V9PornItem findUnLimit91PornItemByDownloadId(int downloadId) {
        return dataManager.findV9PornItemByDownloadId(downloadId);
    }

    @Override
    public List<V9PornItem> loadDownloadingDatas() {
        return dataManager.loadDownloadingData();
    }

    @Override
    public void updateV9PornItem(V9PornItem v9PornItem) {
        dataManager.updateV9PornItem(v9PornItem);
    }

    @Override
    public String getCustomDownloadVideoDirPath() {
        return dataManager.getCustomDownloadVideoDirPath();
    }

    /**
     * 只删除记录，不删除文件
     *
     * @param v9PornItem v
     */
    private void deleteWithoutFile(V9PornItem v9PornItem) {
        v9PornItem.setDownloadId(0);
        dataManager.updateV9PornItem(v9PornItem);
    }

    /**
     * 连同文件一起删除
     *
     * @param v9PornItem v
     */
    private void deleteWithFile(V9PornItem v9PornItem) {
        File file = new File(v9PornItem.getDownLoadPath(getCustomDownloadVideoDirPath()));
        if (file.delete()) {
            v9PornItem.setDownloadId(0);
            dataManager.updateV9PornItem(v9PornItem);
        } else {
            ifViewAttached(new ViewAction<DownloadView>() {
                @Override
                public void run(@NonNull DownloadView view) {
                    view.showMessage("删除文件失败", TastyToast.ERROR);
                }
            });
        }
    }


    /**
     * 直接拷贝缓存好的视频即可
     *
     * @param v9PornItem v
     */
    private void copyCacheFile(final File videoCacheDir, final V9PornItem v9PornItem, final DownloadListener downloadListener) throws IOException {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                VideoCacheFileNameGenerator myFileNameGenerator = new VideoCacheFileNameGenerator();
                String cacheFileName = myFileNameGenerator.generate(v9PornItem.getVideoResult().getVideoUrl());
                File fromFile = new File(videoCacheDir, cacheFileName);
                if (!fromFile.exists() || fromFile.length() <= 0) {
                    e.onError(new Exception("缓存文件错误，无法拷贝"));
                }
                e.onNext(fromFile);
                e.onComplete();
            }
        }).map(new Function<File, V9PornItem>() {
            @Override
            public V9PornItem apply(File fromFile) throws Exception {
                File toFile = new File(v9PornItem.getDownLoadPath(getCustomDownloadVideoDirPath()));
                if (toFile.exists() && toFile.length() > 0) {
                    throw new Exception("已经下载过了");
                } else {
                    if (!toFile.createNewFile()) {
                        throw new Exception("创建文件失败");
                    }
                }
                FileUtils.copyFile(fromFile, toFile);
                v9PornItem.setTotalFarBytes((int) fromFile.length());
                v9PornItem.setSoFarBytes((int) fromFile.length());
                return v9PornItem;
            }
        }).map(new Function<V9PornItem, String>() {
            @Override
            public String apply(V9PornItem v9PornItem) throws Exception {
                v9PornItem.setStatus(FileDownloadStatus.completed);
                v9PornItem.setProgress(100);
                v9PornItem.setFinishedDownloadDate(new Date());
                v9PornItem.setDownloadId(FileDownloadUtils.generateId(v9PornItem.getVideoResult().getVideoUrl(), v9PornItem.getDownLoadPath(getCustomDownloadVideoDirPath())));
                dataManager.updateV9PornItem(v9PornItem);
                return "下载完成";
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String s) {
                        if (downloadListener != null) {
                            downloadListener.onSuccess(s);
                        } else {
                            ifViewAttached(new ViewAction<DownloadView>() {
                                @Override
                                public void run(@NonNull DownloadView view) {
                                    view.showMessage(s, TastyToast.SUCCESS);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (downloadListener != null) {
                            downloadListener.onError(msg);
                        } else {
                            ifViewAttached(new ViewAction<DownloadView>() {
                                @Override
                                public void run(@NonNull DownloadView view) {
                                    view.showMessage(msg, TastyToast.ERROR);
                                }
                            });
                        }
                    }
                });
    }

    public int getPlaybackEngine(){
        return dataManager.getPlaybackEngine();
    }

    public interface DownloadListener {
        void onSuccess(String message);

        void onError(String message);
    }
}
