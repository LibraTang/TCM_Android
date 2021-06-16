package com.example.tcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //返回上一页
        findViewById(R.id.settings_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.settings_btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitChange();
            }
        });

        //修改手机号
        findViewById(R.id.settings_layout_changeUname).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChangeUnameActivity.class);
                startActivity(intent);
            }
        });
        //修改密码
        findViewById(R.id.settings_layout_changePwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        // 退出登录
        findViewById(R.id.settings_layout_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    private void commitChange() {
    }

    /**
     * 退出登录
     */
    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 清空登录数据
        editor.clear();
        editor.apply();

        // 返回登录界面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
