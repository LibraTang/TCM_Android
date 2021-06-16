package com.example.tcm.fragment;

import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.example.tcm.AttentionListActivity;
import com.example.tcm.EditInfoActivity;
import com.example.tcm.FollowListActivity;
import com.example.tcm.MainActivity;
import com.example.tcm.R;
import com.example.tcm.SettingsActivity;
import com.example.tcm.SurveyActivity;
import com.example.tcm.util.Utils;
import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class fragment_wode extends Fragment {

    private TextView nameTextView;
    private TextView selfIntroTextView;
    private TextView attentionNumberTextView;
    private TextView followNumberTextView;
    private TextView attentionTextView;
    private TextView followTextView;

    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wode,container,false);

        nameTextView = view.findViewById(R.id.tv_name);
        selfIntroTextView = view.findViewById(R.id.tv_selfIntro);
        attentionNumberTextView = view.findViewById(R.id.tv_followNum);
        followNumberTextView = view.findViewById(R.id.tv_followerNum);
        attentionTextView = view.findViewById(R.id.tv_follow);
        followTextView = view.findViewById(R.id.tv_follower);

        //加载菜单栏
        final Toolbar toolbar = view.findViewById(R.id.personal_toolbar);
        toolbar.inflateMenu(R.menu.personal_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_editInfo:
                        Intent intent1 = new Intent(getActivity(), EditInfoActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.option_settings:
                        Intent intent2 = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // 加载个人信息
        initInfo();

        // 点击关注，跳转关注列表
        attentionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AttentionListActivity.class);
                startActivity(intent);
            }
        });

        // 点击粉丝，跳转粉丝列表
        followTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowListActivity.class);
                startActivity(intent);
            }
        });

        // 如果是医生则不显示问诊问卷
        if ("1".equals(getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE).getString("auth", "0"))) {
            view.findViewById(R.id.wode_layout_survey).setVisibility(View.GONE);
        }

        view.findViewById(R.id.wode_layout_survey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SurveyActivity.class);
                startActivity(intent);
            }
        });

        /*
        //设置渐变
        AppBarLayout app_bar = view.findViewById(R.id.app_bar_personal);
        final int alphaMaxOffset = 400;
        toolbar.getBackground().setAlpha(0);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // 设置 toolbar 背景
                if (verticalOffset > -alphaMaxOffset) {
                    toolbar.getBackground().setAlpha(255 * -verticalOffset / alphaMaxOffset);
                } else {
                    toolbar.getBackground().setAlpha(255);
                }
            }
        });  */


        return view;

    }

    private void initInfo() {
        String token = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(getActivity(), "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 发送请求获取个人信息
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX + "/user/info")
                .addHeader("token", token)
                .get()
                .build();
        // 获取关注和粉丝数
        Request interactiveRequest = new Request.Builder()
                .url(Utils.URL_PREFIX+"/interactive/attentionFollowCount/"+0)
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"true".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "获取用户信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                // 更新UI
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 设置初值
                        nameTextView.setText((String) jsonObject.get("name"));
                        selfIntroTextView.setText((String) jsonObject.get("selfintro"));
                    }
                });
            }
        });
        client.newCall(interactiveRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "获取关注和粉丝信息失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                final JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "获取关注和粉丝信息失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                // 更新UI
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        attentionNumberTextView.setText(jsonObject.get("attentionCount").toString());
                        followNumberTextView.setText(jsonObject.get("followCount").toString());
                    }
                });
            }
        });
    }
}
