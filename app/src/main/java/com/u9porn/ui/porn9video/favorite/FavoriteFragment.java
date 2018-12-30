package com.u9porn.ui.porn9video.favorite;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u9porn.R;
import com.u9porn.ui.MvpFragment;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * @author megoc
 */
public class FavoriteFragment extends MvpFragment<FavoriteView,FavoritePresenter>{


    @Inject
    protected FavoritePresenter favoritePresenter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @NonNull
    @Override
    public FavoritePresenter createPresenter() {
        return favoritePresenter;
    }
}
