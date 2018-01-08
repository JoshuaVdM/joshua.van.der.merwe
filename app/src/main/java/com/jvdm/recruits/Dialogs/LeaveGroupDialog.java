package com.jvdm.recruits.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jvdm.recruits.R;

/**
 * Created by joske on 05/01/2018.
 */

public class LeaveGroupDialog extends AlertDialog.Builder {
    private String name;
    private onLeaveGroupDialogListener listener;

    public LeaveGroupDialog(Context context, String name, onLeaveGroupDialogListener listener) {
        super(context);
        this.listener = listener;
        this.name = name;
        initialize();
    }

    private void initialize() {
        setTitle(R.string.group_member_leave_group_title);
        setMessage(
                getContext().getString(R.string.group_member_leave_group_message, name)
        );
        setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                listener.onGroupLeave();
                dialog.cancel();
            }
        });
        setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = create();
        dialog.show();
    }

    public interface onLeaveGroupDialogListener {
        void onGroupLeave();
    }
}
