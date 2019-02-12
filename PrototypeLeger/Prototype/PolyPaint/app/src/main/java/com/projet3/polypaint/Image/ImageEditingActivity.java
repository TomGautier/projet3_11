package com.projet3.polypaint.Image;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.projet3.polypaint.R;

public class ImageEditingActivity extends AppCompatActivity {

    private Canvas canvas;
    private Paint defaultPaint = new Paint();
    private Bitmap bitmap;
    private ImageView iView;
    private int shapeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ImageEditingActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);

        shapeColor = ResourcesCompat.getColor(getResources(), R.color.shape, null);
        defaultPaint.setColor(shapeColor);
        iView = (ImageView) findViewById(R.id.canvasView);

        setTouchListener();
    }

    private void setTouchListener() {
        iView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                iView.setImageBitmap(bitmap);
                canvas = new Canvas(bitmap);

                int posX = (int)event.getX(0);
                int posY = (int)event.getY(0);

                Rect r = new Rect(posX - 10,posY - 10,posX + 10,posY + 10);
                canvas.drawRect(r, defaultPaint);

                view.invalidate();
                return false;
            }
        });
    }
}
