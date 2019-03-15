package com.projet3.polypaint.Others;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Utilities {

    public static void setButtonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
    public static void changeButtonState(ImageButton button, boolean state){
        if (state){
            button.setEnabled(true);
            button.clearColorFilter();
        }
        else{
            button.setEnabled(false);
            button.setColorFilter(Color.GRAY);
        }
    }



}
