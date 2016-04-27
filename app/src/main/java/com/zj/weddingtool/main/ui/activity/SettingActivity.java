package com.zj.weddingtool.main.ui.activity;

import android.os.Bundle;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;

/**
 * Created by zj on 15/5/6.
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
    }

    private void initView() {

    }

}
