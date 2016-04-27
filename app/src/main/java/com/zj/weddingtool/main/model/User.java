package com.zj.weddingtool.main.model;

import java.io.Serializable;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 用户实体类
 *
 * @author zj
 * @date 2014-4-24
 */
@SuppressWarnings("serial")
public class User extends BmobUser implements Serializable {

    public static final String SEX_MALE = "男";
    public static final String SEX_FEMALE = "女";

    public static final String USER_TYPE_NORMAL = "普通用户";
    public static final String USER_TYPE_BLACK = "黑名单";

    public static final String USER_ROLE_CUSTOME = "普通";

    public static final String USER_STATE_LOGIN_BY_TATA = "登录[他她]";
    public static final String USER_STATE_LOGOUT = "离线";

    public static final String HBUT_USERNAME_FORMAT = "HBUT_%1s";
    public static final String HBUT_PASSWORD_FORMAT = "HBUT_%1s_*";



    // 用户登录状态
    private String state = USER_STATE_LOGOUT;

    // 用户类型
    private String type = USER_TYPE_NORMAL;

    // 用户角色
    private String role = USER_ROLE_CUSTOME;

    // 用户头像
    private BmobFile picUser;

    // 电话
    private String phone = "";

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BmobFile getPicUser() {
        return picUser;
    }

    public void setPicUser(BmobFile picUser) {
        this.picUser = picUser;
    }


}
