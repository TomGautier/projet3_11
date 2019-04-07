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
import android.util.Pair;

import com.projet3.polypaint.DrawingSession.ImageEditingFragment;

import java.util.ArrayList;

public abstract class GenericShape {

    protected final int SELECTION_GAP = 4;
    private final int EDIT_BUTTON_SIZE = 30;
    protected final int CLONE_OFFSET = 30;
    protected final int ROTATION_BOX_OFFSET = 40;
    private final double DOT_SPACING = 7.5;

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

    public GenericShape() {}
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
    public String getFillingColor(){
        return String.format("#%06X", (0xFFFFFF & style.getBackgroundPaint().getColor()));
    }
    public String getBorderColor(){
        return String.format("#%06X", (0xFFFFFF & style.getBorderPaint().getColor()));
    }

    public void recuperateConnectionStatus(GenericShape shape){
        for (int i =0; i < shape.getAnchorPoints().size(); i++){
            AnchorPoint anchor = shape.getAnchorPoints().get(i);
            if (anchor.isConnected()){
                AnchorPoint newAnchor = anchorPoints.get(anchor.getIndex());
                newAnchor.setConnection(anchor.getConnectionVertex());
            }
        }
    }
    public void setAnchorPoints(){
        anchorPoints = new ArrayList<>();
        anchorPoints.add(new AnchorPoint("top",this));
        anchorPoints.add(new AnchorPoint("right",this));
        anchorPoints.add(new AnchorPoint("bottom",this));
        anchorPoints.add(new AnchorPoint("left",this));
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



    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Rect getBoundingBox() {
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

    public void linkAnchorPoint(int anchorPointIndex, ConnectionFormVertex connectionVertex){
        anchorPoints.get(anchorPointIndex).setConnection(connectionVertex);
            //anchorPoints.get(anchorPointIndex).setConnection(connectionVertex);
            //connectionVertex.getOwner().setConnection(anchorPoints.get(anchorPointIndex),connectionVertex.getIndex());

    }

    public void updateAnchor(ConnectionForm connection){
       /* Pair pair = connection.getDockingPair();
        if (pair == null || pair.first == null)
            return;*/
        boolean changedAnchor = false;
       ConnectionFormVertex vertex = connection.getSelectedVertex();

        if (vertex == null || vertex.getIndex() == -1)
            return;
        if (anchorPoints != null && anchorPoints.size() > 0){
            for (AnchorPoint anchorPoint : anchorPoints){
               /* if (!anchorPoint.isConnected() && anchorPoint.intersect(vertex.getBox())){
                    anchorPoint.setConnection(vertex);
                    //connection.setConnection(anchorPoint.getIndex(),getId(),vertex.getIndex());
                }
                else if (anchorPoint.isConnected() && !anchorPoint.stillConnected()){
                    anchorPoint.removeConnection();
                    //connection.removeConnection(vertex.getIndex());
                }*/
               if (anchorPoint.intersect(vertex.getBox())){
                   anchorPoint.setConnection(vertex);
                   connection.setConnection(anchorPoint.getIndex(),getId(),vertex.getIndex());
                   changedAnchor = true;
               }
               else if(anchorPoint.getConnectionVertex() == vertex){
                   anchorPoint.removeConnection();
                   //connection.removeConnection(vertex.getIndex());
               }
            }
            if (!changedAnchor){
                connection.removeConnection(vertex.getIndex());
            }
        }
    }
    public void removeAnchorConnection(int anchorPointIndex){
        anchorPoints.get(anchorPointIndex).removeConnection();
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
        if (anchorPoints != null && anchorPoints.size() > 0){
            for (AnchorPoint anchorPoint : anchorPoints)
                anchorPoint.setPath();
        }
    }

    public abstract String getType();
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
            case Phase:
                return UMLPhase.DEFAULT_HEIGHT;
            case Comment:
                return Comment.DEFAULT_HEIGHT;
            case text_box:
                return TextBox.DEFAULT_HEIGHT;

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
            case Phase:
                return UMLPhase.DEFAULT_WIDTH;
            case Comment:
                return Comment.DEFAULT_WIDTH;
            case text_box:
                return TextBox.DEFAULT_WIDTH;
        }
        return 0;
    }

