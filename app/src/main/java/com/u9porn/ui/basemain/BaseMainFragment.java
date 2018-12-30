package com.u9porn.ui.basemain;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseViewHolder;
import com.devbrackets.android.exomedia.util.ResourceUtil;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.BuildConfig;
import com.u9porn.R;
import com.u9porn.adapter.BaseMainFragmentAdapter;
import com.u9porn.adapter.SortCategoryAdapter;
import com.u9porn.data.db.entity.Category;
import com.u9porn.eventbus.LowMemoryEvent;
import com.u9porn.ui.MvpFragment;
import com.u9porn.utils.AnimationUtils;
import com.u9porn.utils.FragmentUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public abstract class BaseMainFragment extends MvpFragment<BaseMainView, BaseMainPresenter> implements BaseMainView, View.OnClickListener, SortCategoryAdapter.OnStartDragListener {

    private static final String TAG = BaseMainFragment.class.getSimpleName();
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.iv_sort_category)
    AppCompatImageButton ivSortCategory;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
    private PopupWindow popupWindow;
    private BaseMainFragmentAdapter mBaseMainFragmentAdapter;
    private List<Category> categoryList;
    private List<Category> sortCategoryList;
    private ItemTouchHelper mItemTouchHelper;
    private SortCategoryAdapter sortCategoryAdapter;
    private boolean isNeedInterruptOnBackPressed = false;
    private FragmentManager fragmentManager;
    private int currentSelectPosition = 0;
    private boolean isBackground = false;

    @Inject
    BaseMainPresenter baseMainPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentManager = getChildFragmentManager();
        mBaseMainFragmentAdapter = new BaseMainFragmentAdapter(fragmentManager, categoryList, getCategoryType());
    }

    public BaseMainFragment() {
        // Required empty public constructor
        categoryList = new ArrayList<>();
        sortCategoryList = new ArrayList<>();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_base_main, container, false);
    }


    @NonNull
    @Override
    public BaseMainPresenter createPresenter() {
        return baseMainPresenter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        Drawable dropDownDrawable = ResourceUtil.tintList(context, R.drawable.ic_arrow_drop_down_black_24dp, R.color.common_always_white_text_color);
        ivSortCategory.setImageDrawable(dropDownDrawable);
        ivSortCategory.setOnClickListener(this);
        mBaseMainFragmentAdapter.setDestroy(isNeedDestroy());
        viewPager.setAdapter(mBaseMainFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentSelectPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public boolean isNeedDestroy() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isBackground = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isBackground = true;
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        presenter.loadCategoryData(getCategoryType());
    }

    public abstract int getCategoryType();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showSortCategoryPopupWindow() {
        if (sortCategoryList.size() == 0) {
            presenter.loadAllCategoryData(getCategoryType());
        }
        if (popupWindow == null) {
            View view = getLayoutInflater().inflate(R.layout.layout_popuwindow_sort_category, tabLayout, false);
            RecyclerView sortCategoryRecyclerView = view.findViewById(R.id.recyclerView_sort_category);
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.setElevation(10);
            }
            popupWindow.setOutsideTouchable(true);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    AnimationUtils.rotateDown(ivSortCategory);
                    if (isNeedUpdate()) {
                        presenter.updateCategoryData(sortCategoryList);
                        presenter.loadCategoryData(getCategoryType());
                    }
                    isNeedInterruptOnBackPressed = false;
                }
            });
            popupWindow.setAnimationStyle(R.style.sort_category_popuwindow_anim);
            sortCategoryAdapter = new SortCategoryAdapter(R.layout.item_sort_category, sortCategoryList);
            sortCategoryAdapter.setOnStartDragListener(this);
            sortCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            sortCategoryRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            sortCategoryRecyclerView.setAdapter(sortCategoryAdapter);
            initItemTouchHelper(sortCategoryAdapter, sortCategoryRecyclerView, sortCategoryList);
            PopupWindowCompat.showAsDropDown(popupWindow, tabLayout, 0, 0, Gravity.BOTTOM);
            AnimationUtils.rotateUp(ivSortCategory);
            isNeedInterruptOnBackPressed = true;
            showMessage("拖动左边滑块可以排序哟", TastyToast.INFO);
        } else {
            if (!popupWindow.isShowing()) {
                AnimationUtils.rotateUp(ivSortCategory);
                PopupWindowCompat.showAsDropDown(popupWindow, tabLayout, 0, 0, Gravity.BOTTOM);
                isNeedInterruptOnBackPressed = true;
            } else {
                dismissPopupWindow();
            }
        }
    }

    private void dismissPopupWindow() {
        if (popupWindow == null || !popupWindow.isShowing()) {
            return;
        }
        AnimationUtils.rotateDown(ivSortCategory);
        popupWindow.dismiss();

        if (isNeedUpdate()) {
            presenter.updateCategoryData(sortCategoryList);
            presenter.loadCategoryData(getCategoryType());
        }

        isNeedInterruptOnBackPressed = false;
    }

    private boolean isNeedUpdate() {
        boolean needUpdate = false;
        for (Category category : sortCategoryList) {
            //先检查排序
            int newSortId = sortCategoryList.indexOf(category);
            if (newSortId != category.getSortId()) {
                category.setSortId(newSortId);
                needUpdate = true;
            }
            //在检查是否可见
            Category oldCategory = presenter.findCategoryById(category.getId());
            if (oldCategory.getIsShow() != category.getIsShow()) {
                needUpdate = true;
            }
        }
        return needUpdate;
    }

    public boolean onBackPressed() {
        boolean result = isNeedInterruptOnBackPressed;
        if (isNeedInterruptOnBackPressed) {
            dismissPopupWindow();
        }
        return result;
    }

    private void initItemTouchHelper(final SortCategoryAdapter sortCategoryAdapter, RecyclerView sortCategoryRecyclerView, final List<Category> categoryList) {
        //itemHelper的回调
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //也就是说返回值是组合式的
                //makeMovementFlags (int dragFlags, int swipeFlags)，看下面的解释说明
                int swipFlag = 0;
                //如果也监控左右方向的话，swipFlag=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                int dragflag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                //等价于：0001&0010;多点触控标记触屏手指的顺序和个数也是这样标记哦
                return makeMovementFlags(dragflag, swipFlag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //直接按照文档来操作啊，这文档写得太给力了,简直完美！
                sortCategoryAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                //注意这里有个坑的，itemView 都移动了，对应的数据也要移动
                Collections.swap(categoryList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //暂不处理,处理左右滑动
            }

            @Override
            public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //return true后，可以实现长按拖动排序和拖动动画了
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.sort_category_drag_color));
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
            }
        };

        //1.创建item helper
        mItemTouchHelper = new ItemTouchHelper(callback);
        //2.绑定到recyclerview上面去
        mItemTouchHelper.attachToRecyclerView(sortCategoryRecyclerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_sort_category:
                showSortCategoryPopupWindow();
                break;
            default:
        }
    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void onLoadCategoryData(List<Category> categoryList) {
        this.categoryList.clear();
        this.categoryList.addAll(categoryList);
        mBaseMainFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadAllCategoryData(List<Category> categoryList) {
        sortCategoryList.clear();
        sortCategoryList.addAll(categoryList);
        if (sortCategoryAdapter != null) {
            sortCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void startDragItem(BaseViewHolder helper) {
        mItemTouchHelper.startDrag(helper);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Logger.t(TAG).d("------------------onDestroy()");
    }

    /**
     * 尝试释放内存，因为我们使用的是FragmentPagerAdapter，导致即使页面不可见也不会释放内存，好处在于返回该页面时所有状态视图会保持为之前的状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTryToReleaseMemory(LowMemoryEvent lowMemoryEvent) {
        //有可能已经被回收或者还没创建（概率）
        if (!isBackground || categoryList == null || fragmentManager == null || viewPager == null || mBaseMainFragmentAdapter == null) {
            return;
        }
        if (!BuildConfig.DEBUG) {
            //Bugsnag.notify(new Throwable(TAG + ":LowMemory,try to release some memory now!"), Severity.INFO);
        }
        try {
            Logger.t(TAG).d("start try to release memory ....");
            FragmentTransaction mCurTransaction = fragmentManager.beginTransaction();

            //获取当前不可见的fragment，并尝试释放掉它们来释放内存，但同时会导致切换回该页面时所有状态均没有了，会被重新创建
            for (int i = 0; i < categoryList.size(); i++) {
                //尝试移除不可见的fragment
                if (i < currentSelectPosition - 1 || i > currentSelectPosition + 1) {
                    long itemId = mBaseMainFragmentAdapter.getItemId(i);
                    String name = FragmentUtils.makeFragmentName(viewPager.getId(), itemId);
                    Fragment fragment = fragmentManager.findFragmentByTag(name);
                    if (fragment != null) {
                        mCurTransaction.remove(fragment);
                    }
                }
            }
            mCurTransaction.commitNowAllowingStateLoss();
            //通知系统尝试释放内存
            System.gc();
            System.runFinalization();
            Logger.t(TAG).d("try to release memory success !!!");
        } catch (Exception e) {
            e.printStackTrace();
            if (!BuildConfig.DEBUG) {
                //Bugsnag.notify(new Throwable(TAG + " tryToReleaseMemory error::", e), Severity.WARNING);
            }
        }
    }
}
