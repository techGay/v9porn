package com.u9porn.ui.axgle;

import com.u9porn.data.model.axgle.AxgleVideo;
import com.u9porn.ui.BaseView;

import java.util.List;

/**
 * @author megoc
 */
public interface AxgleView extends BaseView{
    void setData(List<AxgleVideo> pavModelList);

    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<AxgleVideo> pavModelList);
}
