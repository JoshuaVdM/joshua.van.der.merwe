package com.jvdm.recruits.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jvdm.recruits.Model.IEnumSpinner;

/**
 * Created by Joske on 4/01/18.
 */

public class RoleAdapter extends ArrayAdapter<IEnumSpinner> {
    IEnumSpinner[] data;

    public RoleAdapter(Context context, IEnumSpinner[] data) {
        super(context, android.R.layout.simple_spinner_item, data);
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater
                    .inflate(android.R.layout.simple_spinner_item,
                            parent,
                            false);

            viewHolder.role = convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        IEnumSpinner item = data[position];
        viewHolder.role.setText(item.getLabel(getContext()));
        return convertView;
    }

    static class ViewHolder {
        TextView role;
    }
}
