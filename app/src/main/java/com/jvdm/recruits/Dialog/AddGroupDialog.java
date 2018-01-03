package com.jvdm.recruits.Dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.R;

/**
 * Created by joske on 03/01/2018.
 */

public class AddGroupDialog extends AlertDialog.Builder {
    private onAddGroupDialogListener listener;

    public AddGroupDialog(Context context, onAddGroupDialogListener listener) {
        super(context);
        this.listener = listener;
        initialize();
    }

    private void initialize() {
        setTitle(R.string.group_add_title);
        setView(R.layout.dialog_add_group);

        final AlertDialog dialog = create();
        dialog.show();

        final EditText nameTxt = dialog.findViewById(R.id.et_group_name);
        final EditText cityTxt = dialog.findViewById(R.id.et_group_city);
        final Button add = dialog.findViewById(R.id.btn_add);
        final Button cancel = dialog.findViewById(R.id.btn_cancel);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTxt.getText().toString();
                String city = cityTxt.getText().toString();
                Group g = new Group();
                g.setKey(name);
                g.setCity(city);
                listener.onGroupAdded(g);
                dialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    public interface onAddGroupDialogListener {
        void onGroupAdded(Group g);
    }
}
