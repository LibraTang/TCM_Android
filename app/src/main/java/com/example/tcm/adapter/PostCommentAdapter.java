package com.example.tcm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcm.R;
import com.example.tcm.UserInfoActivity;
import com.example.tcm.entity.PostComment;
import com.example.tcm.entity.PostDetail;

import java.util.List;

public class PostCommentAdapter extends BaseAdapter {

    private Context context;
    private List<PostComment> postCommentList;

    public PostCommentAdapter(Context context, List<PostComment> postCommentList) {
        this.context = context;
        this.postCommentList = postCommentList;
    }

    @Override
    public int getCount() {
        return postCommentList == null ? 0 : postCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return postCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.post_comment_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //设置用户名
        viewHolder.nameTextView.setText(postCommentList.get(position).getName());
        //设置内容
        viewHolder.commentTextView.setText(postCommentList.get(position).getDetail());

        //设置用户名组件的点击事件
        viewHolder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转用户详情页面
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("uid", postCommentList.get(position).getUid());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private final class ViewHolder {

        TextView nameTextView;
        TextView commentTextView;

        ViewHolder(View view) {
            nameTextView = view.findViewById(R.id.tv_comment_name);
            commentTextView = view.findViewById(R.id.tv_comment_detail);
        }
    }
}
