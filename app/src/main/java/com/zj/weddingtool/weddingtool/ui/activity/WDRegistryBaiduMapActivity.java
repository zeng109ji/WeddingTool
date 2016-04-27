package com.zj.weddingtool.weddingtool.ui.activity;

import android.os.Bundle;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;

/**
 * 2016-4-19
 *
 * @author zj
 */
public class WDRegistryBaiduMapActivity extends BaseActivity {

    private static final String TAG = WDRegistryBaiduMapActivity.class.getSimpleName();
    private static final int MSG_TOAST = 1;
    //private String storeId = "1";
    //private LinearLayout layoutTop;

    private double latx = 30.963175f;
    private double laty = 120.400244f;
    private String lableName = "天堂软件园";


    MapView mMapView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);120.123012,30.296587
        //可以传值
        //lableName = getIntent().getExtras().getString("lableName");
        //latx  = getIntent().getExtras().getFloat("latx");
        //laty  = getIntent().getExtras().getFloat("laty");
        lableName = getIntent().getExtras().getString("djcname");
        latx  = getIntent().getExtras().getDouble("djc_latitude");
        laty  = getIntent().getExtras().getDouble("djc_longitude");

        setView();
        initView();
        setListener();


    }

    public MapView mapView = null;
    public BaiduMap mBaiduMap = null;

    // 定位相关声明
    public LocationClient locationClient = null;
    //自定义图标
    BitmapDescriptor mCurrentMarker = null;
    boolean isFirstLoc = true;// 是否首次定位


    private void initMyLocation()
    {
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(100)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(90.0f)
                .latitude(latx)
                .longitude(laty).build();

        float f = mBaiduMap.getMaxZoomLevel();//19.0 最小比例尺
        //float m = mBaiduMap.getMinZoomLevel();//3.0 最大比例尺
        mBaiduMap.setMyLocationData(locData);
        mBaiduMap.setMyLocationEnabled(true);
        LatLng ll = new LatLng(latx,laty);
        //MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll,f);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, f-2);//设置缩放比例
        mBaiduMap.animateMapStatus(u);
    }


    public void setView() {
        // TODO Auto-generated method stub


        mMapView = new MapView(this, new BaiduMapOptions());
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();
        /*
        SDKInitializer.initialize(getApplicationContext());


        setContentView(R.layout.activity_view_detail_maps);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();
        */
        initMyLocation() ;



        /**
         * 定位
         */
        // 构造定位数据



        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);

        // 开发者可根据自己实际的业务需求，利用标注覆盖物，在地图指定的位置上添加标注信息。具体实现方法如下：
        //定义Maker坐标点
        LatLng point = new LatLng(latx, laty);


        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_poi);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);


        //文字，在地图中也是一种覆盖物，开发者可利用相关的接口，快速实现在地图上书写文字的需求。实现方式如下：
        //定义文字所显示的坐标点
        LatLng llText = new LatLng(latx, laty);
        //构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOption = new TextOptions()
                .bgColor(0xAAFFFF00)
                .fontSize(28)
                .fontColor(0xFFFF00FF)
                .text(lableName)
                .rotate(0)
                .position(llText);
        //在地图上添加该文字对象并显示
        mBaiduMap.addOverlay(textOption);

    }



    public void initView() {
        // TODO Auto-generated method stub
        /*
        layoutTop = (LinearLayout) findViewById(R.id.main_top);

        TextView top_view_text = (TextView) findViewById(R.id.top_view_text);
        top_view_text.setText("地图信息");
        */

    }

    public void setListener() {
        // TODO Auto-generated method stub

    }



    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}
