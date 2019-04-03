package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.projet3.polypaint.DrawingCollabSession.Player;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.opengl.Matrix;
import android.util.Pair;
import android.widget.ProgressBar;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

public class ConnectionForm extends GenericShape {
    protected final static int DEFAULT_WIDTH = 250;
    protected final static int DEFAULT_HEIGHT = 30;
    private String type;
    private int arrowHeight;
    private int arrowWidth;

    private ConnectionFormVertex first;
    private ConnectionFormVertex last;
    private ConnectionFormVertex selectedVertex;

    private Path path;
    private Path verticesPath;
    private Path arrow;
    private Path frontArrow;

    private Paint linePaint;
    private Paint arrowPaint;
    private boolean isResizing;
    //private Pair<GenericShape,GenericShape> connectedShapes;


    public ConnectionForm(String id, String type, String fillingColor, int[][] vertices){
        this.id = id;
        initialize(vertices,type,fillingColor);
    }
    private void initialize(int[][] vertices, String type, String fillingColor){
        initializeProperties(type, fillingColor);
        initializeVertices(vertices);
    }
    private void initializeProperties(String type, String fillingColor){
        this.type = type;
        path = new Path();
        selectedVertex = null;
        isResizing = false;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor(fillingColor));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(6.0f);
        arrowPaint = new Paint();
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStrokeWidth(1.0f);
        switch(type) {
            case "Agregation":
                arrowHeight = 30;
                arrowWidth = 50;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
            case "Composition":
                arrowHeight = 30;
                arrowWidth = 50;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.FILL);
                break;
            case "Inheritance":
                arrowHeight = 50;
                arrowWidth = 30;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
            case "Bidirectional":
                arrowHeight = 50;
                arrowWidth = 30;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    private void initializeVertices(int[][] values){
        ArrayList<Point> points = new ArrayList();
        ArrayList<ConnectionFormVertex> vertex;
        for (int i = 0; i < values.length; i ++){
            points.add(new Point(values[i][0],values[i][1]));
        }
        last = new ConnectionFormVertex(points.get(points.size()-1),null);
        ConnectionFormVertex vertex1;
        ConnectionFormVertex vertex2 = last;
        for (int j = points.size() - 2; j > 1; j--) {
            vertex1 = new ConnectionFormVertex(points.get(j),vertex2);
            vertex2 = new ConnectionFormVertex(points.get(j-1),vertex1);
        }
        first = new ConnectionFormVertex(points.get(0),vertex2);
    }
    public ConnectionForm clone() {
        return new ConnectionForm(id + "clone", this.type, getFillingColor(), getVerticesPos(CLONE_OFFSET));
    }


    public void finishResize(){
        isResizing = false;
        selectedVertex = null;
    }

    @Override
    public void relativeMove(int x, int y) {
        //super.relativeMove(x,y);
        ConnectionFormVertex current = first;
        while (current != null){
            current.relativeMove(x,y);
            current = current.getNext();
        }
    }
    public void relativeVertexMove(int x, int y, int index) {
       switch(index){
           case 0:
               selectedVertex.relativeMove(x,y);
               break;
           case 1:
               first.relativeMove(x,y);
               break;
           case 2:
               last.relativeMove(x,y);
               break;
       }
    }

    public ConnectionFormVertex getDockingVertex(){
        if (selectedVertex == first)
            return first;
        else if (selectedVertex == last)
            return last;

        return null;
    }

    @Override
    public Path getSelectionPath() {
        return path;
    }
    public String getType(){
        return type;
    }
    public String getFillingColor(){
        return String.format("#%06X", (0xFFFFFF & linePaint.getColor()));
    }

    public int[][] getVerticesPos(int offset){
        ArrayList<int[]> list = new ArrayList<>();
        ConnectionFormVertex current = first;
        while (current.getNext() != null){
            list.add(new int[] {current.x() + offset,current.y() + offset});
        }
        return list.toArray(new int[list.size()][2]);
    }


