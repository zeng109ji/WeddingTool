package com.zj.weddingtool.base.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by ??? on 2015/4/15.
 */
public class UmengActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
