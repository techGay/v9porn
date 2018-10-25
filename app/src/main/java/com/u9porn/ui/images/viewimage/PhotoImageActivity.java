package com.u9porn.ui.images.viewimage;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.utils.GlideApp;
import com.u9porn.utils.SDCardUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.common.io.FileUtils;

/**
 * @author megoc
 */
public class PhotoImageActivity extends AppCompatActivity {

    @BindView(R.id.img)
    PhotoView img;
    private boolean isFullScreen = false;
    private String imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_image);
        ButterKnife.bind(this);
        goFullScreen();
        imgUrl = getIntent().getStringExtra("imgurl");
        GlideApp.with(this).load(imgUrl).into(img);
        ViewCompat.setTransitionName(img, "img");
        init();
    }

    private void init() {
        fixSwipeBack();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSavePictureDialog(imgUrl);
                return true;
            }
        });
    }

    private void showSavePictureDialog( String imageUrl) {
        final String saveUrl;
        if (imageUrl.contains("_")){
            saveUrl=imgUrl.split("_")[0];
        }else {
            saveUrl=imageUrl;
        }
        QMUIDialog.MenuDialogBuilder builder = new QMUIDialog.MenuDialogBuilder(this);
        builder.addItem("保存原图", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GlideApp.with(PhotoImageActivity.this).downloadOnly().load(Uri.parse(saveUrl)).into(new SimpleTarget<File>() {
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
            }
        });
        builder.show();
    }

    private void showMessage(String s, int error) {
        TastyToast.makeText(this,s,TastyToast.LENGTH_SHORT,error);
    }

    private void notifySystemGallery(File file) {

        MediaScannerConnection.scanFile(this,new String[]{file.getAbsolutePath()},new String[]{"image/jpeg"},null);
    }

    private void fixSwipeBack() {
        ViewGroup viewGroup = getWindow().getDecorView().findViewById(android.R.id.content);
        //就是这里了,有些statusbar库为了模拟状态栏，可能设置了padding,会在视频上方出现一条横幅，看上去好像状态栏没隐藏，其实已经隐藏了，这个是假的，错觉，所以重新设置padding为0即可
        viewGroup.setPadding(0, 0, 0, 0);
    }


    protected void goFullScreen() {
        isFullScreen = true;
        setUiFlags(true);
    }

    protected void exitFullScreen() {
        isFullScreen = false;
        setUiFlags(false);
    }

    private void setUiFlags(boolean fullscreen) {
        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(fullscreen ? getFullscreenUiFlags() : View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private int getFullscreenUiFlags() {
        int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        return flags;
    }

    @OnClick(R.id.img)
    public void onViewClicked() {
        if (isFullScreen){
            exitFullScreen();
        }else {
            goFullScreen();
        }
    }
}
