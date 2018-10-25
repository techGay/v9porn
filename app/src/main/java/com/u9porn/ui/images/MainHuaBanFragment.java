package com.u9porn.ui.images;

import com.u9porn.data.db.entity.Category;
import com.u9porn.ui.basemain.BaseMainFragment;

public class MainHuaBanFragment extends BaseMainFragment {

    public static MainHuaBanFragment getInstance() {
        return new MainHuaBanFragment();
    }

    @Override
    public int getCategoryType() {
        return Category.TYPE_HUA_BAN;
    }

    @Override
    public boolean isNeedDestroy() {
        return true;
    }
}
