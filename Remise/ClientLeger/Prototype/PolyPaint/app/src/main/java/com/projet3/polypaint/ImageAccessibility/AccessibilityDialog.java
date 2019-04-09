package com.projet3.polypaint.ImageAccessibility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;
import com.projet3.polypaint.R;

public class AccessibilityDialog extends DialogFragment {
    private View rootView;

    private RadioGroup radioGroup;
    private CheckBox protectedCheckBox;
    private EditText passwordEdit;
    private TextView passwordTextView;

    public AccessibilityDialog() { super(); }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        initializeViews();
        setProtectedCheckBox();

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        boolean isPrivate = radioGroup.getCheckedRadioButtonId() == R.id.radioPrivate;
                        AccessibilityManager.getInstance().onAccessibilityPositiveClick(isPrivate, protectedCheckBox.isChecked(), passwordEdit.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AccessibilityManager.getInstance().onAccessibilityNegativeClick();
                    }
                });
        return builder.create();
    }

    private void initializeViews() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_accessibility, null);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        protectedCheckBox = (CheckBox) rootView.findViewById(R.id.protectionCheckBox);
        passwordEdit = (EditText) rootView.findViewById(R.id.passwordEdit);
        passwordTextView = (TextView) rootView.findViewById(R.id.passwordTextView);
    }

    private void setProtectedCheckBox() {
        protectedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordTextView.setVisibility(View.VISIBLE);
                    passwordEdit.setVisibility(View.VISIBLE);
                }
                else {
                    passwordTextView.setVisibility(View.GONE);
                    passwordEdit.setVisibility(View.GONE);
                }
            }
        });
    }
}
