package com.zj.weddingtool.base.application;

import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.bugly.crashreport.CrashReport;

public class ToolApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        initBmobProFile();
        initBugly();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 整体摧毁的时候调用这个方法
    }

    private void initBmobProFile() {
        //Bmob 自定义文件保存路径
        BmobConfiguration config = new BmobConfiguration.Builder(this).customExternalCacheDir("Shop/bmob").build();
        BmobPro.getInstance(this).initConfig(config);
    }

    private void initBugly() {
        CrashReport.initCrashReport(this, "900011135", false);
    }


}
