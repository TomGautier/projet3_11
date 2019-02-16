package com.projet3.polypaint.Image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.R;

import java.util.ArrayList;

public class ImageEditingActivity extends AppCompatActivity {

    private enum ShapeType{uml_class, uml_activity, uml_artefact, uml_role}

    private final float DEFAULT_STROKE_WIDTH = 2f;
    private final float SELECTION_STROKE_WIDTH = 4f;

    private Canvas canvas;
    private PaintStyle defaultStyle;
    private Bitmap bitmap;
    private ImageView iView;
    private ArrayList<GenericShape> shapes;
    private ShapeType currentShapeType = ShapeType.uml_class;

    private Paint selectionPaint;
    private boolean selectionMode = false;
    private GenericShape currentSelection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ImageEditingActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);

        iView = (ImageView) findViewById(R.id.canvasView);
        shapes = new ArrayList<>();
        initializePaint();

        setTouchListener();
        setToggleListener();
    }

    private void initializePaint() {
        int borderColor = ResourcesCompat.getColor(getResources(), R.color.shape, null);
        Paint borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setAntiAlias(true);

        int backgroundColor = ResourcesCompat.getColor(getResources(), R.color.shapeFillTest, null);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        defaultStyle = new PaintStyle(borderPaint, backgroundPaint);

        int selectionColor = ResourcesCompat.getColor(getResources(), R.color.shapeSelection, null);
        selectionPaint = new Paint();
        selectionPaint.setColor(selectionColor);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(SELECTION_STROKE_WIDTH);
        selectionPaint.setStrokeCap(Paint.Cap.ROUND);
        selectionPaint.setAntiAlias(true);

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

                if (selectionMode)
                    checkSelection(posX, posY);
                else
                    addShape(posX, posY);

                drawAllShapes();

                view.invalidate();
                return false;
            }
        });
    }
    
    private void setToggleListener() {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.buttonSelection);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                selectionMode = isChecked;
            }
        });
    }

    private boolean checkSelection(int x, int y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).isOverPoint(x, y)){
                currentSelection = shapes.get(i);
                return true;
            }
        }

        currentSelection = null;
        return false;
    }

    private void addShape(int posX, int posY) {
        switch (currentShapeType) {
            case uml_class :
                shapes.add(new UMLClass(posX, posY, defaultStyle));
                break;
            case uml_activity :
                shapes.add(new UMLActivity(posX, posY, defaultStyle));
                break;
            case uml_artefact :
                shapes.add(new UMLArtefact(posX, posY, defaultStyle));
                break;
            case uml_role :
                shapes.add(new UMLRole(posX, posY, defaultStyle));
                break;
        }
    }

    private void drawAllShapes() {
        for(GenericShape shape : shapes)
            shape.drawOnCanvas(canvas);

        if (currentSelection != null)
            currentSelection.drawSelectionBox(canvas, selectionPaint);
    }

    public void setShapeTypeToUmlClass(View button) { currentShapeType = ShapeType.uml_class; }
    public void setShapeTypeToUmlActivity(View button) { currentShapeType = ShapeType.uml_activity; }
    public void setShapeTypeToUmlArtefact(View button) { currentShapeType = ShapeType.uml_artefact; }
    public void setShapeTypeToUmlRole(View button) { currentShapeType = ShapeType.uml_role; }
}
