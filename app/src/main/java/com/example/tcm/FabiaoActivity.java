package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.tcm.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FabiaoActivity extends AppCompatActivity {

    private static final int BACK_HOME = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case BACK_HOME:
                    Intent intent = new Intent(FabiaoActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private EditText titleEditText;
    private EditText contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fabiao);

        //设置标题栏
        Toolbar toolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        //去掉默认的标题
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleEditText = findViewById(R.id.deliver_editText_title);
        contentEditText = findViewById(R.id.deliver_editText_content);

        //点击取消：返回上一个activity
        findViewById(R.id.deliver_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 发表
        findViewById(R.id.deliver_btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });
    }

    // 发表帖子
    private void publish() {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(FabiaoActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("word", content);
        String jsonString = jsonObject.toJSONString();

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/post/addpost")
                .addHeader("token", token)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(FabiaoActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                if (!"true".equals(returnMsg)) {
                    Looper.prepare();
                    Toast.makeText(FabiaoActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(FabiaoActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });

        finish();
    }
}