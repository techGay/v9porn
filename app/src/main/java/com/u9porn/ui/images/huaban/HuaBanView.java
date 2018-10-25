package com.u9porn.ui.images.huaban;

import com.u9porn.data.model.HuaBan;
import com.u9porn.ui.BaseView;

import java.util.List;

public interface HuaBanView extends BaseView {
    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<HuaBan.Picture> unLimit91PornItemList);

    void setData(List<HuaBan.Picture> data);
}
