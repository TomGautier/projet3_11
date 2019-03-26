package com.projet3.polypaint.DrawingSession;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.R;

public class StyleEditingDialog extends DialogFragment {
    private View rootView;
    private RadioGroup radioGroup;
    private PaintStyle style;

    public StyleEditingDialog(){ super(); }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_style, null);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.lineStyle);
        switch (style.getStrokeType()) {
            case full:
                radioGroup.check(R.id.radioFull);
                break;
            case dotted:
                radioGroup.check(R.id.radioDotted);
                break;
            case dashed:
                radioGroup.check(R.id.radioDashed);
                break;
        }

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        modifyCurrentStyle();
                        ImageEditingDialogManager.getInstance().onStyleDialogPositiveClick(style);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ImageEditingDialogManager.getInstance().onStyleDialogNegativeClick();
                    }
                });
        return builder.create();
    }

    public void setStyle(PaintStyle style) {
        this.style = new PaintStyle(style);
    }

    private void modifyCurrentStyle() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioFull :
                style.setStrokeType(PaintStyle.StrokeType.full);
                break;
            case R.id.radioDotted :
                style.setStrokeType(PaintStyle.StrokeType.dotted);
                break;
            case R.id.radioDashed :
                style.setStrokeType(PaintStyle.StrokeType.dashed);
                break;
        }
    }
}
