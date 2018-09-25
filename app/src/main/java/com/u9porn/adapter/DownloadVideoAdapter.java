package com.u9porn.adapter;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u9porn.R;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.utils.GlideApp;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/9
 */

public class DownloadVideoAdapter extends BaseQuickAdapter<V9PornItem, BaseViewHolder> {

    public DownloadVideoAdapter(int layoutResId, @Nullable List<V9PornItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, V9PornItem item) {
        helper.setText(R.id.tv_91porn_item_title, item.getTitleWithDuration());
        ImageView simpleDraweeView = helper.getView(R.id.iv_91porn_item_img);
        Uri uri = Uri.parse(item.getImgUrl());
        GlideApp.with(helper.itemView).load(uri).placeholder(R.drawable.placeholder).transition(new DrawableTransitionOptions().crossFade(300)).into(simpleDraweeView);
        helper.setProgress(R.id.progressBar_download, item.getProgress());
        helper.setText(R.id.tv_download_progress, String.valueOf(item.getProgress()) + "%");
        helper.setText(R.id.tv_download_filesize, Formatter.formatFileSize(helper.itemView.getContext(), item.getSoFarBytes()).replace("MB", "") + "/ " + Formatter.formatFileSize(helper.itemView.getContext(), item.getTotalFarBytes()));
        if (item.getStatus() == FileDownloadStatus.completed) {
            helper.setText(R.id.tv_download_speed, "已完成");
            helper.setVisible(R.id.iv_download_control, false);
        } else {
            //未下载完成，显示控制
            helper.setVisible(R.id.iv_download_control, true);
            if (FileDownloader.getImpl().isServiceConnected()) {
                helper.setImageResource(R.id.iv_download_control, R.drawable.pause_download);
                if (item.getStatus() == FileDownloadStatus.progress) {
                    helper.setText(R.id.tv_download_speed, item.getSpeed() + " KB/s");
                } else if (item.getStatus() == FileDownloadStatus.paused) {
                    helper.setText(R.id.tv_download_speed, "暂停中");
                    helper.setImageResource(R.id.iv_download_control, R.drawable.start_download);
                } else if (item.getStatus() == FileDownloadStatus.pending) {
                    helper.setText(R.id.tv_download_speed, "准备中");
                } else if (item.getStatus() == FileDownloadStatus.started) {
                    helper.setText(R.id.tv_download_speed, "开始下载");
                } else if (item.getStatus() == FileDownloadStatus.connected) {
                    helper.setText(R.id.tv_download_speed, "连接中");
                } else if (item.getStatus() == FileDownloadStatus.error) {
                    helper.setText(R.id.tv_download_speed, "下载错误");
                    helper.setImageResource(R.id.iv_download_control, R.drawable.start_download);
                } else if (item.getStatus() == FileDownloadStatus.retry) {
                    helper.setText(R.id.tv_download_speed, "重试中");
                } else if (item.getStatus() == FileDownloadStatus.warn) {
                    helper.setText(R.id.tv_download_speed, "警告");
                    helper.setImageResource(R.id.iv_download_control, R.drawable.start_download);
                }

            } else {
                helper.setText(R.id.tv_download_speed, "暂停中");
                helper.setImageResource(R.id.iv_download_control, R.drawable.start_download);
            }
        }
        helper.addOnClickListener(R.id.iv_download_control);
        helper.addOnClickListener(R.id.right_menu_delete);
        helper.addOnClickListener(R.id.layout_download_item);
    }
}
