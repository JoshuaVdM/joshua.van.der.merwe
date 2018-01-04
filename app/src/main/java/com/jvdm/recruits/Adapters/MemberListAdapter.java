package com.jvdm.recruits.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.Model.GroupMember;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by joske on 03/01/2018.
 */

public class MemberListAdapter extends ArrayAdapter<GroupMember> {

    public MemberListAdapter(@NonNull Context context, List<GroupMember> list) {
        super(context, R.layout.fragment_group_members_list_item, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GroupMember groupMember = getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater
                    .inflate(R.layout.fragment_group_members_list_item, parent, false);

            viewHolder.image = convertView.findViewById(R.id.img_recruit);
            viewHolder.name = convertView.findViewById(R.id.tv_member_name);
            viewHolder.role = convertView.findViewById(R.id.tv_member_role);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.role.setText(groupMember.getRole().getLabel(getContext()));

        DocumentReference userRef = groupMember.getRecruitReference();
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Recruit r = documentSnapshot.toObject(Recruit.class);
                    viewHolder.name.setText(r.getUsername());
                    if (!TextUtils.isEmpty(r.getPhotoUri())) {
                        Picasso.with(getContext())
                                .load(r.getPhotoUri())
                                .transform(new CircleTransform())
                                .into(viewHolder.image);
                    }
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        TextView role;
    }
}
