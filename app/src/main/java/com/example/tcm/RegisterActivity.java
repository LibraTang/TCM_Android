package com.example.tcm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.tcm.util.Utils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText unameEditText;
    private EditText pwordEditText;
    private RadioGroup radioGroup;
    private RadioButton radioButtonUser;
    private RadioButton radioButtonDoctor;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        unameEditText = findViewById(R.id.register_tv_uname);
        pwordEditText = findViewById(R.id.register_tv_pword);
        radioGroup = findViewById(R.id.register_rg_auth);
        radioButtonUser = findViewById(R.id.register_rbtn_user);
        radioButtonDoctor = findViewById(R.id.register_rbtn_doctor);
        registerButton = findViewById(R.id.btn_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    // 注册
    private void register() {
        String uname = unameEditText.getText().toString();
        String pword = pwordEditText.getText().toString();
        if ("".equals(uname) || "".equals(pword)) {
            Toast.makeText(this, "请输入账号和密码！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 给服务器发送注册请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String uname = unameEditText.getText().toString();
                    String pword = pwordEditText.getText().toString();
                    String auth = "0";
                    if (radioButtonDoctor.isChecked()) {
                        auth = "1";
                    }

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("uname", uname)
                            .add("pword", pword)
                            .add("auth", auth)
                            .build();
                    Request request = new Request.Builder()
                            .url(Utils.URL_PREFIX+"/register")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new Exception("登录失败");
                    }

                    String responseData = response.body().string();
                    String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                    if (!"true".equals(returnMsg)) {
                        throw new Exception("注册失败");
                    }

                    // 跳转主界面
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
    }
}