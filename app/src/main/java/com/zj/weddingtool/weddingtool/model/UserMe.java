package com.zj.weddingtool.weddingtool.model;

import java.io.Serializable;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 用户实体类
 *
 * @author Stone
 * @date 2014-4-24
 */
@SuppressWarnings("serial")
public class UserMe extends BmobUser implements Serializable {


    public static final String USER_TYPE_NORMAL = "普通用户";

    public static final String USER_ROLE_USER = "用户";

    public static final String USER_STATE_LOGOUT = "离线";

    public static final String HBUT_USERNAME_FORMAT = "HBUT_%1s";
    public static final String HBUT_PASSWORD_FORMAT = "HBUT_%1s_*";

    // 电话
    private String phone = "";

    // 任务状态
    private ArrayList<Boolean> completed;

    // 结婚预算
    private ArrayList<Integer> mymoney;

    // 结婚预算项目
    private ArrayList<String> mymoneyItem;

    // 当日流程分组
    private ArrayList<String> processgroup;

    // 当日流程分组+时间
    private ArrayList<String> processchildTime;

    // 当日流程分组-事宜
    private ArrayList<String> processchildThings;

    // 当日流程分组-人员
    private ArrayList<String> processchildPeople;

    // 礼金人
    private ArrayList<String> mylijinname;

    // 礼金数目
    private ArrayList<Integer> mylijin;

    // 礼金退还
    private ArrayList<Boolean> tuilijin;

    // 用户登录状态
    private String state = USER_STATE_LOGOUT;

    // 用户类型
    private String type = USER_TYPE_NORMAL;

    // 用户角色
    private String role = USER_ROLE_USER;

    // 结婚日期
    private ArrayList<String> marrydate;

    // 用户头像
    private BmobFile picUser;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Boolean> getCompleted() {
        return completed;
    }

    public void setCompleted(ArrayList<Boolean> completed) {
        this.completed = completed;
    }

    public ArrayList<Integer> getMymoney() {
        return mymoney;
    }

    public void setMymoney(ArrayList<Integer> mymoney) {
        this.mymoney = mymoney;
    }

    public ArrayList<String> getMymoneyItem() {
        return mymoneyItem;
    }

    public ArrayList<String> getProcessgroup() {
        return processgroup;
    }

    public void setProcessgroup(ArrayList<String> processgroup) {
        this.processgroup = processgroup;
    }

    public ArrayList<String> getProcesschildTime() {
        return processchildTime;
    }

    public void setProcesschildTime(ArrayList<String> processchildTime) {
        this.processchildTime = processchildTime;
    }

    public ArrayList<String> getProcesschildThings() {
        return processchildThings;
    }

    public void setProcesschildThings(ArrayList<String> processchildThings) {
        this.processchildThings = processchildThings;
    }

    public ArrayList<String> getProcesschildPeople() {
        return processchildPeople;
    }

    public void setProcesschildPeople(ArrayList<String> processchildPeople) {
        this.processchildPeople = processchildPeople;
    }

    public ArrayList<String> getMylijinname() {
        return mylijinname;
    }

    public void setMylijinname(ArrayList<String> mylijinname) {
        this.mylijinname = mylijinname;
    }

    public ArrayList<Boolean> getTuilijin() {
        return tuilijin;
    }

    public void setTuilijin(ArrayList<Boolean> tuilijin) {
        this.tuilijin = tuilijin;
    }

    public ArrayList<Integer> getMylijin() {
        return mylijin;
    }

    public void setMylijin(ArrayList<Integer> mylijin) {
        this.mylijin = mylijin;
    }

    public void setMymoneyItem(ArrayList<String> mymoneyItem) {
        this.mymoneyItem = mymoneyItem;
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

    public ArrayList<String> getMarrydate() {
        return marrydate;
    }

    public void setMarrydate(ArrayList<String> marrydate) {
        this.marrydate = marrydate;
    }


}
