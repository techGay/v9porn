package com.u9porn.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u9porn.R;
import com.u9porn.data.model.Mm99;
import com.u9porn.utils.GlideApp;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public class Mm99Adapter extends BaseQuickAdapter<Mm99, BaseViewHolder> {
    private Map<String, Integer> heightMap = new HashMap<>();
    private int width;

    public Mm99Adapter(int layoutResId) {
        super(layoutResId);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Mm99 item) {
        final ImageView imageView = helper.getView(R.id.iv_item_99_mm);
        GlideApp.with(helper.itemView.getContext()).asBitmap().load(buildGlideUrl(item.getImgUrl())).transition(new BitmapTransitionOptions().crossFade(300)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imageView.setImageBitmap(resource);
                int height;
                if (!heightMap.containsKey(item.getImgUrl())) {
                    height = resource.getHeight() * width / item.getImgWidth();
                    heightMap.put(item.getImgUrl(), height);
                } else {
                    height = heightMap.get(item.getImgUrl());
                }
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) helper.itemView.getLayoutParams();
                layoutParams.height = height;
                helper.itemView.setLayoutParams(layoutParams);
            }
        });

    }

    private GlideUrl buildGlideUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            HttpUrl httpUrl=HttpUrl.parse(url);
            return new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                    .addHeader("Host", (httpUrl != null ? httpUrl.host() : "img.99mm.net"))
                    .addHeader("Referer", "http://www.99mm.me/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .build());
        }
    }
}
