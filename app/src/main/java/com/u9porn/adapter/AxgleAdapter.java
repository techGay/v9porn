package com.u9porn.adapter;

import android.support.v7.widget.AppCompatImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u9porn.R;
import com.u9porn.data.model.axgle.AxgleVideo;
import com.u9porn.utils.GlideApp;

public class AxgleAdapter extends BaseQuickAdapter<AxgleVideo, BaseViewHolder> {

    public AxgleAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, AxgleVideo item) {
        helper.setText(R.id.tv_item_axgle_title, item.getTitle());
        AppCompatImageView imageView = helper.getView(R.id.iv_item_axgle_img);
        GlideApp.with(helper.itemView).load(item.getPreview_url()).placeholder(R.drawable.placeholder).transition(new DrawableTransitionOptions().crossFade(300)).into(imageView);
    }
}
