package com.projet3.polypaint.Image;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;

class CanvasView extends AppCompatImageView {

    public CanvasView(Context context) {
        super(context);
    }

    @Override
    public boolean performClick() {
        return false;
    }
}
