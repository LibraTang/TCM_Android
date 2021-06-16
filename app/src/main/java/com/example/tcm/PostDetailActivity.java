package com.example.tcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.tcm.adapter.PostCommentAdapter;
import com.example.tcm.adapter.PostDetailAdapter;
import com.example.tcm.entity.PostComment;
import com.example.tcm.entity.PostDetail;
import com.example.tcm.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostDetailActivity extends AppCompatActivity {

    private static final int UI_OPERATION = 1;
    private static final int REFRESH = 2;

    private Long pid;
    private Integer uid;
    private List<PostComment> postCommentList;

    private TextView nameTextView;
    private TextView titleTextView;
    private TextView contentTextView;
    private ListView postCommentListView;
    private EditText commentEditText;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UI_OPERATION:
                    // 帖子标题和内容和发帖人
                    String name = msg.getData().getString("name");
                    String title = msg.getData().getString("title");
                    String content = msg.getData().getString("content");
                    nameTextView.setText(name);
                    titleTextView.setText(title);
                    contentTextView.setText(content);

                    // 评论列表
                    PostCommentAdapter adapter = new PostCommentAdapter(PostDetailActivity.this, postCommentList);
                    postCommentListView.setAdapter(adapter);
                    break;
                case REFRESH:
                    // 刷新
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //设置标题栏
        Toolbar toolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        //去掉默认的标题
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        nameTextView = findViewById(R.id.tv_name);
        titleTextView = findViewById(R.id.tv_title);
        contentTextView = findViewById(R.id.tv_content);
        postCommentListView = findViewById(R.id.lv_comment);
        commentEditText = findViewById(R.id.et_publish);

        // 获取传递的pid
        pid = getIntent().getLongExtra("pid", 0);

        // 初始化帖子详情页面
        initPostDetail(pid);

        // 发表评论
        findViewById(R.id.deliver_btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });

        nameTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 跳转用户详情界面
                Intent intent = new Intent(PostDetailActivity.this, UserInfoActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

    }

    private void initPostDetail(Long pid) {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(PostDetailActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/post/detail/"+pid)
                .addHeader("token", token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(PostDetailActivity.this, "未获取到帖子数据", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                if (!"true".equals((String) jsonObject.get("returnMsg"))) {
                    Looper.prepare();
                    Toast.makeText(PostDetailActivity.this, "获取帖子数据失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                uid = (Integer) jsonObject.get("uid");

                // 获取帖子标题和内容和发帖人
                String title = jsonObject.get("title").toString();
                String content = jsonObject.get("word").toString();
                String name = jsonObject.get("name").toString();

                // 获取评论列表
                String commentArr = jsonObject.get("comment").toString();
                postCommentList = JSONObject.parseArray(commentArr, PostComment.class);

                // 更新UI
                Message message = new Message();
                message.what = UI_OPERATION;
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("content", content);
                bundle.putString("name", name);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    // 发表评论
    private void publish() {
        // 获取用户token
        String token = getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", "");
        if (null == token || "".equals(token)) {
            Toast.makeText(PostDetailActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        }

        String detail = commentEditText.getText().toString();

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("detail", detail);
        String jsonString = jsonObject.toJSONString();

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Utils.URL_PREFIX+"/post/comment/"+pid)
                .addHeader("token", token)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(PostDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                String returnMsg = (String) JSON.parseObject(responseData).get("returnMsg");
                if (!"true".equals(returnMsg)) {
                    Looper.prepare();
                    Toast.makeText(PostDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
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