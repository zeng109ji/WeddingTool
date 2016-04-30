package com.zj.weddingtool.weddingtool.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.androidquery.AQuery;
import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 *
 * @author zj
 * @date 2016-4-19
 */
public class WeddingToolActivity extends BaseActivity implements OnClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "WeddingToolActivity";
//    private static final int RESULT_REQUEST_AUTH_HBUT = 1007;

    AQuery aq = new AQuery(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weddingtool);

     //   getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
    }

    private void initView() {
        aq.id(R.id.cv_campus_jiehunrenwu).clicked(this);
        aq.id(R.id.cv_campus_jiehunyusuan).clicked(this);
        aq.id(R.id.cv_campus_jiehundengji).clicked(this);
        aq.id(R.id.cv_campus_jiehunriqi).clicked(this);
        aq.id(R.id.cv_campus_jiehunmingdan).clicked(this);
        aq.id(R.id.cv_campus_jiehunliucheng).clicked(this);
        aq.id(R.id.cv_campus_jiehunjiuxi).clicked(this);
        aq.id(R.id.cv_campus_jiehunlijin).clicked(this);
    }



    private void campusJiehunrenwu() {
        Intent intent = new Intent(WeddingToolActivity.this, WDTaskActivity.class);
        startActivity(intent);
    }

    private void campusJiehunyusuan() {
        Intent intent = new Intent(WeddingToolActivity.this, WDBudgetActivity.class);
        startActivity(intent);
    }

    private void campusJiehundengji() {
        Intent intent = new Intent(WeddingToolActivity.this, WDRegisterActivity.class);
        startActivity(intent);
    }

    private void campusJiehunriqi() {
        Intent intent = new Intent(WeddingToolActivity.this, WDDateActivity.class);
        startActivity(intent);
    }

    private void campusJiehunmingdan() {
        Intent intent = new Intent(WeddingToolActivity.this, WDGuestListActivity.class);
        startActivity(intent);
    }

    private void campusJiehunliucheng() {
        Intent intent = new Intent(WeddingToolActivity.this, WDDayProcessActivity.class);
        startActivity(intent);
    }

    private void campusJiehunjiuxi() {
    }


    private void campusJiehunlijin() {
        Intent intent = new Intent(WeddingToolActivity.this, WDGiftMoneyActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //结婚任务
            case R.id.cv_campus_jiehunrenwu:
                campusJiehunrenwu();
                break;

            //结婚预算
            case R.id.cv_campus_jiehunyusuan:
                campusJiehunyusuan();
                break;

            //结婚登记
            case R.id.cv_campus_jiehundengji:
                campusJiehundengji();
                break;

            //我的婚期
            case R.id.cv_campus_jiehunriqi:
                campusJiehunriqi();
                break;

            //婚宴名单
            case R.id.cv_campus_jiehunmingdan:
                campusJiehunmingdan();
                break;

            //当日流程
            case R.id.cv_campus_jiehunliucheng:
                campusJiehunliucheng();
                break;

            //宾客席位
            case R.id.cv_campus_jiehunjiuxi:
                campusJiehunjiuxi();
                break;

            //礼金记账
            case R.id.cv_campus_jiehunlijin:
                campusJiehunlijin();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FinderActivity"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FinderActivity"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    private long exitTime = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showToast("再按一次退出他她工具");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
