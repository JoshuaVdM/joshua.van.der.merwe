package com.jvdm.recruits.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jvdm.recruits.R;

/**
 * Created by Joske on 5/01/18.
 */

public class AcceptInvitationDialog extends AlertDialog.Builder {
    private onAcceptInvitationDialogInteractionListener listener;
    private String name;

    public AcceptInvitationDialog(Context context, String name, onAcceptInvitationDialogInteractionListener listener) {
        super(context);
        this.listener = listener;
        this.name = name;
        initialize();
    }

    private void initialize() {
        setTitle(R.string.group_member_invitation_title);
        setMessage(getContext().getString(R.string.group_member_invitation_message) + " " + name );
        setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onInvitationAccepted();
                dialog.cancel();
            }
        });
        setNegativeButton(R.string.action_decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onInvitationDeclined();
                dialog.cancel();
            }
        });

        final AlertDialog dialog = create();
        dialog.show();

    }

    public interface onAcceptInvitationDialogInteractionListener {
        void onInvitationAccepted();
        void onInvitationDeclined();
    }}
