package com.zj.weddingtool.weddingtool.ui.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTThrowable;
import com.brtbeacon.sdk.IBle;
import com.brtbeacon.sdk.callback.BRTBeaconManagerListener;
import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.zj.weddingtool.main.manager.LoginManager;
import com.zj.weddingtool.weddingtool.model.UserMe;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.ShareSDK;

/**
 *
 * @author zj
 * @date 2016-4-19
 */
public class WeddingToolActivity extends BaseActivity implements OnClickListener {

    private String sPhone = new String();
    private String uPhone = new String();

    private String smmarrydate = "";

    private TextView tx_marryDate;
    private TextView tx_days;

    private BRTBeaconManager beaconManager;

    //通知管理器
    private NotificationManager nm;
    //通知显示内容
    private PendingIntent pd;

    @SuppressWarnings("unused")
    private static final String TAG = "WeddingToolActivity";
//    private static final int RESULT_REQUEST_AUTH_HBUT = 1007;

    AQuery aq = new AQuery(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BTRSDK_init();

        setContentView(R.layout.activity_weddingtool);
        ShareSDK.initSDK(this);

     //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        findAll();
        initView();

        //ToolApplication app = (ToolApplication) getApplication();
        //beaconManager = app.getBRTBeaconManager();
        beaconManager.setBRTBeaconManagerListener(beaconManagerListener);
    }

    private void BTRSDK_init(){
        //获取单例
        beaconManager = BRTBeaconManager.getInstance(this);
        // 注册应用 APPKEY申请:http://brtbeacon.com/main/index.shtml
        beaconManager.registerApp("2871ba1a272d44ecbaf6eb68e588bfde");
        // 开启Beacon扫描服务
        beaconManager.startService();
    }

