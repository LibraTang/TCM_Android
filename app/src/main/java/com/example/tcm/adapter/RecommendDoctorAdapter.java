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
import com.example.tcm.entity.RecommendDoctor;

import java.util.List;

public class RecommendDoctorAdapter extends ArrayAdapter<RecommendDoctor> {
    private int resourceId;

    public RecommendDoctorAdapter(Context context, int textViewResourceId, List<RecommendDoctor> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RecommendDoctor recommendDoctor = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.inquiry_doctor_recommend_item, null);

        TextView typeTextView = view.findViewById(R.id.tv_recommend_type);
        TextView doctorTextView = view.findViewById(R.id.tv_recommend_doctor);
        TextView numberTextView = view.findViewById(R.id.tv_recommend_number);

        typeTextView.setText(recommendDoctor.getType());
        doctorTextView.setText(recommendDoctor.getName());

        String answerNumber =  "回答数: " + recommendDoctor.getAnswerNumber().toString();
        numberTextView.setText(answerNumber);

        return view;
    }
}
