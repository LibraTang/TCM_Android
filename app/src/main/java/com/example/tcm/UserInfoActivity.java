package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSONObject;
import com.example.tcm.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {

    private static final int UI_OPERATION = 1;
    private static final int INTERACTIVE = 2;
    private static final int FOLLOW_STATE = 3;

    private String fullAddress;

    private TextView nameTextView;
    private TextView selfIntroTextView;
    private TextView locationButton;
    private TextView attentionNumberTextView;
    private TextView followNumberTextView;
    private ToggleButton followToggleButton;
    private ConstraintLayout surveyLayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    Bundle bundle = msg.getData();
                    nameTextView.setText(bundle.getString("name"));
                    selfIntroTextView.setText(bundle.getString("selfIntro"));
                    break;
                case INTERACTIVE:
                    Bundle bundle1 = msg.getData();
                    attentionNumberTextView.setText(bundle1.getString("attentionNumber"));
                    followNumberTextView.setText(bundle1.getString("followNumber"));
                    break;
                case FOLLOW_STATE:
                    Bundle bundle2 = msg.getData();
                    if ("0".equals(bundle2.getString("state"))) {
                        // 未关注状态
                        followToggleButton.setChecked(false);
                    } else {
                        // 关注状态
                        followToggleButton.setChecked(true);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        nameTextView = findViewById(R.id.tv_name);
        selfIntroTextView = findViewById(R.id.tv_selfIntro);
        locationButton = findViewById(R.id.btn_location);
        attentionNumberTextView = findViewById(R.id.tv_followNum);
        followNumberTextView = findViewById(R.id.tv_followerNum);
        followToggleButton = findViewById(R.id.tb_follow);
        surveyLayout = findViewById(R.id.user_layout_survey);

        // 普通用户不能查看其他用户的问诊问卷
        if ("0".equals(getSharedPreferences("MainActivity", MODE_PRIVATE).getString("auth", ""))) {
            surveyLayout.setVisibility(View.GONE);
        }

        final Integer uid = getIntent().getIntExtra("uid", 0);

        // 初始化用户信息
        initUserInfo(uid);

        // 初始化关注状态
        initInteractive(uid);

        // 查看位置
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, LocationActivity.class);
                intent.putExtra("fullAddress", fullAddress);
                startActivity(intent);
            }
        });

        // 关注、取消关注
        followToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 判断是否是人为点击
                if (!buttonView.isPressed()) {
                    return;
                }

                if (isChecked) {
                    // 未关注->关注
                    follow(uid);
                } else {
                    // 关注->未关注
                    unFollow(uid);
                }
            }
        });

        // 医生查看用户问诊问卷
        surveyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, SurveyActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }

    private void initUserInfo(Integer uid) {
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(UserInfoActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 发送请求获取用户信息
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX + "/user/othersInfo/"+uid)
                .addHeader("token", token)
                .get()
                .build();
        // 获取关注和粉丝数
        Request interactiveRequest = new Request.Builder()
                .url(Utils.URL_PREFIX+"/interactive/attentionFollowCount/"+uid)
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(UserInfoActivity.this, "获取用户信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                // 组合完整地址
                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append(jsonObject.get("city").toString());
                strBuilder.append(jsonObject.get("region").toString());
                strBuilder.append(jsonObject.get("location").toString());
                fullAddress = strBuilder.toString();

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                Bundle bundle = new Bundle();
                bundle.putString("name", jsonObject.get("name").toString());
                bundle.putString("selfIntro", jsonObject.get("selfintro").toString());
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });

        client.newCall(interactiveRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, "获取关注和粉丝信息失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(UserInfoActivity.this, "获取关注和粉丝信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                Message message = new Message();
                message.what = INTERACTIVE;
                Bundle bundle = new Bundle();
                bundle.putString("attentionNumber", jsonObject.get("attentionCount").toString());
                bundle.putString("followNumber", jsonObject.get("followCount").toString());
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    // 初始化关注状态
    private void initInteractive(int uid) {
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(UserInfoActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX + "/interactive/findFollow/"+uid)
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, "获取关注状态失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals(jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(UserInfoActivity.this, "获取关注状态失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                Message message = new Message();
                message.what = FOLLOW_STATE;
                Bundle bundle = new Bundle();
                // 没有关注关系
                if (null == jsonObject.get("result")) {
                    bundle.putString("state", "0");
                } else {
                    bundle.putString("state", "1");
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    // 关注
    private void follow(int uid) {
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(UserInfoActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", uid);
        String jsonString = jsonObject.toJSONString();

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX + "/interactive/follow/")
                .addHeader("token", token)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, "关注失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(UserInfoActivity.this, "关注失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

    // 取消关注
    private void unFollow(int uid) {
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(UserInfoActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", uid);
        String jsonString = jsonObject.toJSONString();

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX + "/interactive/unFollow/")
                .addHeader("token", token)
                .put(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, "取消关注失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(UserInfoActivity.this, "取消关注失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
}