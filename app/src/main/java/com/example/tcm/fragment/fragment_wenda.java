package com.example.tcm.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.tcm.FabiaoActivity;
import com.example.tcm.PostDetailActivity;
import com.example.tcm.R;
import com.example.tcm.adapter.PostDetailAdapter;
import com.example.tcm.entity.PostDetail;
import com.example.tcm.util.Utils;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class fragment_wenda extends Fragment {

    private static final int UI_OPERATION = 1;

    private ListView postDetailListView;

    private List<PostDetail> postDetailList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    PostDetailAdapter adapter = new PostDetailAdapter(getActivity(), R.layout.post_item, postDetailList);
                    postDetailListView.setAdapter(adapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wenda,container,false);

        //设置标题栏
        Toolbar toolbar = view.findViewById(R.id.wenda_toolbar);
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        parent.setSupportActionBar(toolbar);
        //去掉默认的标题
        parent.getSupportActionBar().setDisplayShowTitleEnabled(false);

        postDetailListView = view.findViewById(R.id.lv_wenda);

        //点击发布按钮：跳转到deliver activity
        view.findViewById(R.id.btn_wenda_fabiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FabiaoActivity.class);
                startActivity(intent);
            }
        });

        // 获取帖子数据
        initPostDetail();

        // 点击一项进入帖子详情
        postDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostDetail postDetail = postDetailList.get(position);
                Long pid = postDetail.getPid();
                // 进入帖子详情
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("pid", pid);
                startActivity(intent);
            }
        });

        return view;
    }

    // 从服务器获取帖子数据
    private void initPostDetail() {
        String token = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(getActivity(), "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/post/list")
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "获取帖子数据失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"true".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "获取帖子数据失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                String arrString = jsonObject.get("result").toString();
                postDetailList = JSONObject.parseArray(arrString, PostDetail.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initPostDetail();
    }
}
