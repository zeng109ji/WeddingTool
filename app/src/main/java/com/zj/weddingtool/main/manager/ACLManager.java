package com.zj.weddingtool.main.manager;

import com.zj.weddingtool.base.application.BaseApplication;
import com.zj.weddingtool.base.config.BmobConfig;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.main.model.User;

import cn.bmob.v3.BmobUser;

/**
 * Created by stone on 15/4/4.
 */
public class ACLManager {

    public static final int ACL_LOGIN_XIAOCAI = 0x000;
    public static final int ACL_LOGIN_SCHOOL = 0x001;
    public static final String ACL_LOGIN_TOAST = "抱歉，你已经被限制登录，请稍后再试";

    public static final int ACL_SHOP_COMMENT = 0x100;
    public static final int ACL_SHOP_CALL = 0x101;
    public static final int ACL_SHOP_BUY = 0x102;
    public static final String ACL_SHOP_TOAST_COMMENT = "抱歉， 你已被限制评论该店铺，请稍后再试";
    public static final String ACL_SHOP_TOAST_CALL = "抱歉， 你已被限制呼入，请稍后再试";
    public static final String ACL_SHOP_TOAST_BUY = "抱歉， 你已被限制购买，请稍后再试";


    public static final int ACL_COMMENT_HIDDEN_NAME = 0x201;
    public static final String ACL_COMMENT_TOAST_HIDDEN_NAME = "抱歉，你的等级不支持匿名";

    /**
     * 访问控制
     *
     * @param code
     * @return 有权限返回true, 无权限返回false
     */
    public static boolean checkACL(int code) {

        // 检测账户控制系统是否启用
        if (!BmobConfig.isOpenACL) {
            return true;
        }

        boolean isHasAccess = false;

        switch (code) {

            case ACL_LOGIN_SCHOOL:
            case ACL_LOGIN_XIAOCAI:
                isHasAccess = checkACLLogin();
                break;

            case ACL_SHOP_COMMENT:
            case ACL_SHOP_CALL:
            case ACL_SHOP_BUY:
                isHasAccess = checkACLShop();
                break;

            case ACL_COMMENT_HIDDEN_NAME:
                isHasAccess = checkHiddenName();
                break;

            default:
                isHasAccess = true;
                break;

        }

        if (!isHasAccess) {
            showToast(code);
        }

        return isHasAccess;
    }


    /**
     * 显示提示信息
     *
     * @param code
     */
    private static void showToast(int code) {
        switch (code) {
            case ACL_LOGIN_SCHOOL:
            case ACL_LOGIN_XIAOCAI:
                ToastUtils.showToast(ACL_LOGIN_TOAST);
                break;


            case ACL_SHOP_COMMENT:
                ToastUtils.showToast(ACL_SHOP_TOAST_COMMENT);
                break;
            case ACL_SHOP_CALL:
                ToastUtils.showToast(ACL_SHOP_TOAST_CALL);
                break;
            case ACL_SHOP_BUY:
                ToastUtils.showToast(ACL_SHOP_TOAST_BUY);
                break;


            case ACL_COMMENT_HIDDEN_NAME:
                ToastUtils.showToast(ACL_COMMENT_TOAST_HIDDEN_NAME);
                break;

            default:
                break;

        }

    }


    /**
     * 是否可以登录
     *
     * @return
     */
    private static boolean checkACLLogin() {
        //TODO 修改判断逻辑
        User user = BmobUser.getCurrentUser(BaseApplication.getAppContext(), User.class);
        if (null == user) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 是否可以进行店铺操作
     *
     * @return
     */
    private static boolean checkACLShop() {
        User user = BmobUser.getCurrentUser(BaseApplication.getAppContext(), User.class);
        if (null == user) {
            return false;
        }
        String state = user.getState();
        if (state.equals(User.USER_STATE_LOGOUT) || state.equals(User.USER_TYPE_BLACK)) {
            return false;
        }

        return true;
    }

    /**
     * 是否可以匿名评论
     *
     * @return
     */
    private static boolean checkHiddenName() {
        User user = BmobUser.getCurrentUser(BaseApplication.getAppContext(), User.class);
        if (null == user) {
            return false;
        }
        //String type = user.getType();
        //String role = user.getRole();

        return false;

    }


}
