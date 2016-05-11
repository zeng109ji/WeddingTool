package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareListToPictureActivity extends BaseActivity{
    /** Called when the activity is first created. */
    private WebView webview;
    public static final String TAG = "ShareToPictureActivity";
    private toHtml_Feast mhtml_feast = new toHtml_Feast();
    private toHtml_process mhtml_process = new toHtml_process();
    private ArrayList<String> feast_group=new ArrayList<String>();
    private ArrayList<ArrayList<String>> generals = new ArrayList<ArrayList<String>>();
    private Bundle bundle;
    private Integer share_num = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pic);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = this.getIntent().getExtras();

        share_num = bundle.getInt("share_number");

        if(share_num == 0) {
            feast_group = bundle.getStringArrayList("feast_froup");
            if (feast_group.size() > 0) {
                Log.d(TAG, "feast_group is " + feast_group.size());
                for (int i = 0; i < feast_group.size(); i++) {
                    generals.add(bundle.getStringArrayList("feast" + i));
                }
            }
        }else if(share_num == 1)
        {
            feast_group = bundle.getStringArrayList("process_group");
            if (feast_group.size() > 0) {
                Log.d(TAG, "feast_group is " + feast_group.size());
                for (int i = 0; i < feast_group.size(); i++) {
                    generals.add(bundle.getStringArrayList("process" + i));
                }
            }
        }
        // 2.2版本以上服务器取数据冲突解决办法========start=========
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                .detectNetwork() // or
                        // .detectAll()
                        // for
                        // all
                        // detectable
                        // problems
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        //webview = new WebView(this);

        webview = (WebView) findViewById(R.id.picc_3d);
        // 设置WebView属性，能够执行Javascript脚本
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        webview.setWebViewClient(new MvtFlashWebViewClient());
        // 截图用
        webview.setDrawingCacheEnabled(true);
        // 自适应屏幕大小

        settings.setLoadWithOverviewMode(true);
        String url = testCreateHTML();// 载入本地生成的页面
        webview.loadUrl(url);
        webview.setOnTouchListener(new OnTouchListenerHTML5());

        mHandler.sendEmptyMessageDelayed(0, 1000);

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    saveWebviewPic();
                    break;
            }
        }
    };

    public class OnTouchListenerHTML5 implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                webview.loadUrl("javascript:canvasMouseDown(" + event.getX() + "," + event.getY() + ")");
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                webview.loadUrl("javascript:canvasMouseMove(" + event.getX() + "," + event.getY() + ")");
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // webview.loadUrl("javascript:canvasMouseDown("+event.getX()+","+event.getY()+")");
                return true;
            }
            return false;
        }

    }

    public String testCreateHTML() {
        //String name[] = { "桌子", "椅子", "其它" };
        //int num[] = { 1, 4, 1 };
        //float price[] = { 100, 25, 80 };
        String path = "/storage/emulated/0/Pictures/save_html.html";
        if(share_num == 0) {
            mhtml_feast.convert(path);
        }else if(share_num == 1) {
            mhtml_process.convert(path);
        }
        return "file://" + path;
    }

    public void saveWebviewPic() {
        Picture picture = webview.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        picture.draw(c);
        savePic("/storage/emulated/0/Pictures/html.jpg", bmp, 1);
        showShare();//分享社交功能
    }

    // 保存文件
    public static boolean savePic(String path, Bitmap bmp, int quality) {
        if (bmp == null || bmp.isRecycled()) {
            return false;
        }
        Log.d(TAG,"savePic star...");
        File myCaptureFile = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            if (quality == 1) {// jpg
                bmp.compress(Bitmap.CompressFormat.JPEG, 85, bos);
            } else if (quality == 2) {// png
                bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } else if (quality == 3) {// 发微薄用
                bmp.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            } else if (quality == 5) {// jpg
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            try {
                bos.flush();
                bos.close();
                // writeEixf(path);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d(TAG,"savePic err 1");
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.d(TAG,"savePic err 2");
            e.printStackTrace();
        }
        return true;
    }

    // Web视图
    private class MvtFlashWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub


            super.onPageFinished(view, url);
        }
    }


     class toHtml_Feast {
        private static final String mHtmlHead = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>无标题文档</title></head><body></table><p> </p>"
                + "<table width=\"disp_width\" height=\"36\" border=\"1\">  <caption>  <strong>宾客席位</strong>  <br />  姓名：name    日期： day <br /> </caption> "
                + "<tr> <td width=\"80\" height=\"30\">席位类别</td>    <td width=\"40\">桌数</td>    <td>宾客名单</td>  </tr>";
        private static final String mHtmlItem = "<tr> <td height=\"30\">name</td>    <td>price</td>    <td>num</td>  </tr>";
        private static final String mHtmlEnd = "</table></body></html>";

        private void convert(String path/*, String name[], int num[], float price[]*/) {
            DisplayMetrics  dm = new DisplayMetrics();
            //取得窗口属性
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            //窗口的宽度
            int screenWidth = dm.widthPixels;

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            String currentDate = sdf.format(date);  //当期日期

            try {
                String result = mHtmlHead;
                result = result.replace("disp_width",""+(screenWidth/3));
                result = result.replace("name","username");
                result = result.replace("day",currentDate);

                int temp_total_feast = 0;
                for (int i = 0; i < feast_group.size(); i++) {
                    for(int j = 0; j < generals.get(i).size();j++) {
                        String mid = new String(mHtmlItem);

                        temp_total_feast++;

                        mid = mid.replace("name", feast_group.get(i));
                        mid = mid.replace("price", "" + temp_total_feast);
                        mid = mid.replace("num", "" + generals.get(i).get(j).replace("--",";"));
                        result += mid;
                    }
                }
                /*
                for (int i = 0; i < name.length; i++) {
                    String mid = new String(mHtmlItem);
                    mid = mid.replace("name", name[i]);
                    mid = mid.replace("price", "" + num[i]);
                    mid = mid.replace("num", "" + price[i]);
                    result += mid;
                }
                for (int i = 0; i < 20; i++) {
                    String mid = new String(mHtmlItem);
                    mid = mid.replace("name", name[0]+i);
                    mid = mid.replace("price", "" + num[0]);
                    mid = mid.replace("num", "" + price[0]);
                    result += mid;
                }
                */
                result += mHtmlEnd;
                saveStringToFile(path, result);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private boolean saveStringToFile(String path, String content) {
            // FileWriter fw = new FileWriter(path);
            // MTDebug.startCount();
            // ByteBuffer dst = ByteBuffer.allocate(content.length() * 4);

            try {
                FileOutputStream fos = new FileOutputStream(path);
                // 把长宽写入头部
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }
    }

    class toHtml_process {
        private static final String mHtmlHead = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>无标题文档</title></head><body></table><p> </p>"
                + "<table width=\"disp_width\" height=\"36\" border=\"1\">  <caption>  <strong>婚期当天流程</strong>  <br />  姓名：name    日期： day <br /> </caption> "
                + "<tr> <td width=\"40\" height=\"30\">时间</td>    <td>事宜</td>    <td>人员</td>  </tr>";
        private static final String mHtmlItem = "<tr> <td height=\"30\">name</td>    <td>price</td>    <td>num</td>  </tr>";
        private static final String mHtmlEnd = "</table></body></html>";

        private void convert(String path/*, String name[], int num[], float price[]*/) {
            DisplayMetrics  dm = new DisplayMetrics();
            //取得窗口属性
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            //窗口的宽度
            int screenWidth = dm.widthPixels;

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            String currentDate = sdf.format(date);  //当期日期

            try {
                String result = mHtmlHead;
                result = result.replace("disp_width",""+(screenWidth/3));
                result = result.replace("name","username");
                result = result.replace("day",currentDate);

                for (int i = 0; i < feast_group.size(); i++) {
                    for(int j = 0; j < generals.get(i).size();j++) {
                        String mid = new String(mHtmlItem);

                        mid = mid.replace("name", generals.get(i).get(j).split("--")[0]);
                        mid = mid.replace("price", generals.get(i).get(j).split("--")[1]);
                        mid = mid.replace("num", generals.get(i).get(j).split("--")[2]);
                        result += mid;
                    }
                }
                /*
                for (int i = 0; i < name.length; i++) {
                    String mid = new String(mHtmlItem);
                    mid = mid.replace("name", name[i]);
                    mid = mid.replace("price", "" + num[i]);
                    mid = mid.replace("num", "" + price[i]);
                    result += mid;
                }
                for (int i = 0; i < 20; i++) {
                    String mid = new String(mHtmlItem);
                    mid = mid.replace("name", name[0]+i);
                    mid = mid.replace("price", "" + num[0]);
                    mid = mid.replace("num", "" + price[0]);
                    result += mid;
                }
                */
                result += mHtmlEnd;
                saveStringToFile(path, result);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private boolean saveStringToFile(String path, String content) {
            // FileWriter fw = new FileWriter(path);
            // MTDebug.startCount();
            // ByteBuffer dst = ByteBuffer.allocate(content.length() * 4);

            try {
                FileOutputStream fos = new FileOutputStream(path);
                // 把长宽写入头部
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        //oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本，啦啦啦~");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/storage/emulated/0/Pictures/html.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
// 启动分享GUI
        oks.show(this);
    }
}