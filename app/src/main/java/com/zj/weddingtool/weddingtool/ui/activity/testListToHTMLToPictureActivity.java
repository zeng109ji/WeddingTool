package com.zj.weddingtool.weddingtool.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.StrictMode;
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

public class testListToHTMLToPictureActivity extends BaseActivity{
    /** Called when the activity is first created. */
    private WebView webview;
    public static final String TAG = "testListToHTMLToPictureActivity";
    private toHtml mhtml = new toHtml();
    private Button btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testlisttohtml);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        webview = (WebView) findViewById(R.id.web_3d);
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

        btn = (Button) findViewById(R.id.btn_save);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"savePic btn!");
                saveWebviewPic();
            }
        });
    }

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
        String name[] = { "桌子", "椅子", "其它" };
        int num[] = { 1, 4, 1 };
        float price[] = { 100, 25, 80 };
        String path = "/Pictures/savehtml.html";
        mhtml.convert(path, name, num, price);
        return "file:///storage/emulated/0" + path;
    }

    public void saveWebviewPic() {
        Picture picture = webview.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        picture.draw(c);
        savePic("/storage/emulated/0/Pictures/html.jpg", bmp, 1);
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


     class toHtml {
        private static final String mHtmlHead = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>无标题文档</title></head><body></table><p> </p>"
                + "<table width=\"566\" height=\"36\" border=\"1\">  <caption>  <strong>标题</strong>  <br />  姓名：a    日期： b <br /> </caption> "
                + "<tr> <td height=\"30\">名称</td>    <td>单价</td>    <td>数量</td>  </tr>";
        private static final String mHtmlItem = "<tr> <td height=\"30\">name</td>    <td>price</td>    <td>num</td>  </tr>";
        private static final String mHtmlEnd = "</table></body></html>";

        private void convert(String path, String name[], int num[], float price[]) {
            try {
                String result = mHtmlHead;
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
}