package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.opengl.Matrix;
import android.renderscript.Matrix2f;

import com.projet3.polypaint.DrawingSession.ImageEditingFragment;

import java.util.ArrayList;

public abstract class GenericShape {

    protected final int SELECTION_GAP = 4;
    private final int EDIT_BUTTON_SIZE = 30;
    protected final int CLONE_OFFSET = 30;
    protected final int ROTATION_BOX_OFFSET = 40;

    protected int posX;
    protected int posY;
    protected int width;
    protected int height;
    protected float angle;
    protected PaintStyle style;
    protected String id;
    protected ArrayList<AnchorPoint> anchorPoints;
    protected ArrayList<ConnectionForm> connections;
    protected Path rotationPath;


    public GenericShape(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        this.id = id;
        this.posX = x;
        this.posY = y;
        this.width = width;
        this.height = height;
        this.style = style;
        this.angle = angle;
        anchorPoints = new ArrayList<>();
        connections = new ArrayList<>();
        setAnchorPoints();
    }
    public void setRotationBox(){
        rotationPath = new Path();
        rotationPath.addCircle(posX,posY,(float)(Math.sqrt(Math.pow(width,2) + Math.pow(height,2)) + ROTATION_BOX_OFFSET), Path.Direction.CW);
    }

    public void setAnchorPoints(){
        anchorPoints = new ArrayList<>();
        anchorPoints.add(new AnchorPoint("right",this));
        anchorPoints.add(new AnchorPoint("left",this));
        anchorPoints.add(new AnchorPoint("top",this));
        anchorPoints.add(new AnchorPoint("bottom",this));
    }
    public abstract void drawOnCanvas(Canvas canvas);

    public abstract GenericShape clone();

    public void drawSelectionBox(Canvas canvas, Paint paint) {
        int w2 = width/2 + SELECTION_GAP;
        int h2 = height/2 + SELECTION_GAP;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle, posX,posY);
        canvas.drawPath(p, paint);
        canvas.restore();
        drawEditButton(canvas);
    }
    private void drawEditButton(Canvas canvas) {
        Paint paint = new Paint();
        Path p = new Path();

        // Background
        paint.setColor(Color.LTGRAY);
        p.addRect(new RectF(getEditButton()), Path.Direction.CW);
        canvas.drawPath(p, paint);
        p.reset();

        // Pencil
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setAntiAlias(true);
        int left = getEditButton().left;
        int bottom = getEditButton().bottom;

        p.moveTo(left, bottom);
        p.lineTo(left, bottom - EDIT_BUTTON_SIZE/3);
        p.lineTo(left + 2*EDIT_BUTTON_SIZE/3, bottom - EDIT_BUTTON_SIZE);
        p.lineTo(left + EDIT_BUTTON_SIZE, bottom - 2*EDIT_BUTTON_SIZE/3);
        p.lineTo(left + EDIT_BUTTON_SIZE/3, bottom);
        p.lineTo(left, bottom);
        p.moveTo(left + EDIT_BUTTON_SIZE/3, bottom);
        p.lineTo(left, bottom - EDIT_BUTTON_SIZE/3);

        canvas.drawPath(p, paint);
    }

    private Rect getBoundingBox() {
        int w2 = width/2;
        int h2 = height/2;
        return new Rect(posX - w2, posY - h2, posX + w2, posY + h2);
    }
    public Path getSelectionPath() {
        Path p = new Path();
        p.addRect(new RectF(getBoundingBox()), Path.Direction.CW);
        return p;
    }
    public boolean contains(int x, int y) {
        return getBoundingBox().contains(x,y);
    }
    public void updateAnchor(ConnectionForm connection){
        ConnectionFormVertex vertex = connection.getDockingVertex();
        if (vertex == null)
            return;
        for (AnchorPoint anchorPoint : anchorPoints){
            if (!anchorPoint.isConnected() && anchorPoint.intersect(vertex.getBox())){
                anchorPoint.setConnection(vertex);
            }
            else if (anchorPoint.isConnected() && !anchorPoint.stillConnected()){
                anchorPoint.setConnection(null);
            }
        }
    }
    public boolean canResize(int x, int y){
        return false;
    }
    public boolean canRotate(int x, int y){
        setRotationBox();
        Path temp = new Path();
        temp.addCircle(x,y,5, Path.Direction.CW);
        temp.op(rotationPath, Path.Op.INTERSECT);
        return !temp.isEmpty();
    }
    public Rect getEditButton() {
        int w2 = width/2;
        int h2 = height/2;

        return new Rect(posX + w2, posY - h2 - EDIT_BUTTON_SIZE, posX + w2 + EDIT_BUTTON_SIZE, posY - h2);
    }

    public void drawAnchorPoints(Canvas canvas){
        for (AnchorPoint anchor : anchorPoints){
            anchor.drawOnCanvas(canvas);
        }
    }
    public void rotate(float angle) {
        this.angle += angle;
        rotateAnchorPoints();
        //rotateAnchorPoints(this.angle);
    }

    public void rotateAnchorPoints(){
        for (AnchorPoint anchorPoint : anchorPoints){
            anchorPoint.setPath();
        }
    }
    public void relativeMove(int x, int y) {
        posX += x;
        posY += y;
        for (AnchorPoint anchorPoint : anchorPoints)
            anchorPoint.setPath();
    }
    public int getHeight(){
        return height;
    }
    public  int getWidth() {
        return width;
    }
    public final static int getDefaultHeight(ImageEditingFragment.ShapeType currentShapeType){
        switch (currentShapeType){
            case Activity:
                return UMLActivity.DEFAULT_HEIGHT;
            case UmlClass:
                return UMLClass.DEFAULT_HEIGHT;
            case Artefact:
                return UMLArtefact.DEFAULT_HEIGHT;
            case Role:
                return UMLRole.DEFAULT_HEIGHT;
            case ConnectionForm:
                return ConnectionForm.DEFAULT_HEIGHT;
        }
        return 0;
    }
    public final static int getDefaultWidth(ImageEditingFragment.ShapeType currentShapeType){
        switch (currentShapeType){
            case Activity:
                return UMLActivity.DEFAULT_WIDTH;
            case UmlClass:
                return UMLClass.DEFAULT_WIDTH;
            case Artefact:
                return UMLArtefact.DEFAULT_WIDTH;
            case Role:
                return UMLRole.DEFAULT_WIDTH;
            case ConnectionForm:
                return ConnectionForm.DEFAULT_WIDTH;
        }
        return 0;
    }
    public String getId() {
        return id;
    }
    public int[] getCenterCoord(){
        return new int[] {posX,posY};
    }
    public ArrayList<AnchorPoint> getAnchorPoints(){
        return anchorPoints;
    }



    public abstract void showEditingDialog(FragmentManager fragmentManager);
}