    private void initView() {
        aq.id(R.id.cv_campus_jiehunrenwu).clicked(this);
        aq.id(R.id.cv_campus_jiehunyusuan).clicked(this);
        aq.id(R.id.cv_campus_jiehundengji).clicked(this);
        aq.id(R.id.cv_campus_jiehunriqi).clicked(this);
        aq.id(R.id.cv_campus_jiehunmingdan).clicked(this);
        aq.id(R.id.cv_campus_jiehunliucheng).clicked(this);
        aq.id(R.id.cv_campus_jiehunjiuxi).clicked(this);
        aq.id(R.id.cv_campus_jiehunlijin).clicked(this);

        tx_marryDate = (TextView) findViewById(R.id.first_marrydate);
        tx_days = (TextView) findViewById(R.id.first_marrydays);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this,WeddingToolActivity.class);
        pd = PendingIntent.getActivity(WeddingToolActivity.this, 0, intent, 0);
    }

    BRTBeaconManagerListener beaconManagerListener = new BRTBeaconManagerListener() {
        //BASE Notification ID
        private int Notification_ID_BASE = 110;
        private Notification baseNF;

        @Override
        public void onUpdateBeacon(ArrayList<BRTBeacon> beacons) {
            // Beacon信息更新
            if(beacons.size()>0) {
                for(int i=0;i<beacons.size();i++) {
                    if(beacons.get(i).name!=null) {
                        ToastUtils.showToast("设备" + beacons.get(i).name + "的电量为" + beacons.get(i).battery);

                        //更新通知
                        //比如状态栏提示有一条新短信，还没来得及查看，又来一条新短信的提示。
                        //此时采用更新原来通知的方式比较。
                        //(再重新发一个通知也可以，但是这样会造成通知的混乱，而且显示多个通知给用户，对用户也不友好)
                        //baseNF.setLatestEventInfo(WeddingToolActivity.this, "Title02", "Content02", pd);
                        //nm.notify(Notification_ID_BASE, baseNF);
                    }
                }
            }
        }

        @Override
        public void onNewBeacon(BRTBeacon beacon) {
            // 发现一个新的Beacon
            ToastUtils.showToast("新接入设备"+beacon.name);

            //if(!"".equals(beacon.name)) {
            if(beacon.name!=null) {
                //新建状态栏通知
                baseNF = new Notification();

                //设置通知在状态栏显示的图标
                baseNF.icon = R.drawable.ic_app_512;

                //通知时在状态栏显示的内容
                baseNF.tickerText = "新接入设备" + beacon.name;

                //通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
                //如果要全部采用默认值, 用 DEFAULT_ALL.
                //此处采用默认声音
                baseNF.defaults |= Notification.DEFAULT_SOUND;
                baseNF.defaults |= Notification.DEFAULT_VIBRATE;
                baseNF.defaults |= Notification.DEFAULT_LIGHTS;

                //让声音、振动无限循环，直到用户响应
                //baseNF.flags |= Notification.FLAG_INSISTENT;

                //通知被点击后，自动消失
                baseNF.flags |= Notification.FLAG_AUTO_CANCEL;

                //点击'Clear'时，不清楚该通知(QQ的通知无法清除，就是用的这个)
                //baseNF.flags |= Notification.FLAG_NO_CLEAR;


                //第二个参数 ：下拉状态栏时显示的消息标题 expanded message title
                //第三个参数：下拉状态栏时显示的消息内容 expanded message text
                //第四个参数：点击该通知时执行页面跳转
                baseNF.setLatestEventInfo(WeddingToolActivity.this, "Title01", "Content01", pd);

                //发出状态栏通知
                //The first parameter is the unique ID for the Notification
                // and the second is the Notification object.
                nm.notify(Notification_ID_BASE, baseNF);
            }
        }

        @Override
        public void onGoneBeacon(BRTBeacon beacon) {
            // 一个Beacon消失
            ToastUtils.showToast("离开设备"+beacon.name);
        }

        @Override
        public void onError(BRTThrowable throwable) {
            // 一个Beacon消失
        }
    };



    //填充数据
    private void initDater(String[] dates) {
        smmarrydate = dates[0];
        if (smmarrydate == null) {
            smmarrydate = " ";

            tx_marryDate.setText("我的婚期：未定");
            tx_days.setText("0天");
        }else {
            Date currdate = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            ParsePosition ppst = new ParsePosition(0);
            Date marryDate = sdf.parse(smmarrydate, ppst);

            tx_marryDate.setText("我的婚期：" + smmarrydate.split("-")[0] + "年" + smmarrydate.split("-")[1] + "月" + smmarrydate.split("-")[2] + "日");

            tx_days.setText(getGapCount(currdate,marryDate) + "天");
        }
    }

    private void findAll() {
        Log.d(TAG, "从服务器获取用户信息");

        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
        BmobQuery<UserMe> query = new BmobQuery<>();
        query.getObject(this, curUser.getObjectId(), new GetListener<UserMe>() {
            @Override
            public void onSuccess(UserMe userme) {
                String[] temp = new String[7];
                String temp_phone = new String();
                String temp_phone1 = new String();
                ArrayList<String> mdate = new ArrayList<String>();

                mdate = userme.getMarrydate();

                temp_phone = userme.getPhone();
                temp_phone1 = userme.getUserphone();

                if (temp_phone.length() > 0) {
                    sPhone = temp_phone;

                    if (temp_phone1 != null) {
                        uPhone = temp_phone1;
                        Log.d(TAG, "temp_phone1 != null :" + uPhone);
                    } else {
                        uPhone = temp_phone;
                        Log.d(TAG, "temp_phone1 == null :" + uPhone);
                    }
                } else {
                    if (temp_phone1 != null) {
                        uPhone = temp_phone1;
                        Log.d(TAG, "uPhone != null :" + uPhone);
                    } else {
                        uPhone = temp_phone;
                        Log.d(TAG, "uPhone == null :" + uPhone);
                    }
                }
                Log.d(TAG, "sPhone" + sPhone);
                ////////获取婚期
                if (mdate == null) {
                    for (int i = 0; i < 7; i++) {
                        temp[i] = "";
                    }
                    Log.d(TAG, "没有信息在云端");
                } else {
                    temp = (String[]) mdate.toArray(new String[mdate.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + mdate.size() + "  " + " 条" + userme.getObjectId());
                }
                initDater(temp);
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showToast("从云端同步数据获取失败，请稍后再试");
            }
        });
    }

    private void saveAll(){
        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);

        UserMe query = new UserMe();
        query.setPhone(sPhone);
        if(uPhone != null)
            query.setUserphone(uPhone);
        query.update(this, curUser.getObjectId(), new UpdateListener() {    //query.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                //   ToastUtils.showToast("添加数据成功，返回objectId为：");
                Log.d(TAG, "更新数据成功 " + sPhone + ";" + uPhone);
            }

            @Override
            public void onFailure(int code, String arg0) {
                // 添加失败
                Log.d(TAG, "更新数据失败");
            }
        });
    }

    private void campusJiehunrenwu() {
        Intent intent = new Intent(WeddingToolActivity.this, WDTaskActivity.class);
        startActivity(intent);
    }

    private void campusJiehunyusuan() {
        Intent intent = new Intent(WeddingToolActivity.this, WDBudgetActivity.class);
        startActivity(intent);
    }

    private void campusJiehundengji() {
        Intent intent = new Intent(WeddingToolActivity.this, WDRegisterActivity.class);
        startActivity(intent);
    }

    private void campusJiehunriqi() {
        Intent intent = new Intent(WeddingToolActivity.this, WDDateActivity.class);
        startActivity(intent);
    }

    private void campusJiehunmingdan() {
        Intent intent = new Intent(WeddingToolActivity.this, WDGuestListActivity.class);
        startActivity(intent);
    }

    private void campusJiehunliucheng() {
        Intent intent = new Intent(WeddingToolActivity.this, WDDayProcessActivity.class);
        startActivity(intent);
    }

    private void campusJiehunjiuxi() {
        Intent intent = new Intent(WeddingToolActivity.this, WDFeastLayoutActivity.class);
        startActivity(intent);
    }


    private void campusJiehunlijin() {
        Intent intent = new Intent(WeddingToolActivity.this, WDGiftMoneyActivity.class);
        startActivity(intent);
    }

    /**
     * 判断两个日期之间的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //结婚任务
            case R.id.cv_campus_jiehunrenwu:
                campusJiehunrenwu();
                break;

            //结婚预算
            case R.id.cv_campus_jiehunyusuan:
                campusJiehunyusuan();
                break;

            //结婚登记
            case R.id.cv_campus_jiehundengji:
                campusJiehundengji();
                break;

            //我的婚期
            case R.id.cv_campus_jiehunriqi:
                campusJiehunriqi();
                break;

            //婚宴名单
            case R.id.cv_campus_jiehunmingdan:
                campusJiehunmingdan();
                break;

            //当日流程
            case R.id.cv_campus_jiehunliucheng:
                campusJiehunliucheng();
                break;

            //宾客席位
            case R.id.cv_campus_jiehunjiuxi:
                campusJiehunjiuxi();
                break;

            //礼金记账
            case R.id.cv_campus_jiehunlijin:
                campusJiehunlijin();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    public void onResume() {
        beaconManager.startRanging();
        super.onResume();
        MobclickAgent.onPageStart("FinderActivity"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        beaconManager.stopRanging();
        super.onPause();
        MobclickAgent.onPageEnd("FinderActivity"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
        saveAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_finish_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit_log:
                LoginManager.getInstance().saveLoginInfo("", "", 0);//清除用户名和密码
                ShareSDK.stopSDK(this);
                finish();
                saveAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private long exitTime = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showToast("再按一次退出他她工具");
            exitTime = System.currentTimeMillis();
        } else {
            ShareSDK.stopSDK(this);
            finish();
            saveAll();
        }
    }

    /**
     * 创建Beacon连接需要传递此参数
     * @return IBle
     */
    public IBle getIBle() {
        return beaconManager.getIBle();
    }

    /**
     * 获取Beacon管理对象
     *
     * @return BRTBeaconManager
     */
    public BRTBeaconManager getBRTBeaconManager() {
        return beaconManager;
    }

}
