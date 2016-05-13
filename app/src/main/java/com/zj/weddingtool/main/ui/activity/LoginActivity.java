package com.zj.weddingtool.main.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.zj.weddingtool.R;
import com.zj.weddingtool.base.config.BmobConfig;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.LocalBroadcasts;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.base.util.Utils;
import com.zj.weddingtool.main.FixLoginService;
import com.zj.weddingtool.main.manager.LoginManager;
import com.zj.weddingtool.main.model.User;
import com.zj.weddingtool.weddingtool.ui.activity.WeddingToolActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * 登陆界面
 *
 * @author zj
 * @date 2014-4-24
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActicity";

    private Button btnLogin;
    private Button btnReg;
    private Button btnResetPsd;
    private TextView tvUsername;
    private EditText etUsername;
    private EditText etPassword;

    private String username;
    private String password;

    private Boolean isNotNullOfUsername = false;    //判断用户名输入是否为空
    private Boolean isNotNullOfPassword = false;    ////判断密码输入是否为空

    // 三方登陆支持
    private TextView mUserInfo;
    private ImageView mUserLogo;
    private ImageView mNewLoginButton;

//    Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 0) {
//                mUserInfo.setVisibility(android.view.View.VISIBLE);
//                mUserInfo.setText(msg.getData().getString("nickname"));
//            } else if (msg.what == 1) {
//                Bitmap bitmap = (Bitmap) msg.obj;
//                mUserLogo.setImageBitmap(bitmap);
//                mUserLogo.setVisibility(android.view.View.VISIBLE);
//            } else if (msg.what == MessageType.MSG_LOGIN_BY_SCHOOL_FINISHED) {
//                // 检查登陆结果
//                isLoginSuccess(loginResponse);
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!Utils.isNetworkAvailable(this)) {
            ToastUtils.showToast("网络连接异常");
        }

        // Bmob自动更新组件
        BmobUpdateAgent.initAppVersion(this);
        BmobUpdateAgent.setUpdateOnlyWifi(true);
        //BmobUpdateAgent.update(this);

        initView();

        autoCompleteLoginInfo();
    }

    @Override
    protected void onRestart() {
        if(!Utils.isNetworkAvailable(this)) {
            ToastUtils.showToast("网络连接异常");
        }
        super.onRestart();
    }

    private void initView() {

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReg = (Button) findViewById(R.id.btn_register);
        btnResetPsd = (Button) findViewById(R.id.btn_reset_psd);

        tvUsername = (TextView) findViewById(R.id.tv_username);

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);

        etUsername.addTextChangedListener(textWatcher1);
        etPassword.addTextChangedListener(textWatcher2);

        tvUsername.setText("用户名");
        etUsername.setInputType(InputType.TYPE_CLASS_TEXT);
        completeLoginInfo(0);

        btnLogin.setOnClickListener(this);
        btnReg.setOnClickListener(this);
        btnResetPsd.setOnClickListener(this);

        mUserInfo = (TextView) findViewById(R.id.user_nickname);
        mUserLogo = (ImageView) findViewById(R.id.user_logo);
        mNewLoginButton = (ImageView) findViewById(R.id.new_login_btn);
        mNewLoginButton.setOnClickListener(this);

    }

    /**
     * 自动填充用户名和密码
     */
    private void autoCompleteLoginInfo() {
        // 自动填充最近使用的账号和密码
        HashMap<String, String> map = LoginManager.getInstance().getLoginInfo();

        if (map == null)
            return;

        if (map.containsKey(LoginManager.SP_KEY_USERNAME) && map.containsKey(LoginManager.SP_KEY_PASSWORD)) {
            String username = map.get(LoginManager.SP_KEY_USERNAME);
            String password = map.get(LoginManager.SP_KEY_PASSWORD);
            String loginMethodStr = map.get(LoginManager.SP_KEY_LOGIN_METHOD);
            etUsername.setText(username);
            etPassword.setText(password);
            if (loginMethodStr.equals(""))
                return;
        }
    }

    private void completeLoginInfo(int loginMethod) {
        HashMap<String, String> map = LoginManager.getInstance().getUserInfo(loginMethod);
        if (map == null)
            return;

        if (map.containsKey(LoginManager.SP_KEY_USERNAME) && map.containsKey(LoginManager.SP_KEY_PASSWORD)) {
            String username = map.get(LoginManager.SP_KEY_USERNAME);
            String password = map.get(LoginManager.SP_KEY_PASSWORD);
            etUsername.setText(username);
            etPassword.setText(password);
            if(!username.equals(""))//如果已有账户密码即可自动登陆
                login();
        }
    }

    private void goHome() {

        //加载用户购物车数据
        //ShopCartModule.getInstance().refresh();

        Intent intent = new Intent(LoginActivity.this, WeddingToolActivity.class);
        startActivity(intent);
        finish();
    }

    private void login() {
        updateLoginBtnText("登录中");
        if(!Utils.isNetworkAvailable(this)) {
            ToastUtils.showToast("网络异常，请检查网络连接后重试");
            updateLoginBtnText("重新登陆");
        } else {
            loginByAccount();
        }
    }

    private boolean loginCheck(String username, String password) {
        boolean isValid = true;
        if (!Utils.isNetworkAvailable(this)) {
            ToastUtils.showToast("网络异常，请检查网络连接后重试");
        } else if (username.equals("") || password.equals("")) {
            ToastUtils.showToast("用户名或密码为空");
            updateLoginBtnText("重新登陆");
        } else {
            isValid = true;
        }
        return isValid;
    }


    // 使用账号登陆
    public void loginByAccount() {

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        //登录有效性检测
        if (!loginCheck(username, password))
            return;

        showProgressDialog();
        User bu2 = new User();
        bu2.setUsername(username);
        bu2.setPassword(password);
        bu2.login(this, new SaveListener() {
            @Override
            public void onSuccess() {

                dismissProgressDialog();

                // 更新用户状态
                LoginManager.getInstance().updUserState(User.USER_STATE_LOGIN_BY_TATA);

                // 保存用户登陆方式
                LoginManager.getInstance().saveLoginInfo(username, password, 0);

                // 跳转到主页
                goHome();

            }

            @Override
            public void onFailure(int code, String msg) {
                dismissProgressDialog();
                ToastUtils.showToast(String.format("他她系统登录失败 [%d: %s]", code, msg));
                updateLoginBtnText("重新登陆");

                // 启动登陆修复程序
                showFixDialog();
            }
        });
    }


    /**
     * 选择是否修复
     */
    private void showFixDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("他她工具登陆修复程序").setMessage("账号系统登陆失败，有可能是接口变动" +
                "或服务器出现问题，是否启动他她工具自动修复程序?")
                .setNegativeButton("不了，谢谢", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("立即启动", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startFixTool();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialogTitleLineColor(dialog);
    }

    /**
     * 启动修复程序
     */
    private void startFixTool() {
        showProgressDialog();
        ToastUtils.showToast("自动修复启动中");
        Intent intent = new Intent(LoginActivity.this, FixLoginService.class);
        startService(intent);
    }

    //登录失败以后更新Button的文字
    public void updateLoginBtnText(String text) {
        if (null != btnLogin) {
            btnLogin.setText(text);
            if (text.equals("重新登陆")) {
                btnLogin.setPressed(false);
            } else {
                btnLogin.setSelected(true);
            }
        } else {
            return;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 登陆
            case R.id.btn_login:
                login();
                break;

            //密码重置
            case R.id.btn_reset_psd:
                Intent toResetPsdActivity = new Intent(LoginActivity.this, ResetPsdActivity.class);
                startActivity(toResetPsdActivity);
                break;

            //注册新用户
            case R.id.btn_register:
                Intent toReg = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toReg);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcasts.registerLocalReceiver(fixReceiver, FixLoginService.ACTION_FIX_LOGIN_RESULT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcasts.unregisterLocalReceiver(fixReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LoginActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LoginActivity"); // 保证 onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }


    private BroadcastReceiver fixReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissProgressDialog();
            if(intent.getAction().equals(FixLoginService.ACTION_FIX_LOGIN_RESULT)) {
                boolean isSuccess = intent.getBooleanExtra(FixLoginService.KEY_EXTRA_FIX_LOGIN_RESULT, false);
                ToastUtils.showToast("修复结果验证中");
                if(BmobConfig.DEBUG)
                    Log.d(TAG, isSuccess ? "登陆成功" : "登陆失败");
                if(isSuccess)
                    goHome();
                else
                    ToastUtils.showToast("他她工具系统登陆失败");
            }

        }
    };


    // 文本输入监听
    private TextWatcher textWatcher1 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                isNotNullOfUsername = true;
                //Log.d(TAG, "用户名长度"+s.length());
            } else {
                isNotNullOfUsername = false;
            }

            if (isNotNullOfUsername && isNotNullOfPassword) {
                btnLogin.setPressed(false);
                btnLogin.setEnabled(true);
                btnLogin.setClickable(true);
            } else {
                btnLogin.setPressed(true);
                //禁用登陆按钮
                btnLogin.setEnabled(false);
                btnLogin.setClickable(false);
            }
        }
    };

    private TextWatcher textWatcher2 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                isNotNullOfPassword = true;
                //Log.d(TAG, "密码长度"+s.length());
            } else {
                isNotNullOfPassword = false;
            }

            if (isNotNullOfUsername && isNotNullOfPassword) {
                btnLogin.setPressed(false);
                btnLogin.setEnabled(true);
                btnLogin.setClickable(true);
            } else {
                btnLogin.setPressed(true);
                //禁用登陆按钮
                btnLogin.setEnabled(false);
                btnLogin.setClickable(false);
            }
        }
    };

}
