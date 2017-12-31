package com.jvdm.recruits.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.Model.RecruitItem;
import com.jvdm.recruits.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joske on 31/12/17.
 */

public class RecruitVerificationAdapter extends ArrayAdapter {
    private Context context;
    private List<RecruitItem> recruitList = new ArrayList<>();

    public RecruitVerificationAdapter(@NonNull Context context, ArrayList<RecruitItem> list) {
        super(context, 0, list);
        this.context = context;
        recruitList = list;
    }

    @Override
    public int getCount() {
        return recruitList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.fragment_recruit_verification_list_item, parent, false);
        }
        RecruitItem currentRecruit = recruitList.get(position);

        ImageView image = listItem.findViewById(R.id.img_recruit);
        Picasso.with(context).load(currentRecruit.getPictureUri()).transform(new CircleTransform()).into(image);

        TextView name = listItem.findViewById(R.id.txt_recruit_name);
        name.setText(currentRecruit.getName());

        return listItem;
    }
}
