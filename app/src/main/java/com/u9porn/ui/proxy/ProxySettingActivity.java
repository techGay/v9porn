package com.u9porn.ui.proxy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.ProxyAdapter;
import com.u9porn.data.model.ProxyModel;
import com.u9porn.ui.MvpActivity;
import com.u9porn.ui.setting.SettingActivity;
import com.u9porn.utils.DialogUtils;
import com.u9porn.widget.IpInputEditText;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class ProxySettingActivity extends MvpActivity<ProxyView, ProxyPresenter> implements View.OnClickListener, ProxyView {
    private static final String TAG = ProxySettingActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_dialog_proxy_setting_ip_address)
    IpInputEditText etDialogProxySettingIpAddress;
    @BindView(R.id.et_dialog_proxy_setting_port)
    AppCompatEditText etDialogProxySettingPort;
    @BindView(R.id.bt_proxy_setting_test)
    AppCompatButton btProxySettingTest;
    @BindView(R.id.recycler_view_proxy_setting)
    RecyclerView recyclerViewProxySetting;
    @BindView(R.id.bt_proxy_setting_reset)
    AppCompatButton btProxySettingReset;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    private AlertDialog testAlertDialog;
    private boolean isTestSuccess = false;
    private ProxyAdapter proxyAdapter;
    private LoadViewHelper helper;

    @Inject
    protected ProxyPresenter proxyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_setting);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        init();
        initListener();
    }

    private void init() {
        testAlertDialog = DialogUtils.initLoadingDialog(this, "测试中，请稍候...");
        String proxyHost = presenter.getProxyIpAddress();
        int port = presenter.getProxyPort();
        etDialogProxySettingIpAddress.setIpAddressStr(proxyHost);
        etDialogProxySettingPort.setText(port == 0 ? "" : String.valueOf(port));

        List<ProxyModel> data = new ArrayList<>();
        proxyAdapter = new ProxyAdapter(R.layout.item_proxy, data);

        recyclerViewProxySetting.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProxySetting.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        View view = getLayoutInflater().inflate(R.layout.item_proxy, recyclerViewProxySetting, false);
        proxyAdapter.setHeaderView(view);
        proxyAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.parseXiCiDaiLi(false);
            }
        }, recyclerViewProxySetting);
        proxyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ProxyModel proxyModel = (ProxyModel) adapter.getItem(position);
                if (proxyModel == null) {
                    showMessage("数据出错了", TastyToast.INFO);
                    return;
                }
                proxyAdapter.setClickPosition(position);
                if (proxyModel.getType() != ProxyModel.TYPE_SOCKS) {
                    etDialogProxySettingIpAddress.setIpAddressStr(proxyModel.getProxyIp());
                    etDialogProxySettingPort.setText(proxyModel.getProxyPort());
                } else {
                    showMessage("暂不支持socket代理", TastyToast.INFO);
                }
            }
        });
        recyclerViewProxySetting.setAdapter(proxyAdapter);

        helper = new LoadViewHelper(recyclerViewProxySetting);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                presenter.parseXiCiDaiLi(false);
            }
        });

        presenter.parseXiCiDaiLi(false);
    }

    private void initListener() {
        btProxySettingTest.setOnClickListener(this);
        btProxySettingReset.setOnClickListener(this);
        swipeLayout.setEnabled(false);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                presenter.parseXiCiDaiLi(true);
            }
        });
    }

    @NonNull
    @Override
    public ProxyPresenter createPresenter() {
        return proxyPresenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.proxy_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_done_proxy_setting) {
            doSettingProxy();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSettingProxy() {
        if (!isTestSuccess) {
            showMessage("未有成功测试的代理，无法设置", TastyToast.INFO);
            return;
        }
        presenter.exitTest();
        String proxyIpAddress = etDialogProxySettingIpAddress.getIpAddressStr();
        String proxyPortStr = etDialogProxySettingPort.getText().toString().trim();
        if (TextUtils.isEmpty(proxyPortStr) || !TextUtils.isDigitsOnly(proxyPortStr)) {
            showMessage("无法设置，代理端口错误，请检查", TastyToast.INFO);
            return;
        }
        int proxyPort = Integer.parseInt(proxyPortStr);
        //设置开启代理并存储地址和端口号
        presenter.setOpenHttpProxy(true);
        presenter.setProxyIpAddress(proxyIpAddress);
        presenter.setProxyPort(proxyPort);
        showMessage("设置成功", TastyToast.SUCCESS);
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_proxy_setting_test:
                if (presenter.isSetPorn91VideoAddress()) {
                    Logger.t(TAG).d("木有设置地址呀");
                    showNeedSetAddressFirstDialog();
                    return;
                }
                isTestSuccess = false;
                String proxyIpAddress = etDialogProxySettingIpAddress.getIpAddressStr();
                String portStr = etDialogProxySettingPort.getText().toString().trim();
                if (TextUtils.isEmpty(portStr) || TextUtils.isEmpty(proxyIpAddress) || !TextUtils.isDigitsOnly(portStr)) {
                    showMessage("端口号或IP地址不正确", TastyToast.WARNING);
                    return;
                }
                int proxyPort = Integer.parseInt(portStr);
                presenter.testProxy(proxyIpAddress, proxyPort);
                QMUIKeyboardHelper.hideKeyboard(v);
                break;
            case R.id.bt_proxy_setting_reset:
                etDialogProxySettingIpAddress.setIpAddressStr("");
                etDialogProxySettingPort.setText("");
                View view = getCurrentFocus();
                if (view instanceof AppCompatEditText || view instanceof EditText) {
                    QMUIKeyboardHelper.showKeyboard((EditText) view, QMUIKeyboardHelper.SHOW_KEYBOARD_DELAY_TIME);
                }
                break;
            default:
        }
    }

    private void showNeedSetAddressFirstDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("温馨提示");
        builder.setMessage("还未设置91porn视频地址,无法测试，现在去设置？");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivityWithAnimation(intent);
                finish();
            }
        });
        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void testProxySuccess(String message) {
        isTestSuccess = true;
        showMessage(message, TastyToast.SUCCESS);
    }

    @Override
    public void testProxyError(String message) {
        dismissDialog();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void parseXiCiDaiLiSuccess(List<ProxyModel> proxyModelList) {
        swipeLayout.setEnabled(true);
        swipeLayout.setRefreshing(false);
        proxyAdapter.setNewData(proxyModelList);
    }

    @Override
    public void loadMoreDataComplete() {
        proxyAdapter.loadMoreComplete();
    }

    @Override
    public void loadMoreFailed() {
        proxyAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        proxyAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<ProxyModel> proxyModelList) {
        proxyAdapter.addData(proxyModelList);
    }

    @Override
    public void beginParseProxy() {
        helper.showLoading();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        testAlertDialog.show();
    }

    @Override
    public void showContent() {
        swipeLayout.setRefreshing(false);
        helper.showContent();
        dismissDialog();
    }

    private void dismissDialog() {
        if (testAlertDialog != null && testAlertDialog.isShowing()) {
            testAlertDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        dismissDialog();
        swipeLayout.setRefreshing(false);
        helper.showError();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    protected void onDestroy() {
        presenter.exitTest();
        super.onDestroy();
    }
}
