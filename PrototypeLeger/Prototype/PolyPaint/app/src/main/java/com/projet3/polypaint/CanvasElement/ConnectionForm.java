package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.opengl.Matrix;
import android.util.Pair;
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
    private ImageEditingFragment.ConnectionFormType type;
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
    private Paint selectPaint;
    private Paint modifyPaint;
    private boolean isResizing;
    //private Pair<GenericShape,GenericShape> connectedShapes;


    public ConnectionForm(String id, int x, int y, int width, int height, PaintStyle style, ImageEditingFragment.ConnectionFormType type) {
        super(id, x, y, width, height, style);
        initializeType(type);
        initializePaints();
        initializeVertices();
    }
    public ConnectionForm clone() {
        return new ConnectionForm(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, type);
    }
    private void initializeType(ImageEditingFragment.ConnectionFormType type){
        this.type = type;
        arrowPaint = new Paint();
        arrowPaint.setAntiAlias(true);
        switch(type) {
            case Agregation:
                arrowHeight = 30;
                arrowWidth = 50;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
            case Composition:
                arrowHeight = 30;
                arrowWidth = 50;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.FILL);
                break;
            case Inheritance:
                arrowHeight = 50;
                arrowWidth = 30;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
            case Dependance:
                //no arrows
                break;
            case Bidirectional:
                arrowHeight = 50;
                arrowWidth = 30;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }
    private void initializeVertices() {
        //connectedShapes = new Pair(null,null);
        //vertices = new ArrayList<>();
        path = new Path();
        selectedVertex = null;
        isResizing = false;
        //int wLine = width - ARROW_WIDTH;
        Point origin = new Point(posX - width/2, posY);
        Point middle1 = new Point(origin.x + width/3, posY);
        Point middle2 = new Point(middle1.x + width/3, posY);
        Point end = new Point(posX + width/2,posY);
        last = new ConnectionFormVertex(end, null);
        ConnectionFormVertex middleVertex2  = new ConnectionFormVertex(middle2,last);
        ConnectionFormVertex middleVertex1 = new ConnectionFormVertex(middle1,middleVertex2);
        first = new ConnectionFormVertex(origin,middleVertex1);
        //vertices.add(first);
        //vertices.add(last);
        //originVertex.setNext(endVertex);
        //vertexs.add(originVertex);
        //vertexs.add(endVertex);
        //vertexs.add(endVertex);
    }
    private void initializePaints(){
        linePaint = new Paint();
        // smooths
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(6.0f);


        //arrowPaint.setStrokeWidth(2.0f);

        modifyPaint = new Paint();
        // smooths
        modifyPaint.setAntiAlias(true);
        modifyPaint.setColor(Color.YELLOW);
        modifyPaint.setStyle(Paint.Style.STROKE);
        modifyPaint.setStrokeWidth(4.5f);

        selectPaint = new Paint();
        // smooths
        selectPaint.setAntiAlias(true);
        selectPaint.setColor(Color.RED);
        selectPaint.setStyle(Paint.Style.STROKE);
        selectPaint.setStrokeWidth(1.0f);

    }

   /* public boolean isVertexTouched(){
        return vertexTouched;
    }*/

    public void finishResize(){
        isResizing = false;
        selectedVertex = null;
    }

    @Override
    public void relativeMove(int x, int y) {
        super.relativeMove(x,y);
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
    /*public void updateVertex(int x, int y){
        ConnectionFormVertex nVertex = null;
        if (selectedVertex != null) {
            if (selectedVertex == first) {
                first = new ConnectionFormVertex(new Point(x, y), selectedVertex);
            } else if (selectedVertex.getNext() == null) {
                nVertex = new ConnectionFormVertex(new Point(x, y), null);
                selectedVertex.setNext(nVertex);
            } else {
                nVertex = new ConnectionFormVertex(new Point(x, y), selectedVertex.getNext().clone());
                selectedVertex.setNext(nVertex);
            }
        }
    }*/

    @Override
    public Path getSelectionPath() {
        /*Path p = new Path();
        p.moveTo()*/
        return path;
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
    public void drawOnCanvas(Canvas canvas) {
        path.reset();
        ConnectionFormVertex firstVertex = first;
        path.moveTo(firstVertex.x(), firstVertex.y());
        ConnectionFormVertex currentVertex = firstVertex;
        float angle = 0;
        while (currentVertex.getNext() != null){
            if (!(currentVertex == first && type == ImageEditingFragment.ConnectionFormType.Bidirectional))
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

    private void drawArrow(float angle, Canvas canvas){
        arrow = new Path();
        frontArrow = new Path();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        switch (type){
            case Inheritance:
                canvas.rotate(angle,last.x(),last.y());
                arrow.moveTo(last.x() + arrowWidth/2, last.y()); // Top
                arrow.lineTo(last.x() - arrowWidth/2, last.y() - arrowHeight/2); // Bottom left
                arrow.lineTo(last.x() - arrowWidth/2, last.y() + arrowHeight/2); // Bottom right
                arrow.lineTo(last.x() + arrowWidth/2, last.y()); // Back to Top
                arrow.close();
                break;
            case Bidirectional:
                //front
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
            case Dependance:
                //no arrows
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
        canvas.drawPath(verticesPath,modifyPaint);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }

}
