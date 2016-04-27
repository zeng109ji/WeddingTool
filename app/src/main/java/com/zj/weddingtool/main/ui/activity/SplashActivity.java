package com.zj.weddingtool.main.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.config.BmobConfig;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.DeviceUtils;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.base.util.Utils;
import com.zj.weddingtool.base.widget.Titanic;
import com.zj.weddingtool.base.widget.TitanicTextView;
import com.umeng.analytics.MobclickAgent;

import com.bmob.pay.tool.BmobPay;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class SplashActivity extends BaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;

    private Titanic titanic;
    private TitanicTextView tvTitanic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkNetwork();
        initBmob();
        setContentView(R.layout.activity_splash);

        initView();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    cancleTextAnimation();
                    goHome();
                    finish();
                    break;
                case GO_LOGIN:
                    cancleTextAnimation();
                    goLogin();
                    finish();
                    break;
                default:
                    cancleTextAnimation();
                    break;
            }
        }
    };

    /**
     * 初始化 Bmob SDK
     */
    private void initBmob() {
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, BmobConfig.BMOB_APP_ID_NEW);

        BmobPay.init(this, BmobConfig.BMOB_APP_ID_NEW);

        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, BmobConfig.BMOB_APP_ID_NEW);
    }

    private void initView() {

        titanic = new Titanic();
        tvTitanic = (TitanicTextView) findViewById(R.id.tv_titanic);
        titanic.start(tvTitanic);

        mHandler.sendEmptyMessageDelayed(GO_LOGIN, 3000);
    }

    private void cancleTextAnimation() {
        if (null != titanic) {
            titanic.cancel();
        }
    }

    private void checkNetwork() {
        if(!Utils.isNetworkAvailable(this))
            ToastUtils.showToast("网络连接异常");
    }

    private void goHome() {

    }

    private void goLogin() {
        // 禁止模拟器直接运行
        if(!BmobConfig.DEBUG && DeviceUtils.isEmulator(this)) {
            ToastUtils.showToast("他她工具不支持该环境，程序员！");
            finish();
            return;
        }

        Intent intent = new Intent(SplashActivity.this, com.zj.weddingtool.main.ui.activity.LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashActivity"); // 保证 onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

}