    public String getAttributes(){
        return "";
    }
    public String getMethods(){
        return "";
    }
    public String getId() {
        return id;
    }
    public float getAngle(){
        return angle;
    }
    public int[] getCenterCoord(){
        return new int[] {posX,posY};
    }
    public ArrayList<AnchorPoint> getAnchorPoints(){
        return anchorPoints;
    }

    public abstract void showEditingDialog(FragmentManager fragmentManager);

    protected void traceStyledLine(int x1, int y1, int x2, int y2, Canvas canvas) {
        switch (style.getStrokeType()) {
            case full :
                canvas.drawLine(x1, y1, x2, y2, style.getBorderPaint());
                break;
            case dotted :
                traceDottedLine(x1, y1, x2, y2, canvas);
                break;
            case dashed :
                traceDashedLine(x1, y1, x2, y2, canvas);
                break;
        }
    }

    private void traceDottedLine(int x1, int y1, int x2, int y2, Canvas canvas) {
        double lineLength = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double attainedLength = 0;

        float currentX;
        float currentY;

        while (attainedLength < lineLength) {
            currentX = x1 + (float)((x2 - x1) * (attainedLength / lineLength));
            currentY = y1 + (float)((y2 - y1) * (attainedLength / lineLength));
            canvas.drawLine(currentX, currentY, currentX, currentY, style.getBorderPaint());
            attainedLength += DOT_SPACING;
        }
    }

    private void traceDashedLine(int x1, int y1, int x2, int y2, Canvas canvas) {
        double lineLength = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double attainedLength = 0;

        float currentX1;
        float currentY1;
        float currentX2;
        float currentY2;

        while (attainedLength + DOT_SPACING < lineLength) {
            currentX1 = x1 + (float)((x2 - x1) * attainedLength / lineLength);
            currentY1 = y1 + (float)((y2 - y1) * attainedLength / lineLength);
            currentX2 = x1 + (float)((x2 - x1) * (attainedLength + DOT_SPACING) / lineLength);
            currentY2 = y1 + (float)((y2 - y1) * (attainedLength + DOT_SPACING) / lineLength);
            canvas.drawLine(currentX1, currentY1, currentX2, currentY2, style.getBorderPaint());
            attainedLength += 2 * DOT_SPACING;
        }

        if (attainedLength < lineLength) {
            currentX1 = x1 + (float) ((x2 - x1) * attainedLength / lineLength);
            currentY1 = y1 + (float) ((y2 - y1) * attainedLength / lineLength);
            canvas.drawLine(currentX1, currentY1, x2, y2, style.getBorderPaint());
        }
    }

    protected void traceStyledCircle(int x, int y, int radius, Canvas canvas) {
        switch (style.getStrokeType()) {
            case full :
                canvas.drawCircle(x, y, radius, style.getBorderPaint());
                break;
            case dotted :
                traceDottedCircle(x, y, radius, canvas);
                break;
            case dashed :
                traceDashedCircle(x, y, radius, canvas);
                break;
        }
    }

    private void traceDottedCircle(int x, int y, int radius, Canvas canvas) {
        float currentAngle = 0;
        float sweepAngle = (float)(Math.toDegrees(DOT_SPACING / radius));

        while (currentAngle < 360) {
            canvas.drawArc(x - radius, y - radius, x + radius, y + radius, currentAngle, 0.1f, false, style.getBorderPaint());
            currentAngle += sweepAngle;
        }
    }

    private void traceDashedCircle(int x, int y, int radius, Canvas canvas) {
        float currentAngle = 0;
        float sweepAngle = (float)(Math.toDegrees(DOT_SPACING / radius));

        while (currentAngle < 360) {
            canvas.drawArc(x - radius, y - radius, x + radius, y + radius, currentAngle, sweepAngle, false, style.getBorderPaint());
            currentAngle += 2 * sweepAngle;
        }
    }

    public PaintStyle getStyle() { return style; }
    public void setStyle(PaintStyle style) { this.style = style; }
}
