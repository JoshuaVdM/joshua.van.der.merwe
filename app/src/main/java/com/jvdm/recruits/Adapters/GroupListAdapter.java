package com.jvdm.recruits.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jvdm.recruits.Activities.GroupDetailActivity;
import com.jvdm.recruits.Fragments.GroupsFragment;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.Model.RecruitItem;
import com.jvdm.recruits.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joske on 03/01/2018.
 */

public class GroupListAdapter extends ArrayAdapter<Group> {
    public static final String GROUP_KEY_INTENT = "GROUP_KEY";

    public GroupListAdapter(@NonNull Context context, List<Group> list) {
        super(context, R.layout.fragment_groups_list_item, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Group g = getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_groups_list_item, parent, false);

            viewHolder.image = convertView.findViewById(R.id.img_group);
            viewHolder.name = convertView.findViewById(R.id.txt_group_name);
            viewHolder.city = convertView.findViewById(R.id.txt_group_city);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(g.getKey());
        viewHolder.city.setText(g.getCity());
        // TODO: set image

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GroupDetailActivity.class);
                intent.putExtra(GROUP_KEY_INTENT, g.getKey());
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        TextView city;
    }
}