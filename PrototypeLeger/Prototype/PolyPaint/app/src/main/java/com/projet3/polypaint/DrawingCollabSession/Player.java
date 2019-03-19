package com.projet3.polypaint.DrawingCollabSession;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLException;
import android.support.v4.content.res.ResourcesCompat;

import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.R;

import java.util.ArrayList;

public class Player {
    protected final float SELECTION_STROKE_WIDTH = 4f;
    private static int[] colors = new int[] {Color.RED, Color.BLUE};
    private String name;
    private Paint selectionPaint;
    private ArrayList<GenericShape> selectedShapes;

    @SuppressLint("ResourceAsColor")
    public Player(String name, int selectionColor){
        this.name = name;
        this.selectedShapes = new ArrayList<>();
        initializePaint(selectionColor);
    }
    public String getName(){
        return name;
    }

    public Paint getSelectionPaint(){
        return selectionPaint;
    }
    public final ArrayList<GenericShape> getSelectedShapes(){
        return selectedShapes;
    }
    public void addSelectedShape(GenericShape shape){
        selectedShapes.add(shape);
    }
    public void removeSelectedShape(GenericShape shape){
        selectedShapes.remove(shape);
    }
    public void setSelectedShape(GenericShape shape) {
        for (int i = 0; i < selectedShapes.size(); i++){
            if (selectedShapes.get(i).getId().equals(shape.getId())){
                selectedShapes.set(i,shape);
            }
        }
    }
    public void clearSelectedShape(){selectedShapes.clear();}
    /*public int findSelectedShapeIndex(String id){
        for (int i = 0; i< selectedShapes.size(); i++){
            if (selectedShapes.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }*/

    @SuppressLint("ResourceAsColor")
    private void initializePaint(int selectionColor) {
        // Selection paint
        Paint selectionPaint = new Paint();
        selectionPaint.setColor(colors[selectionColor]);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(SELECTION_STROKE_WIDTH);
        selectionPaint.setStrokeCap(Paint.Cap.ROUND);
        selectionPaint.setAntiAlias(true);
        this.selectionPaint = selectionPaint;
    }
}
