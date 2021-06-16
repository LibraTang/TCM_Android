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
import com.example.tcm.entity.UserAcc;

import java.util.List;

public class UserAccAdapter extends ArrayAdapter<UserAcc> {
    private int resourceId;

    public UserAccAdapter(Context context, int textViewResourceId, List<UserAcc> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserAcc userAcc = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView nameTextView = view.findViewById(R.id.tv_name);
        nameTextView.setText(userAcc.getName());

        return view;
    }
}
