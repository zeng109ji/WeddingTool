package com.zj.weddingtool.main.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zj.weddingtool.base.application.BaseApplication;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.main.model.User;

import java.util.HashMap;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 登录管理
 * <p/>
 * Created by ??? on 2015/4/1.
 */
public class LoginManager {

    private static final String TAG = "LoginManager";

    public static final String SP_KEY_LOGIN_METHOD = "loginMethod";
    public static final String SP_KEY_USERNAME = "username";
    public static final String SP_KEY_PASSWORD = "password";

    public static final String SP_NAME_LOGIN_BASE = "Login_Method_User";
//    public static final String SP_NAME_LOGIN_BY_XIAOCAI = SP_NAME_LOGIN_BASE + "XiaoCai";
//    public static final String SP_NAME_LOGIN_BY_SCHOOL = SP_NAME_LOGIN_BASE + "School";

//    public static final int LOGIN_M_XIAOCAI = 0;
//    public static final int LOGIN_M_SCHOOL = 1;

    //当前用户选择的登陆方式
    public int curLoginMethod = 0;

    private static LoginManager instance = new LoginManager();

    private LoginManager() {
    }

    public static LoginManager getInstance() {
        return instance;
    }

    /**
     * 保存用户登陆信息
     *
     * @param username
     * @param password
     * @param loginMethod 登陆方式
     */
    public void saveLoginInfo(String username, String password,
                              int loginMethod) {

        //保存登陆方式
        saveLoginMethod(loginMethod);

        //保存用户信息
        saveUserInfo(username, password, loginMethod);
    }

    /**
     * 获取用户登陆信息
     *
     * @return
     */
    public HashMap<String, String> getLoginInfo() {
        HashMap<String, String> hashMap;
        SharedPreferences sp = BaseApplication.getAppContext().getSharedPreferences(SP_NAME_LOGIN_BASE, Context.MODE_PRIVATE);
        if (sp.contains(SP_KEY_LOGIN_METHOD)) {
            int loginMehthod = sp.getInt(SP_KEY_LOGIN_METHOD, -1);
            if (loginMehthod != -1) {
                hashMap = getUserInfo(loginMehthod);
                return hashMap;
            }
        }

        return null;
    }

    /**
     * 根据用户选择的登陆方式来确定自动填充的用户名和密码
     *
     * @param loginMethod 登陆的方式
     */
    public HashMap<String, String> getUserInfo(int loginMethod) {
        String spFileName = getSpFileName(loginMethod);
        SharedPreferences sp = BaseApplication.getAppContext().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        HashMap<String, String> map = new HashMap<>();
        map.put(SP_KEY_USERNAME, sp.getString(SP_KEY_USERNAME, ""));
        map.put(SP_KEY_PASSWORD, sp.getString(SP_KEY_PASSWORD, ""));
        map.put(SP_KEY_LOGIN_METHOD, loginMethod+"");
        return map;
    }


    private String getSpFileName(int loginMethod) {
        String spFileName = "";
        spFileName = SP_NAME_LOGIN_BASE;
        return spFileName;
    }


    /**
     * 根据用户选择的登陆方式保存用户的登陆记录
     *
     * @param username    用户民
     * @param password    密码
     * @param loginMethod 登陆的方式
     */
    private void saveUserInfo(String username, String password,
                              int loginMethod) {

        String spFileName = getSpFileName(loginMethod);
        SharedPreferences sp = BaseApplication.getAppContext().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_KEY_USERNAME, username);
        editor.putString(SP_KEY_PASSWORD, password);
        editor.putInt(SP_KEY_LOGIN_METHOD, loginMethod);
        editor.commit();
    }

    /**
     * 保存用户当前选择的登陆方式
     *
     * @param method
     */
    private void saveLoginMethod(int method) {
        SharedPreferences sp = BaseApplication.getAppContext().getSharedPreferences(SP_NAME_LOGIN_BASE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putInt(SP_KEY_LOGIN_METHOD, 0);
        editor.commit();
    }


    /**
     * 更新用户状态
     *
     * @param state 用户状态
     */
    public void updUserState(String state) {
        final User user = BmobUser.getCurrentUser(BaseApplication.getAppContext(), User.class);
        if (null != user) {
            user.setState(state);
            user.update(BaseApplication.getAppContext(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, String.format("%s : 登录成功", user.getUsername()));
                }

                @Override
                public void onFailure(int i, String s) {
                    ToastUtils.showToast("用户状态更新失败");
                }
            });
        }
    }

}
