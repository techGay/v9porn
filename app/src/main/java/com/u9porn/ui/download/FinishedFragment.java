package com.u9porn.ui.download;


import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitsuki.swipe.SwipeItemLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.DownloadVideoAdapter;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.service.DownloadVideoService;
import com.u9porn.ui.MvpFragment;
import com.u9porn.utils.DownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class FinishedFragment extends MvpFragment<DownloadView, DownloadPresenter> implements DownloadManager.DownloadStatusUpdater, DownloadView {

    @BindView(R.id.recyclerView_download_finish)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private DownloadVideoAdapter mDownloadAdapter;
    private boolean isFocusRefresh = false;

    @Inject
    protected DownloadPresenter downloadPresenter;

    @Inject
    public FinishedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DownloadManager.getImpl().addUpdater(this);
    }

    @NonNull
    @Override
    public DownloadPresenter createPresenter() {
        return downloadPresenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        List<V9PornItem> mV9PornItemList = new ArrayList<>();
        mDownloadAdapter = new DownloadVideoAdapter(R.layout.item_right_menu_delete_download, mV9PornItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mDownloadAdapter);
        mDownloadAdapter.setEmptyView(R.layout.empty_view, recyclerView);
        mDownloadAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                V9PornItem v9PornItem = (V9PornItem) adapter.getItem(position);
                if (v9PornItem == null) {
                    return;
                }
                openMp4File(v9PornItem);
            }
        });
        mDownloadAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                V9PornItem v9PornItem = (V9PornItem) adapter.getItem(position);
                if (view.getId() == R.id.right_menu_delete && v9PornItem != null) {
                    SwipeItemLayout swipeItemLayout = (SwipeItemLayout) view.getParent();
                    swipeItemLayout.close();
                    File file = new File(v9PornItem.getDownLoadPath(presenter.getCustomDownloadVideoDirPath()));
                    if (file.exists()) {
                        showDeleteFileDialog(v9PornItem);
                    } else {
                        presenter.deleteDownloadedTask(v9PornItem, false);
                        presenter.loadFinishedData();
                    }
                }
            }
        });
        presenter.loadFinishedData();
    }

    private void showDeleteFileDialog(final V9PornItem v9PornItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("是否连同删除本地文件？");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.deleteDownloadedTask(v9PornItem, false);
                presenter.loadFinishedData();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.deleteDownloadedTask(v9PornItem, true);
                presenter.loadFinishedData();
            }
        });
        builder.show();
    }

    /**
     * 调用系统播放器播放本地视频
     *
     * @param v9PornItem item
     */
    private void openMp4File(V9PornItem v9PornItem) {
        File file = new File(v9PornItem.getDownLoadPath(presenter.getCustomDownloadVideoDirPath()));
        if (file.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.u9porn.fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager pm = context.getPackageManager();
            ComponentName cn = intent.resolveActivity(pm);
            if (cn == null) {
                showMessage("你手机上未安装任何可以播放此视频的播放器！", TastyToast.INFO);
                return;
            }
            startActivity(intent);
        } else {
            showReDownloadFileDialog(v9PornItem);
        }
    }


    private void showReDownloadFileDialog(final V9PornItem v9PornItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("文件不存在，可能已经被删除，要重新下载？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                v9PornItem.setDownloadId(0);
                v9PornItem.setSoFarBytes(0);
                presenter.updateV9PornItem(v9PornItem);
                presenter.downloadVideo(v9PornItem, true);
                isFocusRefresh = true;
                Intent intent = new Intent(getContext(), DownloadVideoService.class);
                context.startService(intent);
            }
        });
        builder.show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_finish, container, false);
    }

    @Override
    public String getTitle() {
        return "下载完成";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().removeUpdater(this);
    }

    @Override
    public void complete(BaseDownloadTask task) {
        presenter.loadFinishedData();
    }

    @Override
    public void update(BaseDownloadTask task) {
        if (isFocusRefresh) {
            isFocusRefresh = false;
            presenter.loadFinishedData();
        }
    }

    @Override
    public void setDownloadingData(List<V9PornItem> v9PornItems) {

    }

    @Override
    public void setFinishedData(List<V9PornItem> v9PornItems) {
        mDownloadAdapter.setNewData(v9PornItems);
    }


    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }
}
