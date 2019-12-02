package com.u9porn.ui.images.viewimage;

import android.annotation.SuppressLint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.PictureAdapter;
import com.u9porn.constants.Keys;
import com.u9porn.data.model.Mm99;
import com.u9porn.ui.MvpActivity;
import com.u9porn.utils.DialogUtils;
import com.u9porn.utils.GlideApp;
import com.u9porn.utils.SDCardUtils;
import com.u9porn.widget.ProblematicViewPager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.common.io.FileUtils;

/**
 * @author flymegoc
 */
public class PictureViewerActivity extends MvpActivity<PictureViewerView, PictureViewerPresenter> implements PictureViewerView {
    @BindView(R.id.viewPager)
    ProblematicViewPager viewPager;
    @BindView(R.id.tv_num)
    TextView tvNum;

    private List<String> imageList;
    private boolean isFullScreen = false;
    private PictureAdapter pictureAdapter;
    private AlertDialog alertDialog;

    @Inject
    protected PictureViewerPresenter pictureViewerPresenter;
    GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        goFullScreen();
        alertDialog = DialogUtils.initLoadingDialog(this, "解析图片列表中，请稍后...");
        init();
        //initListener();
    }

    @NonNull
    @Override
    public PictureViewerPresenter createPresenter() {
        return pictureViewerPresenter;
    }

    private void init() {
        fixSwipeBack();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        imageList = getIntent().getStringArrayListExtra(Keys.KEY_INTENT_PICTURE_VIEWER_IMAGE_ARRAY_LIST);
        int currentPosition = getIntent().getIntExtra(Keys.KEY_INTENT_PICTURE_VIEWER_CURRENT_IMAGE_POSITION, 0);
        updateNumberText(currentPosition);
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
        pictureAdapter = new PictureAdapter(imageList);
        viewPager.setAdapter(pictureAdapter);
        viewPager.setCurrentItem(currentPosition);
        int id = getIntent().getIntExtra(Keys.KEY_INTENT_MEI_ZI_TU_CONTENT_ID, 0);
        if (id > 0) {
            presenter.listMeZiPicture(id, false);
        }
        Mm99 mm99 = (Mm99) getIntent().getSerializableExtra(Keys.KEY_INTENT_99_MM_ITEM);
        if (mm99 != null) {
            presenter.list99MmPicture(mm99.getId(), mm99.getContentUrl(), false);
        }
        // View decorView = getWindow().getDecorView();
    }


    private void initListener() {
        pictureAdapter.setOnImageClickListener(new PictureAdapter.onImageClickListener() {
            @Override
            public void onImageClick(View view, int position) {
                if (isFullScreen) {
                    exitFullScreen();
                } else {
                    goFullScreen();
                }
            }

            @Override
            public void onImageLongClick(View view, int position) {
                if (imageList == null) {
                    return;
                }
                showSavePictureDialog(imageList.get(position));
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateNumberText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateNumberText(int position) {
        if (imageList == null) {
            return;
        }
        if (position < 0) {
            position = 0;
        }
        tvNum.setText((position + 1) + "/" + imageList.size());
    }

    private void showSavePictureDialog(final String imageUrl) {
        QMUIDialog.MenuDialogBuilder builder = new QMUIDialog.MenuDialogBuilder(this);
        builder.addItem("保存图片", (dialog, which) -> {
            GlideApp.with(PictureViewerActivity.this).downloadOnly().load(Uri.parse(imageUrl)).into(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                    File filePath = new File(SDCardUtils.DOWNLOAD_IMAGE_PATH);
                    if (!filePath.exists()) {
                        if (!filePath.mkdirs()) {
                            showMessage("创建文件夹失败了", TastyToast.ERROR);
                            return;
                        }
                    }
                    File file = new File(filePath, UUID.randomUUID().toString() + ".jpg");
                    try {
                        FileUtils.copyFile(resource, file);
                        showMessage("保存图片成功了", TastyToast.SUCCESS);
                        notifySystemGallery(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessage("保存图片失败了", TastyToast.ERROR);
                    }
                }
            });
            dialog.dismiss();
        });
        builder.show();
    }

    private void fixSwipeBack() {
        ViewGroup viewGroup = getWindow().getDecorView().findViewById(android.R.id.content);
        //就是这里了,有些statusbar库为了模拟状态栏，可能设置了padding,会在视频上方出现一条横幅，看上去好像状态栏没隐藏，其实已经隐藏了，这个是假的，错觉，所以重新设置padding为0即可
        viewGroup.setPadding(0, 0, 0, 0);
    }

    private void notifySystemGallery(File file) {

        MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
    }

    protected void goFullScreen() {
        isFullScreen = true;
        //tvNum.setVisibility(View.VISIBLE);
        hideSystemUI();
    }

    protected void exitFullScreen() {
        isFullScreen = false;
        //tvNum.setVisibility(View.VISIBLE);
        showSystemUI();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }

    @Override
    public void setStatusBarColor(int color, int statusBarAlpha) {

    }

    @Override
    public void setData(List<String> imageList) {
        this.imageList.clear();
        this.imageList.addAll(imageList);
        pictureAdapter.notifyDataSetChanged();

    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        alertDialog.show();
    }

    @Override
    public void showContent() {
        dismissDialog();
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
        dismissDialog();
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        viewPager = null;
        imageList = null;
        pictureAdapter = null;
        super.onDestroy();

    }
}
