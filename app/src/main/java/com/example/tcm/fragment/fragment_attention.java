package com.example.tcm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.example.tcm.PostDetailActivity;
import com.example.tcm.R;
import com.example.tcm.adapter.PostDetailAdapter;
import com.example.tcm.entity.PostDetail;
import com.example.tcm.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class fragment_attention extends Fragment {

    private static final int UI_OPERATION = 1;

    private ListView attentionPostListView;

    private List<PostDetail> postDetailList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    PostDetailAdapter adapter = new PostDetailAdapter(getActivity(), R.layout.post_item, postDetailList);
                    attentionPostListView.setAdapter(adapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_attention,container,false);

        //加载标题栏
        Toolbar toolbar = view.findViewById(R.id.attention_toolbar);
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        parent.setSupportActionBar(toolbar);
        //去掉默认的标题
        parent.getSupportActionBar().setDisplayShowTitleEnabled(false);

        attentionPostListView = view.findViewById(R.id.lv_attention_post);

        // 获取关注人的帖子
        initAttentionPost();

        // 点击一项进入帖子详情
        attentionPostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostDetail postDetail = postDetailList.get(position);
                Long pid = postDetail.getPid();
                // 进入帖子详情
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("pid", pid);
                startActivity(intent);
            }
        });

        return view;
    }

    // 获取关注人的帖子
    private void initAttentionPost() {
        String token = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(getActivity(), "用户不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/post/attention")
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "获取关注数据失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"success".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "获取关注数据失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                String arrString = jsonObject.get("result").toString();
                postDetailList = JSONObject.parseArray(arrString, PostDetail.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                handler.sendMessage(message);
            }
        });
    }
}
