package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.tcm.entity.InquiryDetail;
import com.example.tcm.entity.InquiryPrescription;
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

public class InquiryPescriptionActivity extends AppCompatActivity {

    private static final int UI_OPERATION = 1;
    private static final int REFRESH = 2;
    private static final int PRESCRIPTION = 3;

    private TextView nameTextView;
    private TextView contentTextView;
    private TextView doctorTextView;
    private TextView prescriptionTextView;
    private EditText prescriptionEditText;

    private int idid;
    private int uid;
    private int doctorId;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    String name = msg.getData().getString("name");
                    String content = msg.getData().getString("content");
                    nameTextView.setText(name);
                    contentTextView.setText(content);
                    break;
                case REFRESH:
                    // 刷新
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
                case PRESCRIPTION:
                    String doctor = msg.getData().getString("doctor");
                    String prescription = msg.getData().getString("content");
                    doctorTextView.setText(doctor);
                    prescriptionTextView.setText(prescription);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_prescription);

        nameTextView = findViewById(R.id.tv_name);
        contentTextView = findViewById(R.id.tv_content);
        doctorTextView = findViewById(R.id.tv_doctor);
        prescriptionTextView = findViewById(R.id.tv_prescription);
        prescriptionEditText = findViewById(R.id.et_publish);

        // 获取传递的问诊id
        idid = getIntent().getIntExtra("idid", 0);

        // 用户类型
        String auth = getSharedPreferences("MainActivity", Context.MODE_PRIVATE).getString("auth", "");

        // 如果是普通用户则不显示发表处方
        if ("0".equals(auth)) {
            findViewById(R.id.inquiry_layout_publish).setVisibility(View.GONE);
        }

        // 初始化问诊详情和处方
        initInquiryPrescription(idid);

        // 提供处方
        findViewById(R.id.deliver_btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });

        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转用户详情界面
                Intent intent = new Intent(InquiryPescriptionActivity.this, UserInfoActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        doctorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转医生详情界面
                Intent intent = new Intent(InquiryPescriptionActivity.this, UserInfoActivity.class);
                intent.putExtra("uid", doctorId);
                startActivity(intent);
            }
        });
    }

    // 初始化问诊详情和处方
    private void initInquiryPrescription(int idid) {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(InquiryPescriptionActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        OkHttpClient client = new OkHttpClient();
        Request detailRequest = new Request.Builder()
                .url(Utils.URL_PREFIX+"/inquiry/detail/"+idid)
                .addHeader("token", token)
                .get()
                .build();
        Request prescriptionRequest = new Request.Builder()
                .url(Utils.URL_PREFIX+"/inquiry/prescription/"+idid)
                .addHeader("token", token)
                .get()
                .build();

        client.newCall(detailRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(InquiryPescriptionActivity.this, "未获取到问诊数据", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(InquiryPescriptionActivity.this, "获取问诊数据失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                String inquiryString = jsonObject.get("result").toString();
                InquiryDetail inquiryDetail = JSON.parseObject(inquiryString, InquiryDetail.class);

                uid = inquiryDetail.getUid();

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                Bundle bundle = new Bundle();
                bundle.putString("name", inquiryDetail.getName());
                bundle.putString("content", inquiryDetail.getContent());
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });

        client.newCall(prescriptionRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(InquiryPescriptionActivity.this, "未获取到处方数据", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(InquiryPescriptionActivity.this, "暂无处方！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                String prescriptionString = jsonObject.get("result").toString();
                InquiryPrescription inquiryPrescription = JSON.parseObject(prescriptionString, InquiryPrescription.class);

                doctorId = inquiryPrescription.getDoctorId();

                // 更新UI
                Message message = new Message();
                message.what = PRESCRIPTION;
                Bundle bundle = new Bundle();
                bundle.putString("doctor", inquiryPrescription.getDoctor());
                bundle.putString("content", inquiryPrescription.getContent());
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });

    }

    // 提供处方
    private void publish() {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(InquiryPescriptionActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        String prescription = prescriptionEditText.getText().toString();

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idid", idid);
        jsonObject.put("content", prescription);
        String jsonString = jsonObject.toJSONString();

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/inquiry/prescription/")
                .addHeader("token", token)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(InquiryPescriptionActivity.this, "提供处方失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                if (!"success".equals(returnMsg)) {
                    Looper.prepare();
                    Toast.makeText(InquiryPescriptionActivity.this, "提供处方失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    // 刷新页面
                    Message message = new Message();
                    message.what = REFRESH;
                    handler.sendMessage(message);
                }
            }
        });
    }
}