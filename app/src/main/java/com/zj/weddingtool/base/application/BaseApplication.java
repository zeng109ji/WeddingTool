package com.zj.weddingtool.base.application;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

/**
 * 应用的基类，封装一些应用全局调用的对象及API（to be continued），
 * 
 * 包括提供全局的Handler对象和Application Context，
 * 
 * 也提供了强杀进程等方法。
 *
 */
public class BaseApplication extends Application {
	
	private static Handler sHandler;
	private static Application sApplication;

    private static ProgressDialog progressDialog;
	
	/**
	 * @return 整个APP可以使用的Handler（为主线程）
	 */
	public static Handler getHandler() {
		checkHandler();
		return sHandler;
	}
	
	/**
	 * @return 整个APP可以使用的Context
	 */
	public static Application getAppContext() {
		checkNull(sApplication);
		return sApplication;
	}
	
	private static void checkNull(Object obj) {
		if (null == obj) {
			throw new RuntimeException("check whether the app has a Application "
					+ "class extends BaseApplication ? or forget to " 
					+ "invoke super class's constructor first!");
		}
	}
	
	private static void checkHandler() {
		if (null == sHandler) {
			sHandler = new Handler(Looper.getMainLooper());
		}
	}
	
	public BaseApplication() {
		sApplication = this;
	}

    public static void showProgressDialog() {
        if(progressDialog==null)
            progressDialog = new ProgressDialog(getAppContext());

        if(progressDialog!=null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
	
	/**
	 * 强杀本进程
	 */
	public static void killProcess() {
		Process.killProcess(Process.myPid());
	}

}
