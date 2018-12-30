package com.u9porn.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
import com.u9porn.BuildConfig;
import com.u9porn.R;
import com.u9porn.data.model.UpdateVersion;
import com.u9porn.constants.Constants;
import com.u9porn.utils.NotificationChannelHelper;

import java.io.File;

/**
 * 升级下载apk服务
 *
 * @author flymegoc
 * @date 2017/12/23
 */

public class UpdateDownloadService extends Service {
    private static final String KEY_ACTION = "key_action";
    private static final int ACTION_PAUSE = 1;
    private static final int ACTION_GO_ON = 2;
    private static final int ACTION_CANCEL = 3;
    private static final String TAG = UpdateDownloadService.class.getSimpleName();
    private int progress = 1;
    private int downloadId;
    private String path;
    private UpdateVersion updateVersion;
    //下载中取消任务
    private boolean isPause = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_NOT_STICKY;
        }
        int action = intent.getIntExtra(KEY_ACTION, 0);
        if (action == ACTION_PAUSE) {
            FileDownloader.getImpl().pauseAll();
            return START_NOT_STICKY;
        } else if (action == ACTION_GO_ON) {
            //直接过去就好
        } else if (action == ACTION_CANCEL) {
            //如果当前是暂停状态，则可以直接删除
            if (isPause) {
                FileDownloader.getImpl().clear(downloadId, path);
                stopForeground(true);
            } else {
                //否则先暂停，在暂停中移除
                FileDownloader.getImpl().pauseAll();
                isPause = true;
            }
            return START_NOT_STICKY;
        }

        if (updateVersion == null) {
            updateVersion = (UpdateVersion) intent.getSerializableExtra("updateVersion");
            if (updateVersion == null) {
                return START_NOT_STICKY;
            }
        }
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/v9porn_" + updateVersion.getVersionName() + ".apk";
        if (BuildConfig.DEBUG) {
            File file = new File(path);
            file.delete();
        }
        Logger.t(TAG).d(path);
        downloadId = FileDownloader.getImpl().create(updateVersion.getApkDownloadUrl()).setPath(path).setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                isPause = false;
                updateNotification(task, soFarBytes, totalBytes, ACTION_PAUSE);
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                isPause = false;
                updateNotification(task, soFarBytes, totalBytes, ACTION_PAUSE);
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                isPause = false;
                installApk(path);
                stopForeground(true);
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (!isPause) {
                    isPause = true;
                    updateNotification(task, soFarBytes, totalBytes, ACTION_GO_ON);
                } else {
                    FileDownloader.getImpl().clear(downloadId, path);
                    stopForeground(true);
                }
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {

            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        }).setWifiRequired(false).start();
        return START_NOT_STICKY;
    }

    private void updateNotification(BaseDownloadTask task, int soFarBytes, int totalBytes, int action) {
        int progress = (int) (((float) soFarBytes / totalBytes) * 100);
        String fileSize = Formatter.formatFileSize(UpdateDownloadService.this, soFarBytes).replace("MB", "") + "/ " + Formatter.formatFileSize(UpdateDownloadService.this, totalBytes);
        startNotification(action, progress, fileSize, task.getSpeed());
    }

    private void installApk(String path) {

        File file = new File(path);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getApplicationContext(), "com.u9porn.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void startNotification(int action, int progress, String fileSize, int speed) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannelHelper.CHANNEL_ID_FOR_UPDATE);
        builder.setContentTitle("正在下载");
        //只响铃震动一次
        builder.setOnlyAlertOnce(true);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_download_apk);

        remoteViews.setTextViewText(R.id.tv_download_filesize, fileSize);
        if (action == ACTION_GO_ON) {
            remoteViews.setTextViewText(R.id.bt_download_apk_pause, "继续");
        } else {
            remoteViews.setTextViewText(R.id.bt_download_apk_pause, "暂停");
        }
        remoteViews.setTextViewText(R.id.tv_download_speed, speed + "KB/s");
        remoteViews.setTextViewText(R.id.tv_download_progress, progress + "%");

        remoteViews.setProgressBar(R.id.progressBar_download, 100, progress, false);

        Intent pauseStartIntent = new Intent(this, UpdateDownloadService.class);
        pauseStartIntent.putExtra(KEY_ACTION, action);
        PendingIntent pauseStartPendingIntent = PendingIntent.getService(this, action, pauseStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_download_apk_pause, pauseStartPendingIntent);

        Intent cancelIntent = new Intent(this, UpdateDownloadService.class);
        cancelIntent.putExtra(KEY_ACTION, ACTION_CANCEL);
        PendingIntent cancelPendingIntent = PendingIntent.getService(this, ACTION_CANCEL, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_download_apk_cancel, cancelPendingIntent);

        builder.setContent(remoteViews);
        Notification notification = builder.build();
        int id = Constants.APK_DOWNLOAD_NOTIFICATION_ID;
        startForeground(id, notification);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
