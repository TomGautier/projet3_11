package com.projet3.polypaint.ImageAccessibility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.projet3.polypaint.R;

public class ImagePasswordDialog extends DialogFragment {
    private View rootView;
    private EditText passwordEdit;

    public ImagePasswordDialog() { super(); }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        initializeViews();

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AccessibilityManager.getInstance().onPasswordPositiveClick(passwordEdit.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AccessibilityManager.getInstance().onPasswordNegativeClick();
                    }
                });
        return builder.create();
    }

    private void initializeViews() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_password, null);

        passwordEdit = (EditText) rootView.findViewById(R.id.passwordEdit);
    }
}
