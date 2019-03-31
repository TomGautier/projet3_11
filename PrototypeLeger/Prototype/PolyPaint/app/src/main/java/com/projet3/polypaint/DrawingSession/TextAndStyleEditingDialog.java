package com.projet3.polypaint.DrawingSession;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.R;

public class TextAndStyleEditingDialog extends DialogFragment {
    private View rootView;
    private EditText editText;

    private SeekBar redSliderBG;
    private SeekBar greenSliderBG;
    private SeekBar blueSliderBG;
    private ImageView backgroundPreview;

    private SeekBar redSliderFG;
    private SeekBar greenSliderFG;
    private SeekBar blueSliderFG;
    private ImageView borderPreview;

    private String contents = "";

    private PaintStyle style;
    private int backgroundColor;
    private int borderColor;

    public TextAndStyleEditingDialog() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        initializeViews();
        initializeStyle();
        setSliderListeners();

        editText.setText(contents);

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String contents = editText.getText().toString();
                        modifyCurrentStyle();
                        ImageEditingDialogManager.getInstance().onDialogPositiveClick(style, contents);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ImageEditingDialogManager.getInstance().onTextDialogNegativeClick();
                    }
                });
        return builder.create();
    }

    private void initializeViews() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_style_and_text, null);
        editText = (EditText) rootView.findViewById(R.id.contentEdit);

        redSliderBG = (SeekBar) rootView.findViewById(R.id.redSliderBG);
        greenSliderBG = (SeekBar) rootView.findViewById(R.id.greenSliderBG);
        blueSliderBG = (SeekBar) rootView.findViewById(R.id.blueSliderBG);
        backgroundPreview = (ImageView) rootView.findViewById(R.id.colorPreviewBG);

        redSliderFG = (SeekBar) rootView.findViewById(R.id.redSliderFG);
        greenSliderFG = (SeekBar) rootView.findViewById(R.id.greenSliderFG);
        blueSliderFG = (SeekBar) rootView.findViewById(R.id.blueSliderFG);
        borderPreview = (ImageView) rootView.findViewById(R.id.colorPreviewFG);
    }
    private void initializeStyle() {
        backgroundColor = style.getBackgroundPaint().getColor();
        borderColor = style.getBorderPaint().getColor();

        redSliderBG.setProgress((backgroundColor & (0xff0000)) / (0x10000));
        greenSliderBG.setProgress((backgroundColor & (0xff00)) / (0x100));
        blueSliderBG.setProgress(backgroundColor & (0xff));
        backgroundPreview.setBackgroundColor(backgroundColor);

        redSliderFG.setProgress((borderColor & (0xff0000)) / (0x10000));
        greenSliderFG.setProgress((borderColor & (0xff00)) / (0x100));
        blueSliderFG.setProgress(borderColor & (0xff));
        borderPreview.setBackgroundColor(borderColor);
    }

    private void setSliderListeners() {
        redSliderBG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                backgroundColor = (backgroundColor & (0xff00ffff)) + (progress * (0x010000));
                backgroundPreview.setBackgroundColor(backgroundColor);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        greenSliderBG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                backgroundColor = (backgroundColor & (0xffff00ff)) + (progress * (0x0100));
                backgroundPreview.setBackgroundColor(backgroundColor);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        blueSliderBG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                backgroundColor = (backgroundColor & (0xffffff00)) + (progress);
                backgroundPreview.setBackgroundColor(backgroundColor);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        redSliderFG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                borderColor = (borderColor & (0xff00ffff)) + (progress * (0x010000));
                borderPreview.setBackgroundColor(borderColor);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        greenSliderFG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                borderColor = (borderColor & (0xffff00ff)) + (progress * (0x0100));
                borderPreview.setBackgroundColor(borderColor);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        blueSliderFG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                borderColor = (borderColor & (0xffffff00)) + (progress);
                borderPreview.setBackgroundColor(borderColor);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
    public void setStyle(PaintStyle style) {
        this.style = new PaintStyle(style);
    }

    private void modifyCurrentStyle() {
        style.setBackgroundColor(backgroundColor);
        style.setBorderColor(borderColor);
    }
}
