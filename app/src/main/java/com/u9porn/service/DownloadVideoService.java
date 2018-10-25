package com.u9porn.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u9porn.MyApplication;
import com.u9porn.R;
import com.u9porn.data.DataManager;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.di.component.DaggerServiceComponent;
import com.u9porn.ui.download.DownloadActivity;
import com.u9porn.utils.DownloadManager;
import com.u9porn.constants.Constants;
import com.u9porn.utils.NotificationChannelHelper;

import java.util.List;

import javax.inject.Inject;

import dagger.android.DaggerService;

/**
 * @author flymegoc
 */
public class DownloadVideoService extends DaggerService implements DownloadManager.DownloadStatusUpdater {

    @Inject
    protected DataManager dataManager;

    public DownloadVideoService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DownloadManager.getImpl().addUpdater(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    private void startNotification(String videoName, int progress, String fileSize, int speed) {
        int id = Constants.VIDEO_DOWNLOAD_NOTIFICATION_ID;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannelHelper.CHANNEL_ID_FOR_DOWNLOAD);
        builder.setContentTitle("正在下载");
        //只响铃震动一次
        builder.setOnlyAlertOnce(true);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setProgress(100, progress, false);
        builder.setContentText(fileSize + "--" + speed + "KB/s");
        builder.setContentInfo(videoName);
        Intent intent = new Intent(this, DownloadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(id, notification);
    }

    @Override
    public void onDestroy() {
        DownloadManager.getImpl().removeUpdater(this);
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void complete(BaseDownloadTask task) {
        updateNotification(task, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
    }

    @Override
    public void update(BaseDownloadTask task) {
        updateNotification(task, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
    }

    private void updateNotification(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        int progress = (int) (((float) soFarBytes / totalBytes) * 100);
        String fileSize = Formatter.formatFileSize(DownloadVideoService.this, soFarBytes).replace("MB", "") + "/ " + Formatter.formatFileSize(DownloadVideoService.this, totalBytes);
        V9PornItem v9PornItem = dataManager.findV9PornItemByDownloadId(task.getId());
        if (v9PornItem != null) {
            if (task.getStatus() == FileDownloadStatus.completed) {
                List<V9PornItem> v9PornItemList = dataManager.findV9PornItemByDownloadStatus(FileDownloadStatus.progress);
                if (v9PornItemList.size() == 0) {
                    stopForeground(true);
                }
            } else {
                startNotification(v9PornItem.getTitle(), progress, fileSize, task.getSpeed());
            }
        } else {
            List<V9PornItem> v9PornItemList = dataManager.loadDownloadingData();
            if (v9PornItemList.size() == 0) {
                stopForeground(true);
            }
        }
    }
}
