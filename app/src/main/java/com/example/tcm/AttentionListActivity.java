package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tcm.adapter.RecommendDoctorAdapter;
import com.example.tcm.adapter.UserAccAdapter;
import com.example.tcm.entity.RecommendDoctor;
import com.example.tcm.entity.UserAcc;
import com.example.tcm.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttentionListActivity extends AppCompatActivity {

    private static final int UI_OPERATION = 1;

    private ListView userListView;

    private List<UserAcc> userAccList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    // 更新推荐列表
                    UserAccAdapter adapter = new UserAccAdapter(AttentionListActivity.this, R.layout.user_list_item, userAccList);
                    userListView.setAdapter(adapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_list);

        userListView = findViewById(R.id.lv_user);

        // 初始化关注列表
        initList();

        // 点击进入详情
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserAcc userAcc = userAccList.get(position);
                Intent intent = new Intent(AttentionListActivity.this, UserInfoActivity.class);
                intent.putExtra("uid", userAcc.getUid());
                startActivity(intent);
            }
        });
    }

    // 初始化关注列表
    private void initList() {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(AttentionListActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/interactive/attentionDetail/")
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(AttentionListActivity.this, "未获取到关注列表", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(AttentionListActivity.this, "获取关注列表失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                // 获取关注列表
                String attentionArr = jsonObject.get("result").toString();
                userAccList = JSONObject.parseArray(attentionArr, UserAcc.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                handler.sendMessage(message);
            }
        });
    }
}