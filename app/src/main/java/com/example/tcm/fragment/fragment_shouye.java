package com.example.tcm.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.example.tcm.InquiryPescriptionActivity;
import com.example.tcm.InquiryDetailActivity;
import com.example.tcm.R;
import com.example.tcm.adapter.InquiryDetailAdapter;
import com.example.tcm.entity.InquiryDetail;
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

import static android.content.Context.MODE_PRIVATE;

public class fragment_shouye extends Fragment{

    private static final int UI_OPERATION =1;

    private ListView inquiryDetailListView;

    private List<InquiryDetail> inquiryDetailList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    InquiryDetailAdapter adapter = new InquiryDetailAdapter(getActivity(), R.layout.post_item, inquiryDetailList);
                    inquiryDetailListView.setAdapter(adapter);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_shouye,container,false);

        //设置标题栏
        Toolbar toolbar = view.findViewById(R.id.shouye_toolbar);
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        parent.setSupportActionBar(toolbar);
        //去掉默认的标题
        parent.getSupportActionBar().setDisplayShowTitleEnabled(false);

        inquiryDetailListView = view.findViewById(R.id.lv_inquiry);

        //点击发布按钮：跳转到deliver activity
        view.findViewById(R.id.btn_inquiry_fabiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InquiryDetailActivity.class);
                startActivity(intent);
            }
        });


        // 获取问诊列表：如果是普通用户则显示自己的问诊列表，如果是医生则显示咨询他的问诊列表
        String auth = getActivity().getSharedPreferences("MainActivity", Context.MODE_PRIVATE).getString("auth", "");
        if ("0".equals(auth)) {
            // 普通用户
            initMyInquiryList();
        } else if ("1".equals(auth)) {
            // 隐藏发布按钮
            view.findViewById(R.id.btn_inquiry_fabiao).setVisibility(View.GONE);
            // 医生
            initDoctorInquiryList();
        }

        inquiryDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InquiryDetail inquiryDetail = inquiryDetailList.get(position);
                Integer idid = inquiryDetail.getIdid();
                // 进入问诊详情
                Intent intent = new Intent(getActivity(), InquiryPescriptionActivity.class);
                intent.putExtra("idid", idid);
                startActivity(intent);
            }
        });

        return view;

    }


    // 初始化普通用户问诊列表
    private void initMyInquiryList() {
        String token = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(getActivity(), "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/inquiry/detailList")
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "获取问诊数据失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "获取问诊数据失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                String arrString = jsonObject.get("result").toString();
                inquiryDetailList = JSONObject.parseArray(arrString, InquiryDetail.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                handler.sendMessage(message);
            }
        });
    }

    // 初始化医生问诊列表
    private void initDoctorInquiryList() {
        String token = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(getActivity(), "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/inquiry/doctor")
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "获取问诊数据失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "获取问诊数据失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                String arrString = jsonObject.get("result").toString();
                inquiryDetailList = JSONObject.parseArray(arrString, InquiryDetail.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                handler.sendMessage(message);
            }
        });
    }
}
