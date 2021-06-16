package com.example.tcm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tcm.R;
import com.example.tcm.entity.PostDetail;

import java.util.List;

public class PostDetailAdapter extends ArrayAdapter<PostDetail> {
    private int resourceId;

    public PostDetailAdapter(Context context, int textViewResourceId, List<PostDetail> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostDetail postDetail = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView nameTextView = view.findViewById(R.id.tv_post_name);
        TextView titleTextView = view.findViewById(R.id.tv_post_title);
        titleTextView.setText(postDetail.getTitle());
        nameTextView.setText(postDetail.getName());

        return view;
    }
}
