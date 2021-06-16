package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.tcm.fragment.fragment_shouye;
import com.example.tcm.fragment.fragment_attention;
import com.example.tcm.fragment.fragment_wenda;
import com.example.tcm.fragment.fragment_wode;
import com.example.tcm.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //根据sharedPreferences判断是否已经登录
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // 若已经登录：渲染主界面
        if (sharedPreferences.getBoolean("isLogin", false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

                        // 先登录
                        String uname = sharedPreferences.getString("uname", "");
                        String pword = sharedPreferences.getString("pword", "");
                        // 向服务器发请求
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("uname", uname)
                                .add("pword", pword)
                                .build();
                        Request request = new Request.Builder()
                                .url(Utils.URL_PREFIX + "/login")
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        // 获取token
                        String token = (String) JSON.parseObject(responseData).get("token");
                        getPreferences(MODE_PRIVATE).edit().putString("token", token).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            renderMainActivity();
        } else {
            renderLoginActivity();
        }
    }

    //加载主页面
    private void renderMainActivity() {
        //标题栏
        setContentView(R.layout.activity_main);
        //底部导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_shouye:
                        fragment = new fragment_shouye();
                        break;
                    case R.id.navigation_wenda:
                        fragment = new fragment_wenda();
                        break;
                    case R.id.navigation_attention:
                        fragment = new fragment_attention();
                        break;
                    case R.id.navigation_wode:
                        fragment = new fragment_wode();
                        break;
                    default:
                        break;
                }
                transaction.replace(R.id.nav_host_fragment, fragment);
                transaction.commit();
                return true;
            }
        });
    }

    //加载登录界面
    private void renderLoginActivity() {
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        Button loginButton = findViewById(R.id.btn_login);
        TextView registerButton = findViewById(R.id.tv_register);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToRegister();
            }
        });
    }

    //点击注册按钮
    private void jumpToRegister() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    //点击登录按钮
    private void login() {
        final Handler mainHandler = new Handler();

        String uname = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(uname.equals("") || password.equals("")) {
            Toast.makeText(MainActivity.this, "请输入账号和密码！", Toast.LENGTH_SHORT).show();
            return ;
        }
        // 给服务器发消息，处理返回值。登录失败：提醒；登录成功：保存uname和pword,渲染主界面
        new Thread(new Runnable() {
            public void run() {
                try {
                    String uname = usernameEditText.getText().toString();
                    String pword = passwordEditText.getText().toString();

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("uname", uname)
                            .add("pword", pword)
                            .build();
                    Request request = new Request.Builder()
                            .url(Utils.URL_PREFIX+"/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new Exception("登录失败");
                    }

                    String responseData = response.body().string();
                    // 解析返回的json
                    String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                    if (!"true".equals(returnMsg)) {
                        throw new Exception("登录失败");
                    }
                    String token = (String) JSON.parseObject(responseData).get("token");
                    String auth =JSON.parseObject(responseData).get("auth").toString();

                    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                    // 保存token
                    editor.putString("token", token);
                    // 保存登录状态
                    editor.putBoolean("isLogin", true);
                    // 保存用户名密码
                    editor.putString("uname", uname);
                    editor.putString("pword", pword);
                    // 保存身份信息
                    editor.putString("auth", auth);
                    editor.apply();

                    // 跳转主界面，需要切换回主线程操作
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderMainActivity();
                        }
                    });
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
    }
}