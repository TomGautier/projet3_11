package com.projet3.polypaint.Image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.R;

import java.util.ArrayList;

public class ImageEditingActivity extends AppCompatActivity {

    public enum ShapeType{uml_class, uml_activity, uml_artefact, uml_role}

    private Canvas canvas;
    private Paint defaultPaint = new Paint();
    private Bitmap bitmap;
    private ImageView iView;
    private int shapeColor;
    private ArrayList<GenericShape> shapes;
    private ShapeType currentShapeType = ShapeType.uml_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ImageEditingActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);

        shapeColor = ResourcesCompat.getColor(getResources(), R.color.shape, null);
        defaultPaint.setColor(shapeColor);
        defaultPaint.setStyle(Paint.Style.STROKE);
        iView = (ImageView) findViewById(R.id.canvasView);

        shapes = new ArrayList<>();

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

                addShape(posX, posY);
                drawAllShapes();

                view.invalidate();
                return false;
            }
        });
    }

    private void addShape(int posX, int posY) {
        switch (currentShapeType) {
            case uml_class :
                shapes.add(new UMLClass(posX, posY, defaultPaint));
                break;
            case uml_activity :
                shapes.add(new UMLActivity(posX, posY, defaultPaint));
                break;
            case uml_artefact :
                shapes.add(new UMLArtefact(posX, posY, defaultPaint));
                break;
            case uml_role :
                shapes.add(new UMLRole(posX, posY, defaultPaint));
                break;
        }
    }

    private void drawAllShapes() {
        for(GenericShape shape : shapes)
            shape.drawOnCanvas(canvas);
    }

    public void setShapeTypeToUmlClass(View button) { currentShapeType = ShapeType.uml_class; }
    public void setShapeTypeToUmlActivity(View button) { currentShapeType = ShapeType.uml_activity; }
    public void setShapeTypeToUmlArtefact(View button) { currentShapeType = ShapeType.uml_artefact; }
    public void setShapeTypeToUmlRole(View button) { currentShapeType = ShapeType.uml_role; }
}
