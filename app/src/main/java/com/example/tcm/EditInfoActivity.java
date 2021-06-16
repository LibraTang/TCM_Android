package com.example.tcm;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.tcm.util.Utils;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class EditInfoActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText selfIntroEditText;
    private EditText emailEditText;
    private RadioButton manRadioButton;
    private RadioButton womanRadioButton;
    private EditText ageEditText;
    private TextView addressTestView;
    private EditText locationEditText;
    private Spinner typeSpinner;
    private String type;
    private String auth;
    private ConstraintLayout typeLayout;

    private Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpersoninfo);

        // 身份
        auth = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("auth", "");

        nameEditText = findViewById(R.id.et_name);
        selfIntroEditText = findViewById(R.id.et_selfIntro);
        emailEditText = findViewById(R.id.et_email);
        manRadioButton = findViewById(R.id.editInfo_rbtn_man);
        womanRadioButton =findViewById(R.id.editInfo_rbtn_woman);
        ageEditText = findViewById(R.id.editInfo_et_age);
        addressTestView = findViewById(R.id.editInfo_tv_address);
        locationEditText = findViewById(R.id.editInfo_et_location);
        typeLayout = findViewById(R.id.settings_layout_changeType);

        // 初始化spinner选项
        typeSpinner = findViewById(R.id.editInfo_sn_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // 如果是普通用户则不显示科室
        if ("0".equals(auth)) {
            typeLayout.setVisibility(View.GONE);
        }

        // 初始化个人信息
        initInfo();

        findViewById(R.id.editInfo_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回上一页
                finish();
            }
        });

        findViewById(R.id.editInfo_btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提交更改到服务器
                submitChange();
            }
        });

        findViewById(R.id.editInfo_tv_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //城市选择器
                showCityPicker();
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = typeSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // 初始化修改个人信息界面
    private void initInfo() {
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(EditInfoActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 发送请求获取个人信息
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX + "/user/info")
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(EditInfoActivity.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"true".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(EditInfoActivity.this, "获取用户信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                // 更新UI
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 设置初值
                        nameEditText.setText((String) jsonObject.get("name"));
                        selfIntroEditText.setText((String) jsonObject.get("selfintro"));
                        emailEditText.setText((String) jsonObject.get("email"));
                        ageEditText.setText((String) jsonObject.get("age"));
                        locationEditText.setText((String) jsonObject.get("location"));

                        String province = (String) jsonObject.get("province");
                        String city = (String) jsonObject.get("city");
                        String region = (String) jsonObject.get("region");
                        String address = province + " - " + city + " - " + region;
                        addressTestView.setText(address);

                        String sex = (String) jsonObject.get("sex");
                        if ("0".equals(sex)) {
                            manRadioButton.setChecked(true);
                        } else {
                            womanRadioButton.setChecked(true);
                        }

                        // 医生显示科室
                        if ("1".equals(auth)) {
                            String type = (String) jsonObject.get("type");
                            SpinnerAdapter adapter = typeSpinner.getAdapter();
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (type.equals(adapter.getItem(i).toString())) {
                                    typeSpinner.setSelection(i, true);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void showCityPicker() {
        final CityPickerView cityPicker=new CityPickerView();
        cityPicker.init(this);
        CityConfig cityConfig = new CityConfig.Builder()
                .province("浙江省")//默认显示的省份
                .city("杭州市")//默认显示省份下面的城市
                .district("余杭区")//默认显示省市下面的区县数据
                .build();
        cityPicker.setConfig(cityConfig);

        final TextView textView=findViewById(R.id.editInfo_tv_address);

        //点击监听
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    //点击确定
                    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                        textView.setText(province+" - "+city+" - "+district);
                    }
                    @Override
                    //点击取消
                    public void onCancel() {
                    }
                });
                cityPicker.showCityPicker();
            }
        });
    }

    // 提交修改信息到服务器
    private void submitChange() {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(EditInfoActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        String name = nameEditText.getText().toString();
        String selfIntro = selfIntroEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String sex = womanRadioButton.isChecked() ? "1" : "0";
        Integer age = Integer.valueOf(ageEditText.getText().toString());
        String address = addressTestView.getText().toString();
        String location = locationEditText.getText().toString();
        Integer typeCode = 0;

        // 解析省-市-区
        String[] addressArray = address.split(" - ");
        String province = addressArray[0];
        String city = addressArray[1];
        String region = addressArray[2];

        // 科室代码
        if ("1".equals(auth)) {
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

        // 发送请求
        OkHttpClient client = new OkHttpClient();
        // 构造json请求体
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("age", age);
        jsonObject.put("sex", sex);
        jsonObject.put("email", email);
        jsonObject.put("province", province);
        jsonObject.put("city", city);
        jsonObject.put("region", region);
        jsonObject.put("location", location);
        jsonObject.put("selfintro", selfIntro);
        jsonObject.put("tag", "");
        jsonObject.put("type", typeCode);
        String jsonString = jsonObject.toJSONString();
        // 请求体
        RequestBody infoBody = RequestBody.create(jsonString, MediaType.parse("application/json"));
        Request infoRequest = new Request.Builder()
                .url(Utils.URL_PREFIX+"/user/userinfo")
                .addHeader("token", token)
                .put(infoBody)
                .build();
        RequestBody accBody = new FormBody.Builder().add("name", name).build();
        Request accRequest = new Request.Builder()
                .url(Utils.URL_PREFIX+"/user/userAcc")
                .addHeader("token", token)
                .put(accBody)
                .build();
        // 异步请求
        client.newCall(infoRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(EditInfoActivity.this, "修改个人信息失败！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                if (!"true".equals(returnMsg)) {
                    Looper.prepare();
                    Toast.makeText(EditInfoActivity.this, "修改个人信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
        client.newCall(accRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(EditInfoActivity.this, "修改个人信息失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                if (!"true".equals(returnMsg)) {
                    Toast.makeText(EditInfoActivity.this, "修改个人信息失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 返回上一页
        finish();
    }
}
