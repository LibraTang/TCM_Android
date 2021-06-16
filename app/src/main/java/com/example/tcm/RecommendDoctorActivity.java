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
import com.example.tcm.adapter.PostCommentAdapter;
import com.example.tcm.adapter.RecommendDoctorAdapter;
import com.example.tcm.entity.PostComment;
import com.example.tcm.entity.PostDetail;
import com.example.tcm.entity.RecommendDoctor;
import com.example.tcm.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendDoctorActivity extends AppCompatActivity {

    private static final int UI_OPERATION = 1;
    private static final int BACK_HOME = 2;

    private ListView recommendListView;

    private Integer typeCode;
    private String content;
    private List<RecommendDoctor> recommendDoctorList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    // 更新推荐列表
                    RecommendDoctorAdapter adapter = new RecommendDoctorAdapter(RecommendDoctorActivity.this, R.layout.inquiry_doctor_recommend_item, recommendDoctorList);
                    recommendListView.setAdapter(adapter);
                    break;
                case BACK_HOME:
                    // 返回首页
                    Intent intent = new Intent(RecommendDoctorActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_doctor);

        recommendListView = findViewById(R.id.lv_recommend);

        // 获取传递的参数
        typeCode = getIntent().getIntExtra("typeCode", 0);
        content = getIntent().getStringExtra("content");

        // 请求推荐医生列表
        initRecommend();

        // 选择一个医生提交问诊
        recommendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecommendDoctor recommendDoctor = recommendDoctorList.get(position);
                createInquiry(recommendDoctor);
            }
        });
    }


    // 初始化推荐列表
    private void initRecommend() {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(RecommendDoctorActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/user/userRecommend/"+typeCode)
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(RecommendDoctorActivity.this, "未获取到推荐数据", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(RecommendDoctorActivity.this, "获取推荐数据失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                // 获取推荐列表
                String recommendArr = jsonObject.get("result").toString();
                recommendDoctorList = JSONObject.parseArray(recommendArr, RecommendDoctor.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                handler.sendMessage(message);
            }
        });
    }

    // 创建问诊单
    private void createInquiry(RecommendDoctor recommendDoctor) {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(RecommendDoctorActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", content);
        jsonObject.put("type", typeCode);
        jsonObject.put("doctor", recommendDoctor.getUid());
        String jsonString = jsonObject.toJSONString();

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/inquiry/detail/")
                .addHeader("token", token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(RecommendDoctorActivity.this, "创建问诊失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(RecommendDoctorActivity.this, "创建问诊失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                // 返回主界面
                Message message = new Message();
                message.what = BACK_HOME;
                handler.sendMessage(message);
            }
        });
    }
}