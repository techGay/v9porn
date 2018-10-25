package com.u9porn.ui.pav;

import com.u9porn.data.model.pxgav.PavModel;
import com.u9porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public interface PavView extends BaseView {
    void setData(List<PavModel> pavModelList);

    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<PavModel> pavModelList);
}
