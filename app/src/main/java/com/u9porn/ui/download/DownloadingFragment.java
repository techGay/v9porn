package com.u9porn.ui.download;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aitsuki.swipe.SwipeItemLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.DownloadVideoAdapter;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.service.DownloadVideoService;
import com.u9porn.ui.MvpFragment;
import com.u9porn.utils.DownloadManager;

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
public class DownloadingFragment extends MvpFragment<DownloadView, DownloadPresenter> implements DownloadManager.DownloadStatusUpdater, DownloadView {

    private static final String TAG = DownloadingFragment.class.getSimpleName();
    @BindView(R.id.recyclerView_download)
    RecyclerView recyclerView;
    Unbinder unbinder;
    private DownloadVideoAdapter mDownloadAdapter;
    private ArrayList<V9PornItem> mV9PornItemList;

    @Inject
    protected DownloadPresenter downloadPresenter;

    @Inject
    public DownloadingFragment() {
        // Required empty public constructor
    }

    private FileDownloadConnectListener fileDownloadConnectListener = new FileDownloadConnectListener() {
        @Override
        public void connected() {
            Logger.t(TAG).d("连接上下载服务");
            List<V9PornItem> v9PornItems = presenter.loadDownloadingDatas();
            for (V9PornItem v9PornItem : v9PornItems) {
                int status = FileDownloader.getImpl().getStatus(v9PornItem.getVideoResult().getVideoUrl(), v9PornItem.getDownLoadPath(presenter.getCustomDownloadVideoDirPath()));
                Logger.t(TAG).d("fix status:::" + status);
                if (status != v9PornItem.getStatus()) {
                    v9PornItem.setStatus(status);
                    presenter.updateV9PornItem(v9PornItem);
                }
            }
            presenter.loadDownloadingData();
        }

        @Override
        public void disconnected() {
            Logger.t(TAG).d("下载服务连接断开了");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DownloadManager.getImpl().addUpdater(this);
        FileDownloader.getImpl().addServiceConnectListener(fileDownloadConnectListener);
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
        mV9PornItemList = new ArrayList<>();
        mDownloadAdapter = new DownloadVideoAdapter(R.layout.item_right_menu_delete_download, mV9PornItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(mDownloadAdapter);
        mDownloadAdapter.setEmptyView(R.layout.empty_view, recyclerView);

        mDownloadAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                goToPlayVideo((V9PornItem) adapter.getItem(position), presenter.getPlaybackEngine(), 0, 0);
            }
        });

        mDownloadAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, final View view, int position) {
                final V9PornItem v9PornItem = (V9PornItem) adapter.getItem(position);
                if (v9PornItem == null) {
                    return;
                }
                Logger.t(TAG).d("当前状态：" + v9PornItem.getStatus());
                if (view.getId() == R.id.right_menu_delete) {
                    SwipeItemLayout swipeItemLayout = (SwipeItemLayout) view.getParent();
                    swipeItemLayout.close();
                    presenter.deleteDownloadingTask(v9PornItem);
                    presenter.loadDownloadingData();
                } else if (view.getId() == R.id.iv_download_control) {
                    if (FileDownloader.getImpl().isServiceConnected()) {
                        if (v9PornItem.getStatus() == FileDownloadStatus.progress) {
                            FileDownloader.getImpl().pause(v9PornItem.getDownloadId());
                            ((ImageView) view).setImageResource(R.drawable.start_download);
                        } else {
                            showDownloadCheck(v9PornItem, view);
                        }
                    }
                }
            }
        });
    }

    /**
     * 让使用者自己选择是重新下载还是继续下载
     *
     * @param v9PornItem 需要下载的视频信息
     * @param view       需要更新的view
     */
    private void showDownloadCheck(final V9PornItem v9PornItem, final View view) {
        showDialog("请选择下载方式", new String[]{"继续下载", "重新下载"}, new DialogCheck() {
            @Override
            public void onCheck(int index) {
                startDownload(v9PornItem, view, index != 0);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();
            Logger.t(TAG).d("启动下载服务");
        } else {
            presenter.loadDownloadingData();
            Logger.t(TAG).d("下载服务已经连接");
        }
    }

    private void startDownload(V9PornItem v9PornItem, View view, boolean isForceReDownload) {
        presenter.downloadVideo(v9PornItem, isForceReDownload);
        ((ImageView) view).setImageResource(R.drawable.pause_download);
        Intent intent = new Intent(getContext(), DownloadVideoService.class);
        context.startService(intent);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void complete(BaseDownloadTask task) {
        if (mV9PornItemList == null || mV9PornItemList.size() == 0) {
            return;
        }
        Logger.t(TAG).d("已经下载完成了");
        V9PornItem v9PornItem = presenter.findUnLimit91PornItemByDownloadId(task.getId());
        if (v9PornItem != null) {
            int position = mV9PornItemList.indexOf(v9PornItem);
            if (position >= 0 && position < mV9PornItemList.size()) {
                mV9PornItemList.remove(position);
                mDownloadAdapter.notifyItemRemoved(position);
            } else {
                presenter.loadDownloadingData();
            }
        } else {
            presenter.loadDownloadingData();
        }
    }

    @Override
    public void update(BaseDownloadTask task) {
        Logger.t(TAG).d("updateV9PornItem(BaseDownloadTask task)");
        if (mV9PornItemList == null || mV9PornItemList.size() == 0) {
            return;
        }
        V9PornItem v9PornItem = presenter.findUnLimit91PornItemByDownloadId(task.getId());
        if (v9PornItem != null) {
            int position = mV9PornItemList.indexOf(v9PornItem);
            Logger.t(TAG).d("position" + position);
            if (position >= 0 && position < mV9PornItemList.size()) {
                mV9PornItemList.set(position, v9PornItem);
                mDownloadAdapter.notifyItemChanged(position);
            } else {
                mV9PornItemList.add(v9PornItem);
                mDownloadAdapter.notifyItemInserted(mV9PornItemList.size());
            }
        } else {
            presenter.loadDownloadingData();
        }
    }

    @Override
    public String getTitle() {
        return "正在下载";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileDownloader.getImpl().removeServiceConnectListener(fileDownloadConnectListener);
        DownloadManager.getImpl().removeUpdater(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setDownloadingData(List<V9PornItem> v9PornItems) {
        mV9PornItemList.clear();
        mV9PornItemList.addAll(v9PornItems);
        mDownloadAdapter.notifyDataSetChanged();
        if (v9PornItems.size() == 0) {
            try {
                Intent intent = new Intent(getContext(), DownloadVideoService.class);
                context.stopService(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setFinishedData(List<V9PornItem> v9PornItems) {

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

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
    }
}
