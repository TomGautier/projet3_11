package com.projet3.polypaint.DrawingSession;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.projet3.polypaint.R;

public class TextEditingDialog extends DialogFragment {
    private View rootView;
    private String contents = "";

    public TextEditingDialog() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_text_editing, null);
        ((EditText)rootView.findViewById(R.id.editText)).setText(contents);

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String contents = ((EditText)rootView.findViewById(R.id.editText)).getText().toString();
                        ImageEditingDialogManager.getInstance().onTextEditingDialogPositiveClick(contents);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ImageEditingDialogManager.getInstance().onTextEditingDialogNegativeClick();
                    }
                });
        return builder.create();
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
