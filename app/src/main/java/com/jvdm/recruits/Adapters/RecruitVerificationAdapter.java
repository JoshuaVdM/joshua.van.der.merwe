package com.jvdm.recruits.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.Helpers.Helper;
import com.jvdm.recruits.Model.RecruitItem;
import com.jvdm.recruits.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joske on 31/12/17.
 */

public class RecruitVerificationAdapter extends ArrayAdapter<RecruitItem> {
    private Context context;
    private List<RecruitItem> recruitList = new ArrayList<>();

    public RecruitVerificationAdapter(@NonNull Context context, ArrayList<RecruitItem> list) {
        super(context, R.layout.fragment_recruit_verification_list_item, list);
        this.context = context;
        recruitList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final RecruitItem recruitItem = getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.fragment_recruit_verification_list_item, parent, false);

            viewHolder.image = convertView.findViewById(R.id.img_recruit);
            viewHolder.name = convertView.findViewById(R.id.txt_recruit_name);
            viewHolder.accept = convertView.findViewById(R.id.btn_accept);
            viewHolder.decline = convertView.findViewById(R.id.btn_decline);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = recruitItem.getUid();
                RecruitAccess.updateRecruitVerified(key, true);
                Helper.showShortToast(context, "Account request accepted");
            }
        });

        viewHolder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = recruitItem.getUid();
                RecruitAccess.declineRecruitVerification(key);
                Helper.showShortToast(context, "Account request denied");
            }
        });

        viewHolder.name.setText(recruitItem.getName());
        Picasso.with(context).load(recruitItem.getPictureUri()).transform(new CircleTransform()).into(viewHolder.image);

        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        Button accept;
        Button decline;
    }
}
