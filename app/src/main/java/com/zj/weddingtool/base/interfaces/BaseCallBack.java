package com.zj.weddingtool.base.interfaces;

/**
 * 网络请求回调接口
 *
 * Created by ??? on 2015/4/7.
 */
public interface BaseCallBack {
    public void onSuccess();
    public void onFails(int code, String msg);
}
