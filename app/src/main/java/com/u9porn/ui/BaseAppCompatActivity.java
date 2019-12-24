package com.u9porn.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.constants.Keys;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.eventbus.NeedCheckGoogleRecaptchaEvent;
import com.u9porn.ui.google.GoogleRecaptchaVerifyActivity;
import com.u9porn.utils.PlaybackEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * @author flymegoc
 * @date 2017/11/20
 * @describe
 */

public abstract class BaseAppCompatActivity extends DaggerAppCompatActivity implements BGASwipeBackHelper.Delegate {
    private final String TAG = this.getClass().getSimpleName();

    protected BGASwipeBackHelper mSwipeBackHelper;
    protected boolean existActivityWithAnimation = true;
    protected Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        context = this;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkGoogleRecaptcha(NeedCheckGoogleRecaptchaEvent needCheckGoogleRecaptchaEvent) {
        if (needGoToCheckGoogleRecaptcha()) {
            Intent intent = new Intent(this, GoogleRecaptchaVerifyActivity.class);
            startActivityWithAnimation(intent);
        }
    }

    protected boolean needGoToCheckGoogleRecaptcha() {
        return true;
    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return zhichi
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 带动画的启动activity
     */
    public void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.side_out_left);
    }

    /**
     * 带动画的启动activity
     */
    public void startActivityForResultWithAnimation(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.side_out_left);
    }

    protected void goToPlayVideo(V9PornItem v9PornItem, int playBackEngine) {
        Intent intent = PlaybackEngine.getPlaybackEngineIntent(this, playBackEngine);
        intent.putExtra(Keys.KEY_INTENT_V9PORN_ITEM, v9PornItem);
        startActivityWithAnimation(intent);
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
        super.onBackPressed();
        if (existActivityWithAnimation) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.side_out_right);
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param color color
     */
    protected void setStatusBarColor(@ColorInt int color) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color          color
     * @param statusBarAlpha 透明度
     */
    public void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha);
    }

    protected void showMessage(String msg, int type) {
        //因为时在onDestroy 才取消的请求，初步断定又可能就是在那么微妙的一瞬间发生了
        //android.view.WindowManager$BadTokenException · Unable to add window -- token android.os.BinderProxy@53d6ca9 is not valid; is your activity running? 如果activity正在销毁则可能引发
        if (!isFinishing()) {
            TastyToast.makeText(getApplicationContext(), msg, TastyToast.LENGTH_SHORT, type).show();
        }
    }

    protected void initToolBar(Toolbar toolbar) {
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setContentInsetStartWithNavigation(0);
    }
}