    @Override
    public boolean contains(int x, int y){
        ConnectionFormVertex current = first;
        while (current != null){
            if (current.contains(x, y)){
                selectedVertex = current;
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    @Override
    public boolean canResize(int x, int y){
        if (isResizing)
            return true;
        if (contains(x,y)){
            isResizing = true;
            return true;
        }
        return false;
    }


    @Override
    public void setAnchorPoints() {
        //no anchor points for connectionForms
    }
    @Override
    public void drawAnchorPoints(Canvas canvas){
        //no anchor points
    }
    @Override
    public void rotateAnchorPoints(){
        //no anchor points
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        path.reset();
        path.moveTo(first.x(), first.y());
        ConnectionFormVertex currentVertex = first;
        float angle = 0;
        while (currentVertex.getNext() != null){
            if (!(currentVertex == first && type == ImageEditingFragment.ConnectionFormType.Bidirectional.toString()))
                path.addCircle(currentVertex.x(),currentVertex.y(),5, Path.Direction.CW);
            path.lineTo(currentVertex.getNext().x(),currentVertex.getNext().y());
            angle = getAngle(currentVertex.getNext().getPoint(), currentVertex.getPoint());
            currentVertex = currentVertex.getNext();
        }
        last = currentVertex;
        canvas.drawPath(path, linePaint);
        drawArrow(angle, canvas);
       // canvas.drawPath(arrow, arrowPaint);
    }
    public static int[][] generateDefaultPoints(int x, int y){
        int[][] array = new int[4][2];
        int width = GenericShape.getDefaultWidth(ImageEditingFragment.ShapeType.ConnectionForm);
        for (int i =0; i < array.length; i++){
            array[i][0] = (x - width/2) + (width/3)*i;
            array[i][1] = y;
        }
        return array;
    }


    private void drawArrow(float angle, Canvas canvas){
        arrow = new Path();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        switch (type){
            case "Inheritance":
                canvas.rotate(angle,last.x(),last.y());
                arrow.moveTo(last.x() + arrowWidth/2, last.y()); // Top
                arrow.lineTo(last.x() - arrowWidth/2, last.y() - arrowHeight/2); // Bottom left
                arrow.lineTo(last.x() - arrowWidth/2, last.y() + arrowHeight/2); // Bottom right
                arrow.lineTo(last.x() + arrowWidth/2, last.y()); // Back to Top
                arrow.close();
                break;
            case "Bidirectional":
                //front
                frontArrow = new Path();
                canvas.rotate(getAngle(first.getPoint(),first.getNext().getPoint()),first.x(),first.y());
                frontArrow.moveTo(first.x() + arrowWidth/2, first.y()); // Top
                frontArrow.lineTo(first.x() - arrowWidth/2, first.y() - arrowHeight/2); // Bottom left
                frontArrow.lineTo(first.x() - arrowWidth/2, first.y() + arrowHeight/2); // Bottom right
                frontArrow.lineTo(first.x() + arrowWidth/2, first.y()); // Back to Top
                frontArrow.close();
                canvas.drawPath(frontArrow,arrowPaint);
                //end
                canvas.restore();
                canvas.save(Canvas.MATRIX_SAVE_FLAG);
                canvas.rotate(angle, last.x(), last.y());
                arrow.moveTo(last.x() + arrowWidth/2, last.y()); // Top
                arrow.lineTo(last.x() - arrowWidth/2, last.y() - arrowHeight/2); // Bottom left
                arrow.lineTo(last.x() - arrowWidth/2, last.y() + arrowHeight/2); // Bottom right
                arrow.lineTo(last.x() + arrowWidth/2, last.y()); // Back to Top
                arrow.close();
                break;

            default: //composition et aggregation
                canvas.rotate(angle,last.x(),last.y());
                arrow.moveTo(last.x(), last.y() + arrowHeight/2); // Top
                arrow.lineTo(last.x() - arrowWidth/2, last.y()); // Left
                arrow.lineTo(last.x(), last.y() - arrowHeight/2); // Bottom
                arrow.lineTo(last.x() + arrowWidth/2, last.y()); // Right
                arrow.lineTo(last.x(), last.y() + arrowHeight/2); // Back to Top
                arrow.close();
                break;
        }
        canvas.drawPath(arrow,arrowPaint);
        canvas.restore();
    }
    private float getAngle(Point target, Point source) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - source.y, target.x - source.x));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
    @Override
    public void drawSelectionBox(Canvas canvas, Paint paint) {
        verticesPath = new Path();
        ConnectionFormVertex current = first;
        while(current != null){
            verticesPath.addCircle(current.x(),current.y(),20, Path.Direction.CW);
            current = current.getNext();
        }
        canvas.drawPath(verticesPath, paint);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }

}
