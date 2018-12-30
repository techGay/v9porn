package com.u9porn.ui.porn9video.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.constants.KeysActivityRequestResultCode;
import com.u9porn.data.model.User;
import com.u9porn.ui.MvpActivity;
import com.u9porn.ui.porn9video.favorite.FavoriteActivity;
import com.u9porn.ui.porn9video.search.SearchActivity;
import com.u9porn.ui.setting.SettingActivity;
import com.u9porn.utils.DialogUtils;
import com.u9porn.utils.GlideApp;
import com.u9porn.constants.Keys;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class UserLoginActivity extends MvpActivity<UserView, UserPresenter> implements UserView {

    private static final String TAG = UserLoginActivity.class.getSimpleName();
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.wb_captcha)
    ImageView captchaImageView;
    @BindView(R.id.bt_user_login)
    Button btUserLogin;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cb_remember_password)
    CheckBox cbRememberPassword;
    @BindView(R.id.cb_auto_login)
    CheckBox cbAutoLogin;

    private AlertDialog alertDialog;
    private String username;
    private String password;
    private int loginForAction;

    @Inject
    UserPresenter userPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        loginForAction = getIntent().getIntExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, 0);
        if (!TextUtils.isEmpty(presenter.getVideo9PornAddress())) {
            loadCaptcha();
        }
        btUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etAccount.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                String captcha = etCaptcha.getText().toString().trim();
                login(username, password, captcha);
            }
        });
        captchaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCaptcha();
            }
        });

        cbAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAutoLogin.isChecked()) {
                    cbAutoLogin.setChecked(true);
                    cbRememberPassword.setChecked(true);
                } else {
                    cbAutoLogin.setChecked(false);
                }
            }
        });

        cbRememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbRememberPassword.isChecked()) {
                    cbRememberPassword.setChecked(true);
                } else {
                    cbRememberPassword.setChecked(false);
                    cbAutoLogin.setChecked(false);
                }
            }
        });

        alertDialog = DialogUtils.initLoadingDialog(this, "登录中，请稍后...");
        setUpUserInfo();

        if (TextUtils.isEmpty(presenter.getVideo9PornAddress())) {
            showNeedSetAddressFirstDialog();
        }
    }

    private void setUpUserInfo() {
        username = presenter.getUserName();
        password = presenter.getPassword();
        if (!TextUtils.isEmpty(password)) {
            cbRememberPassword.setChecked(true);
        }
        boolean isAutoLogin = presenter.isAutoLogin();
        cbAutoLogin.setChecked(isAutoLogin);

        etAccount.setText(username);
        etPassword.setText(password);
    }

    private void login(String username, String password, String captcha) {

        if (TextUtils.isEmpty(username)) {
            showMessage("请填写用户名", TastyToast.INFO);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showMessage("请填写密码", TastyToast.INFO);
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            showMessage("请填写验证码", TastyToast.INFO);
            return;
        }
        QMUIKeyboardHelper.hideKeyboard(getCurrentFocus());
        presenter.login(username, password, captcha);
    }

    /**
     * 加载验证码，目前似乎是非必须，不填也是可以登录的
     */
    private void loadCaptcha() {
        String url = presenter.getVideo9PornAddress() + "captcha.php";

        Logger.t(TAG).d("验证码链接：" + url);
        Uri uri = Uri.parse(url);
        GlideApp.with(this).load(uri).placeholder(R.drawable.placeholder).transition(new DrawableTransitionOptions().crossFade(300)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(captchaImageView);
    }

    @NonNull
    @Override
    public UserPresenter createPresenter() {
        return userPresenter;
    }

    private void showNeedSetAddressFirstDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("温馨提示");
        builder.setMessage("还未设置91porn视频地址,无法登录或注册，现在去设置？");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivityWithAnimation(intent);
                finish();
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void loginSuccess(User user) {

        presenter.saveUserInfoPrf(username, password, cbRememberPassword.isChecked(), cbAutoLogin.isChecked());
        showMessage("登录成功", TastyToast.SUCCESS);
        switchWhereToGo();
    }


    private void switchWhereToGo() {
        switch (loginForAction) {
            case KeysActivityRequestResultCode.LOGIN_ACTION_FOR_LOOK_MY_FAVORITE:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivityWithAnimation(intent);
                finish();
                break;
            case KeysActivityRequestResultCode.LOGIN_ACTION_FOR_SEARCH_91PRON_VIDEO:
                Intent intentSearch = new Intent(this, SearchActivity.class);
                startActivityWithAnimation(intentSearch);
                finish();
                break;
            case KeysActivityRequestResultCode.LOGIN_ACTION_FOR_GET_UID:
                setResult(KeysActivityRequestResultCode.RESULT_CODE_FOR_REFRESH_GET_UID);
                onBackPressed();
                break;
            case KeysActivityRequestResultCode.LOGIN_ACTION_FOR_LOOK_AUTHOR_VIDEO:
                setResult(KeysActivityRequestResultCode.RESULT_FOR_LOOK_AUTHOR_VIDEO);
                onBackPressed();
                break;
            default:
                setResult(RESULT_OK);
                onBackPressed();
        }
    }

    @Override
    public void loginError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void registerSuccess(User user) {

    }

    @Override
    public void registerFailure(String message) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        if (alertDialog == null) {
            return;
        }
        alertDialog.show();
    }

    @Override
    public void showContent() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_user_register) {
            Intent intent = new Intent(this, UserRegisterActivity.class);
            startActivityWithAnimation(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
