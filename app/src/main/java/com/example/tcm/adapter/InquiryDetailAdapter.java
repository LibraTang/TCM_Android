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
import com.example.tcm.entity.InquiryDetail;

import java.util.List;

public class InquiryDetailAdapter extends ArrayAdapter<InquiryDetail> {
    private int resourceId;

    public InquiryDetailAdapter(Context context, int textViewResourceId, List<InquiryDetail> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        InquiryDetail inquiryDetail = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.inquiry_item, null);

        TextView nameTextView = view.findViewById(R.id.tv_inquiry_name);
        TextView contentTextView = view.findViewById(R.id.tv_inquiry_content);
        TextView typeTextView = view.findViewById(R.id.tv_inquiry_type);

        nameTextView.setText(inquiryDetail.getName());
        contentTextView.setText(inquiryDetail.getContent());
        typeTextView.setText(inquiryDetail.getType());

        return view;
    }
}
