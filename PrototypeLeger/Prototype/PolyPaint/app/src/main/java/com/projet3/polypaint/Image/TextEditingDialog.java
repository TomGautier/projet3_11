package com.projet3.polypaint.Image;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.projet3.polypaint.R;

public class TextEditingDialog extends DialogFragment {

    public interface TextEditingDialogListener {
        public void onTextEditingDialogPositiveClick(String contents);
        public void onTextEditingDialogNegativeClick();
    }

    private TextEditingDialogListener listener;
    private View rootView;

    public TextEditingDialog() {
        super();
    }

    public TextEditingDialog(TextEditingDialogListener parent) {
        super();
        listener = parent;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_text_editing, null);

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String contents = ((EditText)rootView.findViewById(R.id.editText)).getText().toString();
                        listener.onTextEditingDialogPositiveClick(contents);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onTextEditingDialogNegativeClick();
                    }
                });
        return builder.create();
    }

}
