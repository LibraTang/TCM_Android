package com.example.tcm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class InquiryDetailActivity extends AppCompatActivity {

    private EditText contentEditText;
    private Spinner typeSpinner;

    private Integer typeCode;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_detail);

        //设置标题栏
        Toolbar toolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        //去掉默认的标题
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        contentEditText = findViewById(R.id.deliver_editText_content);
        typeSpinner = findViewById(R.id.deliver_sn_type);

        //点击发布按钮：跳转到医生推荐activity
        findViewById(R.id.deliver_btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InquiryDetailActivity.this, RecommendDoctorActivity.class);
                // 传递问诊科室和病情
                content = contentEditText.getText().toString();
                intent.putExtra("content", content);
                intent.putExtra("typeCode", typeCode);
                startActivity(intent);
            }
        });

        //点击取消：返回上一个activity
        findViewById(R.id.deliver_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化spinner选项
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = typeSpinner.getSelectedItem().toString();
                switch (type) {
                    case "肾内科":
                        typeCode = 1;
                        break;
                    case "内分泌科":
                        typeCode = 2;
                        break;
                    case "心内科":
                        typeCode = 3;
                        break;
                    case "呼吸科":
                        typeCode = 4;
                        break;
                    case "消化科":
                        typeCode = 5;
                        break;
                    case "神经内科":
                        typeCode = 6;
                        break;
                    case "妇科":
                        typeCode = 7;
                        break;
                    case "儿科":
                        typeCode = 8;
                        break;
                    case "五官科":
                        typeCode = 9;
                        break;
                    case "血液肿瘤科":
                        typeCode = 10;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}