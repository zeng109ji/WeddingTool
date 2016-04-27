package com.zj.weddingtool.weddingtool.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.weddingtool.model.overlayutil.OverlayManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 2016-4-19
 *
 * @author zj
 */
public class WDRegisterActivity extends BaseActivity {

    private static final String TAG = WDRegisterActivity.class.getSimpleName();

    private ListView lv;
    private ListDengJiChuAdapter mlAdapter;
    private List<HashMap<String, Object>> mlist = null;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private PoiSearch poiSearch;

    /** 搜索关键词 */
    private final String keyword = "婚姻登记处";
    /** 每页容量50 */
    private final int pageCapacity = 50;
    /** 第一页 */
    private final int pageNum = 0;
    /** 搜索半径10km */
    private final int radius = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 介绍如何使用个性化地图，需在MapView 构造前设置个性化地图文件路径
        // 注: 设置个性化地图，将会影响所有地图实例。
        // MapView.setCustomMapStylePath("个性化地图config绝对路径");
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplicationContext());
        setContentView(R.layout.activity_jhdengji);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
        Intent intent = getIntent();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
            mMapView = new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
        } else {
            mMapView = new MapView(this, new BaiduMapOptions());
        }
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();
        */

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        LatLng cenpt = new LatLng(30.581256,114.311882);//武汉的经纬度
        MapStatusUpdate mapStatus = MapStatusUpdateFactory.newLatLngZoom(cenpt, 12);
        mBaiduMap.setMapStatus(mapStatus);


        // 初始化搜索模块
        poiSearch = PoiSearch.newInstance();
        // 注册搜索事件监听
        poiSearch.setOnGetPoiSearchResultListener(new PoiSearchResultListener());

        // 搜索该坐标附近的
        poiSearch.searchNearby(new PoiNearbySearchOption().keyword(keyword)
                .location(new LatLng(30.581256, 114.311882))
                .pageCapacity(pageCapacity).pageNum(pageNum).radius(radius));

        initView();
    }


    //初始化视图控件
    private void initView() {

        /* 实例化各个控件 */
        lv = (ListView) findViewById(R.id.listView_dengji);

        mlist = new ArrayList<HashMap<String, Object>>();

        TypedArray typedArray = getResources().obtainTypedArray(R.array.jhdengjichuall);
        //for (int i = 0; i < typedArray.length(); i++) {
        //    typedArray.getResourceId(i, 0);
        //    Log.d(TAG, getResources().getStringArray(typedArray.getResourceId(i,0))[0]);
        //    Log.d(TAG, getResources().getStringArray(typedArray.getResourceId(i,0))[1]);
        //}
        for (int i = 0; i < typedArray.length(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("djcname", getResources().getStringArray(typedArray.getResourceId(i,0))[0]);
            map.put("djcaddr", getResources().getStringArray(typedArray.getResourceId(i,0))[1]);
            map.put("djctimeday", getResources().getStringArray(typedArray.getResourceId(i,0))[2]);
            map.put("djctimehour", getResources().getStringArray(typedArray.getResourceId(i,0))[3]);
            map.put("djcphone", getResources().getStringArray(typedArray.getResourceId(i,0))[4]);
            map.put("djc_latitude",getResources().getStringArray(typedArray.getResourceId(i,0))[5]);
            map.put("djc_longitude",getResources().getStringArray(typedArray.getResourceId(i,0))[6]);
            mlist.add(map);
        }
        // 实例化自定义的MyAdapter
       mlAdapter = new ListDengJiChuAdapter(this, mlist, new String[] { "djcname", "djcaddr", "djctimeday","djctimehour", "djcphone" },
                new int[] {R.id.djcname, R.id.djcaddr, R.id.djctimeday, R.id.djctimehour, R.id.btn_phone});

        // 绑定Adapter
        lv.setAdapter(mlAdapter);

    }



    @Override
    public void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
        MobclickAgent.onPageEnd("AboutActivity"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
        MobclickAgent.onPageStart("AboutActivity"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 销毁搜索模块
        poiSearch.destroy();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
    }

    private class PoiSearchResultListener implements OnGetPoiSearchResultListener {
        @Override
        public void onGetPoiDetailResult(PoiDetailResult result) {
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            } else {
                Toast.makeText(getApplicationContext(), result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
                Log.d(result.getName(), " " + result.getLocation().latitude + " " + result.getLocation().longitude);
            }
        }

        @Override
        public void onGetPoiResult(PoiResult result) {
            if ((result == null)
                    || (result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND)) {
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                MyPoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result);
                overlay.addToMap();
                // 缩放地图，使所有Overlay都在合适的视野内
                overlay.zoomToSpan();
                mMapView.onResume();
            //    return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            }
        }
    }

    /**
     * 覆盖物
     * 思路如下：
     * 这里通过继承OverlayManager这个类，改写了getOverlayOptions()和onMarkerClick()这两个方法
     * 通过官方demo中给出的icon_gconding，在代码中给图片加上数字，达到需要的效果
     * 每次搜索最多只能设置50，另外Bitmap过多会导致OOM。
     */
    private class MyPoiOverlay extends OverlayManager {
        private PoiResult poiResult = null;

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        public void setData(PoiResult poiResult) {
            this.poiResult = poiResult;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.getExtraInfo() != null) {
                int index = marker.getExtraInfo().getInt("index");
                PoiInfo poi = poiResult.getAllPoi().get(index);

                // 详情搜索
                poiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUid(poi.uid));
                return true;
            }
            return false;
        }

        @Override
        public List<OverlayOptions> getOverlayOptions() {
            if ((this.poiResult == null)
                    || (this.poiResult.getAllPoi() == null))
                return null;
            ArrayList<OverlayOptions> arrayList = new ArrayList<OverlayOptions>();
            for (int i = 1; i < this.poiResult.getAllPoi().size(); i++) {
                if (this.poiResult.getAllPoi().get(i).location == null)
                    continue;
                // 给marker加上标签
                Bundle bundle = new Bundle();
                bundle.putInt("index", i);
                arrayList.add(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(setNumToIcon(i))).extraInfo(bundle)
                        .position(this.poiResult.getAllPoi().get(i).location));
            }
            return arrayList;
        }

        @Override
        public boolean onPolylineClick(Polyline polyline) {
            // TODO Auto-generated method stub
            return false;
        }
        /**
         * 往图片添加数字
         */
        private Bitmap setNumToIcon(int num) {
            BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(
                    R.drawable.ic_poi);
            Bitmap bitmap = bd.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            int widthX;
            int heightY = 0;
            if (num < 10) {
                paint.setTextSize(30);
                widthX = 8;
                heightY = 6;
            } else {
                paint.setTextSize(20);
                widthX = 11;
            }

            canvas.drawText(String.valueOf(num),
                    ((bitmap.getWidth() / 2) - widthX),
                    ((bitmap.getHeight() / 2) + heightY), paint);
            return bitmap;
        }

    }

    public class ListDengJiChuAdapter extends BaseAdapter {

        private Context mcontext;
        private LayoutInflater mInflater = null;
        private List<HashMap<String, Object>> mmlist = null;
        private String keyString[] = null;
        private int idValue[] = null;// id值
        private String itemString = null; // 记录每个item中textview的值

        public ListDengJiChuAdapter(Context context, List<HashMap<String, Object>> list, String[] from, int[] to) {
            this.mcontext = context;
            this.mmlist = list;
            mInflater = LayoutInflater.from(context);
            keyString = new String[from.length];
            idValue = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, idValue, 0, to.length);

            initDate();
        }

        public void initDate() {

        }


        @Override
        public int getCount() {
            return mmlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mmlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListDengJiChuHolder holder = null;
            if (holder == null) {
                holder = new ListDengJiChuHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_dengjichu_list, null);
                }

                holder.DengJiChuName = (TextView) convertView.findViewById(R.id.djcname);
                holder.DengJiChuAddr = (TextView) convertView.findViewById(R.id.djcaddr);
                holder.DengJiChuTimeDay = (TextView) convertView.findViewById(R.id.djctimeday);
                holder.DengJiChuTimeHour = (TextView) convertView.findViewById(R.id.djctimehour);
                holder.btn_daohang = (Button) convertView.findViewById(R.id.btn_daohang);
                holder.btn_phone = (Button) convertView.findViewById(R.id.btn_phone);
                holder.btn_map = (Button) convertView.findViewById(R.id.btn_map);

                convertView.setTag(holder);
            } else {
                holder = (ListDengJiChuHolder) convertView.getTag();
            }

            HashMap<String, Object> map = mmlist.get(position);
            if (map != null) {
                itemString = (String) map.get(keyString[0]);
                holder.DengJiChuName.setText(itemString);
                itemString = (String) map.get(keyString[1]);
                holder.DengJiChuAddr.setText("地址："+itemString);
                itemString = (String) map.get(keyString[2]);
                holder.DengJiChuTimeDay.setText("时间："+itemString);
                itemString = (String) map.get(keyString[3]);
                holder.DengJiChuTimeHour.setText(itemString);
                itemString = (String) map.get(keyString[4]);
                holder.btn_phone.setText("拨打电话："+itemString);
            }

            final int p = position;

            holder.btn_daohang.setTag(position);
            //给Button添加单击事件  添加Button之后ListView将失去焦点  需要的直接把Button的焦点去掉
            holder.btn_daohang.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showNavi(p);
                    Log.d(TAG, "click btn_daohang");
                }
            });

            holder.btn_phone.setTag(position);
            //给Button添加单击事件  添加Button之后ListView将失去焦点  需要的直接把Button的焦点去掉
            holder.btn_phone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    call(p);
                    Log.d(TAG, "click btn_phone");
                }
            });

            holder.btn_map.setTag(position);
            //给Button添加单击事件  添加Button之后ListView将失去焦点  需要的直接把Button的焦点去掉
            holder.btn_map.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showMap(p);
                    Log.d(TAG, "click btn_map");
                }
            });

            return convertView;
        }

        public void showMap(int position){
            /*
            ImageView img=new ImageView(mcontext);
            img.setImageResource(R.drawable.ic_hbut);
            new AlertDialog.Builder(mcontext).setView(img)
                    .setTitle("名称： "+ mmlist.get(position).get("djcname"))
                    .setMessage(" " + mmlist.get(position).get("djcaddr"))
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            */
            Intent intent = new Intent(WDRegisterActivity.this, WDRegistryBaiduMapActivity.class);
             //绑定一捆数据
            Bundle data = new Bundle();
            data.putString("djcname", mmlist.get(position).get("djcname").toString());
            data.putString("djcaddr", mmlist.get(position).get("djcaddr").toString());
            data.putDouble("djc_longitude", Double.parseDouble(mmlist.get(position).get("djc_longitude").toString()));
            data.putDouble("djc_latitude", Double.parseDouble(mmlist.get(position).get("djc_latitude").toString()));
            intent.putExtras(data);
            //Toast.makeText(getApplicationContext(),"data.size: " + data.size(),Toast.LENGTH_SHORT).show();
            Log.d(TAG, mmlist.get(position).get("djc_longitude").toString() + " " + mmlist.get(position).get("djc_latitude").toString());
            startActivity(intent);
        }

        public void showNavi(int position){

            Intent intent = new Intent(WDRegisterActivity.this, BaiduNaviMainActivity.class);
            //绑定一捆数据
            Bundle data = new Bundle();
            data.putString("djcname", mmlist.get(position).get("djcname").toString());
            data.putString("djcaddr", mmlist.get(position).get("djcaddr").toString());
            data.putDouble("djc_longitude", Double.parseDouble(mmlist.get(position).get("djc_longitude").toString()));
            data.putDouble("djc_latitude", Double.parseDouble(mmlist.get(position).get("djc_latitude").toString()));
            intent.putExtras(data);
            //Toast.makeText(getApplicationContext(),"data.size: " + data.size(),Toast.LENGTH_SHORT).show();
            Log.d(TAG, mmlist.get(position).get("djc_longitude").toString() + " " + mmlist.get(position).get("djc_latitude").toString());
            startActivity(intent);
        }

        public void call(int position){
            Uri uri = Uri.parse("tel:" + mmlist.get(position).get("djcphone").toString());
            Intent it = new Intent(Intent.ACTION_DIAL,uri);
            startActivity(it);
        }
    }

    public static class ListDengJiChuHolder {

        public TextView DengJiChuName;
        public TextView DengJiChuAddr;
        public TextView DengJiChuTimeDay;
        public TextView DengJiChuTimeHour;
        public Button btn_daohang;
        public Button btn_phone;
        public Button btn_map;
    }


}
