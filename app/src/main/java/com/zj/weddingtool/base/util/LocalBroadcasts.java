package com.zj.weddingtool.base.util;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.zj.weddingtool.base.application.BaseApplication;

/**
 * 本地广播的封装
 */
public class LocalBroadcasts {

	private LocalBroadcasts() {}
	
	private static LocalBroadcastManager sLocalBroadcastManager;
	private synchronized static void checkLocalBroadcastManagerInstance() {
		if (null == sLocalBroadcastManager) {
			sLocalBroadcastManager = LocalBroadcastManager.getInstance(BaseApplication.getAppContext());
		}
	}
	
	/**
	 * 注册广播，参数<code> actions </code> 为注册的行为
	 */
	public static void registerLocalReceiver(BroadcastReceiver receiver, String...actions) {
        if (null == receiver) {
            return ;
        }
		if (null == actions || actions.length == 0) {
			return ;
		}
		IntentFilter filter = new IntentFilter();
		for (int i = 0; i < actions.length; i++) {
			filter.addAction(actions[i]);
		}
		registerLocalReceiver(receiver, filter);
	}
	
	/**
	 * 注册广播
	 */
	public static void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter...filters) {
        if (null == receiver) {
            return ;
        }
        if (null == filters || filters.length == 0) {
            return ;
        }
		checkLocalBroadcastManagerInstance();
        for (int i = 0; i < filters.length; i++) {
            sLocalBroadcastManager.registerReceiver(receiver, filters[i]);
        }
	}
	
	/**
	 * 注销指定广播
	 */
	public static void unregisterLocalReceiver(BroadcastReceiver receiver) {
		checkLocalBroadcastManagerInstance();
		try {
			sLocalBroadcastManager.unregisterReceiver(receiver);
		} catch (Exception ignore) { }
	}
	
	/**
	 * 提供action发送本地广播
	 */
	public static void sendLocalBroadcast(String action) {
		if (TextUtils.isEmpty(action)) {
			return ;
		}
		checkLocalBroadcastManagerInstance();
		sLocalBroadcastManager.sendBroadcast(new Intent(action));
	}
	
	/**
	 * 提供Intent发送本地广播
	 */
	public static void sendLocalBroadcast(Intent intent) {
        if (null == intent) {
            return ;
        }
		checkLocalBroadcastManagerInstance();
		sLocalBroadcastManager.sendBroadcast(intent);
	}


}
