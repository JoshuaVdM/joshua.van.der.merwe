package com.jvdm.recruits.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.jvdm.recruits.R;

/**
 * Created by joske on 08/01/2018.
 */

public class InactivateGroupDialog extends AlertDialog.Builder {
    private String groupName;
    private onInactivateGroupDialogListener listener;

    public InactivateGroupDialog(@NonNull Context context,
                                 String groupName,
                                 String title,
                                 String message,
                                 onInactivateGroupDialogListener listener) {
        super(context);
        this.groupName = groupName;
        this.listener = listener;
        initialize(title, message);
    }

    private void initialize(String title, String message) {
        setTitle(title);
        setMessage(message);

        setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onGroupInactivated();
                dialogInterface.cancel();
            }
        });
        setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = create();
        dialog.show();
    }


    public interface onInactivateGroupDialogListener {
        void onGroupInactivated();
    }
}
