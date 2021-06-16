package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.tcm.entity.UserAcc;
import com.example.tcm.entity.UserSurvey;
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

public class SurveyActivity extends AppCompatActivity {

    private static final int UI_OPERATION = 1;

    private UserSurvey userSurvey;

    private RadioButton wearSame;
    private RadioButton wearLess;
    private RadioButton wearMore;
    private RadioButton hotYes;
    private RadioButton hotNo;
    private RadioButton coldYes;
    private RadioButton coldNo;
    private RadioButton irritationYes;
    private RadioButton irritationNo;
    private RadioButton inflamedYes;
    private RadioButton inflamedNo;
    private RadioButton shitNo;
    private RadioButton shitOne;
    private RadioButton shitTwo;
    private RadioButton shitMore;
    private RadioButton peeClear;
    private RadioButton peeYellow;
    private RadioButton waistYes;
    private RadioButton waistNo;
    private RadioButton appetiteYes;
    private RadioButton appetiteNo;
    private RadioButton sleepYes;
    private RadioButton sleepNo;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:

                    int wear = userSurvey.getWearMore();
                    int hot = userSurvey.getHot();
                    int cold = userSurvey.getCold();
                    int irritation = userSurvey.getIrritation();
                    int inflamed = userSurvey.getGetInflamed();
                    int shit = userSurvey.getShit();
                    int pee = userSurvey.getPee();
                    int waist = userSurvey.getWaist();
                    int appetite = userSurvey.getAppetite();
                    int sleep = userSurvey.getSleep();

                    if (0 == wear) {
                        wearSame.setChecked(true);
                    } else if (1 == wear){
                        wearLess.setChecked(true);
                    } else {
                        wearMore.setChecked(true);
                    }

                    if (0 == hot) {
                        hotNo.setChecked(true);
                    } else {
                        hotYes.setChecked(true);
                    }

                    if (0 == cold) {
                        coldNo.setChecked(true);
                    } else {
                        coldYes.setChecked(true);
                    }

                    if (0 == irritation) {
                        irritationNo.setChecked(true);
                    } else {
                        irritationYes.setChecked(true);
                    }

                    if (0 == inflamed) {
                        inflamedNo.setChecked(true);
                    } else {
                        inflamedYes.setChecked(true);
                    }

                    if (0 == shit) {
                        shitNo.setChecked(true);
                    } else if (1 == shit) {
                        shitOne.setChecked(true);
                    } else if (2 == shit) {
                        shitTwo.setChecked(true);
                    } else {
                        shitMore.setChecked(true);
                    }

                    if (0 == pee) {
                        peeClear.setChecked(true);
                    } else {
                        peeYellow.setChecked(true);
                    }

                    if (0 == waist) {
                        waistNo.setChecked(true);
                    } else {
                        waistYes.setChecked(true);
                    }

                    if (0 == appetite) {
                        appetiteNo.setChecked(true);
                    } else {
                        appetiteYes.setChecked(true);
                    }

                    if (0 == sleep) {
                        sleepNo.setChecked(true);
                    } else {
                        sleepYes.setChecked(true);
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        wearSame = findViewById(R.id.rb_wear_same);
        wearLess = findViewById(R.id.rb_wear_less);
        wearMore = findViewById(R.id.rb_wear_more);
        hotYes = findViewById(R.id.rb_hot_yes);
        hotNo = findViewById(R.id.rb_hot_no);
        coldYes = findViewById(R.id.rb_cold_yes);
        coldNo = findViewById(R.id.rb_cold_no);
        irritationYes = findViewById(R.id.rb_irritation_yes);
        irritationNo = findViewById(R.id.rb_irritation_no);
        inflamedYes = findViewById(R.id.rb_inflamed_yes);
        inflamedNo = findViewById(R.id.rb_inflamed_no);
        shitNo = findViewById(R.id.rb_shit_no);
        shitOne = findViewById(R.id.rb_shit_one);
        shitTwo = findViewById(R.id.rb_shit_two);
        shitMore = findViewById(R.id.rb_shit_more);
        peeClear = findViewById(R.id.rb_pee_clear);
        peeYellow = findViewById(R.id.rb_pee_yellow);
        waistYes = findViewById(R.id.rb_waist_yes);
        waistNo = findViewById(R.id.rb_waist_no);
        appetiteYes = findViewById(R.id.rb_appetite_yes);
        appetiteNo = findViewById(R.id.rb_appetite_no);
        sleepYes = findViewById(R.id.rb_sleep_yes);
        sleepNo = findViewById(R.id.rb_sleep_no);

        // 从其他活动传过来的uid
        int uid = getIntent().getIntExtra("uid", 0);

        // 初始化问卷
        initSurvey(uid);

        // 如果是医生则只可查看不可修改
        if ("1".equals(getSharedPreferences("MainActivity", MODE_PRIVATE).getString("auth", ""))) {
            findViewById(R.id.deliver_btn_publish).setVisibility(View.GONE);
        }

        findViewById(R.id.deliver_btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSurvey();
                finish();
            }
        });
    }

    // 初始化问卷
    private void initSurvey(int uid) {
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(SurveyActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/user/userSurvey/"+uid)
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(SurveyActivity.this, "未获取到问卷信息", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(SurveyActivity.this, "获取问卷信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                // 获取问卷信息
                String userSurveyStr = jsonObject.get("result").toString();
                userSurvey = JSONObject.parseObject(userSurveyStr, UserSurvey.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                handler.sendMessage(message);
            }
        });
    }

    // 提交问卷
    private void submitSurvey() {
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(SurveyActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        UserSurvey userSurvey = new UserSurvey();

        if (wearSame.isChecked()) {
            userSurvey.setWearMore(0);
        } else if (wearLess.isChecked()) {
            userSurvey.setWearMore(1);
        } else {
            userSurvey.setWearMore(2);
        }

        if (hotNo.isChecked()) {
            userSurvey.setHot(0);
        } else {
            userSurvey.setHot(1);
        }

        if (coldNo.isChecked()) {
            userSurvey.setCold(0);
        } else {
            userSurvey.setCold(1);
        }

        if (irritationNo.isChecked()) {
            userSurvey.setIrritation(0);
        } else {
            userSurvey.setIrritation(1);
        }

        if (inflamedNo.isChecked()) {
            userSurvey.setGetInflamed(0);
        } else {
            userSurvey.setGetInflamed(1);
        }

        if (shitNo.isChecked()) {
            userSurvey.setShit(0);
        } else if (shitOne.isChecked()) {
            userSurvey.setShit(1);
        } else if (shitTwo.isChecked()) {
            userSurvey.setShit(2);
        } else {
            userSurvey.setShit(3);
        }

        if (peeClear.isChecked()) {
            userSurvey.setPee(0);
        } else {
            userSurvey.setPee(1);
        }

        if (waistNo.isChecked()) {
            userSurvey.setWaist(0);
        } else {
            userSurvey.setWaist(1);
        }

        if (appetiteNo.isChecked()) {
            userSurvey.setAppetite(0);
        } else {
            userSurvey.setAppetite(1);
        }

        if (sleepNo.isChecked()) {
            userSurvey.setSleep(0);
        } else {
            userSurvey.setSleep(1);
        }

        String jsonString = JSON.toJSONString(userSurvey);
        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/user/userSurvey")
                .addHeader("token", token)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(SurveyActivity.this, "提交问卷失败！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                if (!"success".equals(returnMsg)) {
                    Looper.prepare();
                    Toast.makeText(SurveyActivity.this, "提交问卷失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }
}