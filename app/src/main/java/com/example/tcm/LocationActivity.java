package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.tcm.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationActivity extends AppCompatActivity {

    private static final int UI_OPERATION = 1;

    public LocationClient locationClient;

    private TextView locationTextView;
    private MapView mapView;
    private BaiduMap baiduMap;

    private String fullAddress;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    Bundle bundle = msg.getData();
                    Double lag = bundle.getDouble("lat");
                    Double lng = bundle.getDouble("lng");

                    locationTextView.setText(fullAddress);

                    BDLocation location = new BDLocation();
                    location.setLatitude(Double.valueOf(lag));
                    location.setLongitude(Double.valueOf(lng));
                    navigateTo(location);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);

        fullAddress = getIntent().getStringExtra("fullAddress");

        locationTextView = findViewById(R.id.tv_location);
        mapView = findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        // 申请权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LocationActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

        // 请求地点信息
        initLocation(fullAddress);

        // 设置定位点
//        BDLocation location = new BDLocation();
//        location.setLatitude(30.674402);
//        location.setLongitude(104.103668);
//        navigateTo(location);
    }

    // 请求地点信息
    private void initLocation(String fullAddress) {
        // 发送请求获取地点信息
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX + "/address/geoCoder?fullAddress="+fullAddress)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(LocationActivity.this, "获取位置信息失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(LocationActivity.this, "获取位置信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                Double lat = jsonObject.getDouble("lat");
                Double lng = jsonObject.getDouble("lng");

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", lat);
                bundle.putDouble("lng", lng);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    private void requestLocation() {
        locationClient.start();
    }

    // 处理权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void navigateTo(BDLocation location) {
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        // 设置地图的缩放程度
        update = MapStatusUpdateFactory.zoomTo(25f);
        // 以动画形式更新地图状态
        baiduMap.animateMapStatus(update);

        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        // 设置定位点
        baiduMap.setMyLocationData(locationData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}