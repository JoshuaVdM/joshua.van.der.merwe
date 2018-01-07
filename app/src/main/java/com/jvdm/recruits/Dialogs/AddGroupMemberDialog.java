package com.jvdm.recruits.Dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jvdm.recruits.Adapters.RecruitsAutoCompleteAdapter;
import com.jvdm.recruits.Adapters.RoleAdapter;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Model.GroupMember;
import com.jvdm.recruits.Model.InvitationState;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.Model.Role;
import com.jvdm.recruits.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joske on 04/01/2018.
 */

public class AddGroupMemberDialog extends AlertDialog.Builder {
    private onAddGroupMemberDialogListener listener;
    private List<Recruit> recruits;
    private RecruitsAutoCompleteAdapter adapter;
    private Recruit selectedRecruit;
    private AutoCompleteTextView textView;
    private Role role;

    public AddGroupMemberDialog(Context context, onAddGroupMemberDialogListener listener) {
        super(context);
        this.listener = listener;
        recruits = new ArrayList<>();
        initialize();
    }

    private void initRecruits() {
        final CollectionReference ref = RecruitAccess.getRecruitsCollectionReference();
        ref.whereEqualTo("verified", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                Recruit r = snapshot.toObject(Recruit.class);
                                r.setUid(snapshot.getId());
                                adapter.add(r);
                            }
                        }
                    }
                });
    }

    private void initialize() {
        setTitle(R.string.group_add_member_title);
        setView(R.layout.dialog_add_group_member);

        final AlertDialog dialog = create();
        dialog.show();

        adapter = new RecruitsAutoCompleteAdapter(getContext(), recruits,
                new RecruitsAutoCompleteAdapter.onRecruitsAutoCompleteAdapterInteractionListener() {
                    @Override
                    public void onRecruitSelected(Recruit r) {
                        textView.setText(r.getUsername());
                        textView.dismissDropDown();
                        selectedRecruit = r;
                    }
                });

        initRecruits();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        textView = dialog.findViewById(R.id.actv_group_member_name);
        textView.setAdapter(adapter);
        textView.setThreshold(1);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                selectedRecruit = null;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final Spinner spinner = dialog.findViewById(R.id.spin_member_role);
        ArrayAdapter spinnerAdapter = new RoleAdapter(getContext(), Role.values());
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                role = (Role) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button btnAdd = dialog.findViewById(R.id.btn_add);
        final Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedRecruit == null) {
                    Toast.makeText(
                            getContext(),
                            getContext().getString(R.string.recruit_selection_invalid),
                            Toast.LENGTH_SHORT).show();
                } else {
                    GroupMember gm = new GroupMember();
                    gm.setKey(selectedRecruit.getUid());
                    gm.setRecruitReference(
                            RecruitAccess.getRecruitDocumentReference(selectedRecruit.getUid()));
                    gm.setRole(role);
                    gm.setState(InvitationState.PENDING);
                    listener.onGroupMemberSelected(gm);
                    dialog.cancel();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    public interface onAddGroupMemberDialogListener {
        void onGroupMemberSelected(GroupMember gm);
    }
}
