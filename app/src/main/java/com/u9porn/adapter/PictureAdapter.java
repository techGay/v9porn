package com.u9porn.adapter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.orhanobut.logger.Logger;
import com.u9porn.R;
import com.u9porn.utils.GlideApp;

import java.util.List;

import okhttp3.HttpUrl;

/**
 * @author flymegoc
 * @date 2018/1/25
 */

public class PictureAdapter extends PagerAdapter {

    private static final String TAG = PictureAdapter.class.getSimpleName();
    private List<String> imageList;
    private onImageClickListener onImageClickListener;

    public PictureAdapter(List<String> imageList) {
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList == null ? 0 : imageList.size();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, final int position) {

        View contentView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_picture_adapter, container, false);

        PhotoView photoView = contentView.findViewById(R.id.photoView);
        final ProgressBar progressBar = contentView.findViewById(R.id.progressBar);
        //http://i.meizitu.net/2018/01/25c01.jpg
        String url = imageList.get(position);
        if (url.contains("meizitu.net")) {
            GlideApp.with(container).load(buildGlideUrl(url)).transition(new DrawableTransitionOptions().crossFade(300)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(photoView);
        } else {
            GlideApp.with(container).load(buildGlide99MMUrl(url)).transition(new DrawableTransitionOptions().crossFade(300)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(photoView);
        }
        // Now just add PhotoView to ViewPager and return it
        container.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageClick(v, position);
                }
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageLongClick(v, position);
                }
                return true;
            }
        });
        Logger.t(TAG).d("instantiateItem");
        return contentView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        FrameLayout view = (FrameLayout) object;
        for (int i = 0; i < view.getChildCount(); i++) {
            View childView = view.getChildAt(i);
            if (childView instanceof PhotoView) {
                childView.setOnClickListener(null);
                childView.setOnLongClickListener(null);
                GlideApp.with(container).clear(childView);
                view.removeViewAt(i);
                Logger.t(TAG).d("clean photoView");
            }
        }
        container.removeView(view);
        Logger.t(TAG).d("destroyItem");
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public interface onImageClickListener {
        void onImageClick(View view, int position);

        void onImageLongClick(View view, int position);
    }

    public void setOnImageClickListener(PictureAdapter.onImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    private GlideUrl buildGlideUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            return new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                    .addHeader("Host", "i.meizitu.net")
                    .addHeader("Referer", "http://www.mzitu.com/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .build());
        }
    }

    private GlideUrl buildGlide99MMUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            HttpUrl httpUrl=HttpUrl.parse(url);
            return new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                    .addHeader("Host", httpUrl != null ? httpUrl.host() : "img.99mm.net")
                    .addHeader("Referer", "http://www.99mm.me/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .build());
        }
    }
}
