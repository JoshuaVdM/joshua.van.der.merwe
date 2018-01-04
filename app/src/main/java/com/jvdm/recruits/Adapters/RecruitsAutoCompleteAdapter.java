package com.jvdm.recruits.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joske on 04/01/2018.
 */

public class RecruitsAutoCompleteAdapter extends ArrayAdapter<Recruit> {
    private onRecruitsAutoCompleteAdapterInteractionListener listener;
    private List<Recruit> recruits;
    private List<Recruit> recruitsAll;

    public RecruitsAutoCompleteAdapter(@NonNull Context context, List<Recruit> recruits, onRecruitsAutoCompleteAdapterInteractionListener listener) {
        super(context, R.layout.dialog_add_group_member_autocomplete_item, recruits);
        this.recruits = new ArrayList<>(recruits);
        this.recruitsAll = new ArrayList<>(recruits);
        this.listener = listener;
    }

    @Override
    public void add(@Nullable Recruit object) {
        if (object != null) {
            recruitsAll.add(object);
        }
    }

    @Override
    public int getCount() {
        return recruits.size();
    }

    @Nullable
    @Override
    public Recruit getItem(int position) {
        return recruits.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Recruit r = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.dialog_add_group_member_autocomplete_item, parent, false);

            viewHolder.textView = convertView.findViewById(R.id.tv_recruit_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(r.getUsername());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecruitSelected(r);
            }
        });

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((Recruit) resultValue).getUsername();
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                List<Recruit> recruitSuggestions = new ArrayList<>();
                if (charSequence != null) {
                    for (Recruit r: recruitsAll) {
                        if (r.getUsername().toLowerCase()
                                .contains(charSequence.toString().toLowerCase())) {
                            recruitSuggestions.add(r);
                        }
                    }
                    filterResults.values = recruitSuggestions;
                    filterResults.count = recruitSuggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                recruits.clear();
                if (results != null && results.count > 0) {
                    for (Object object: (List<?>) results.values) {
                        if (object instanceof Recruit) {
                            recruits.add((Recruit) object);
                        }
                    }
                    notifyDataSetChanged();
                } else if (charSequence == null) {
                    recruits.addAll(recruitsAll);
                }
            }
        };
    }

    private static class ViewHolder {
        TextView textView;
    }

    public interface onRecruitsAutoCompleteAdapterInteractionListener {
        void onRecruitSelected(Recruit r);
    }
}
