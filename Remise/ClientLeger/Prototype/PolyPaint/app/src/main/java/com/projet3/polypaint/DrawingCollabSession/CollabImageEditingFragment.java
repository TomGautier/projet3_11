package com.projet3.polypaint.DrawingCollabSession;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.projet3.polypaint.CanvasElement.AnchorPoint;
import com.projet3.polypaint.CanvasElement.Comment;
import com.projet3.polypaint.CanvasElement.ConnectionForm;
import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.RotationGestureDetector;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLPhase;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import com.projet3.polypaint.Network.RequestManager;
import com.projet3.polypaint.Network.SocketManager;
import com.projet3.polypaint.Network.FetchManager;
import com.projet3.polypaint.R;

import java.util.ArrayList;
import java.util.Date;

public class CollabImageEditingFragment extends ImageEditingFragment
        implements /*ImageEditingDialogManager.ImageEditingDialogSubscriber, */DrawingCollabSessionListener {

    private View rootView;
    private String imageID;
    private ArrayList<Player> players;
    private Player client;
    private int selectedColorCpt;
    private CollabShape currentShape;
    private boolean isEditingNewShape = false;
    private static final String IMAGE_ID_TAG = "IMAGE_ID";
    private static final String VISIBILTY_TAG = "visibility";
    private static final String PASSWORD_TAG = "password";

    public static CollabImageEditingFragment newInstance(String sessionId, String visibility, String password){
        CollabImageEditingFragment fragment = new CollabImageEditingFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_ID_TAG, sessionId);
        args.putString(VISIBILTY_TAG, visibility);
        args.putString(PASSWORD_TAG, password);
        fragment.setArguments(args);
        return fragment;
    }

    public CollabImageEditingFragment()  {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater,container,savedInstanceState);
        players = new ArrayList<>();
        selectedColorCpt = 0;
        client = new Player(FetchManager.currentInstance.getUserUsername(), selectedColorCpt);

        String imageId = getArguments().getString(IMAGE_ID_TAG);
        if (imageId == null || imageId.equals("")) {
            joinNewDrawingSession(getArguments().getString(VISIBILTY_TAG), getArguments().getString(PASSWORD_TAG));
        }
        else {
            joinDrawingSession(imageId);
        }
        return rootView;
    }

    public void joinDrawingSession(String imageId) {
        SocketManager.currentInstance.setupDrawingCollabSessionListener(this);
        SocketManager.currentInstance.joinCollabSession(imageId);
    }
    public void joinNewDrawingSession(String visibility, String password) {
        SocketManager.currentInstance.setupDrawingCollabSessionListener(this);
        String id = FetchManager.currentInstance.getUserUsername() + "_" + (new Date()).getTime();
        RequestManager.currentInstance.postImage(id, visibility, password, FetchManager.currentInstance.getUserUsername());
        SocketManager.currentInstance.joinCollabSession(id);
    }
    @Override
    protected boolean canResize(){
        return client.getSelectedShapes().size() == 1 && client.getSelectedShapes().get(0).getClass().equals(ConnectionForm.class);
    }
    @Override
    protected void resizeShape(MotionEvent event){
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);
        ConnectionForm connectionForm =((ConnectionForm) client.getSelectedShapes().get(0));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchPosY = posY;
                lastTouchPosX = posX;
                break;
            case MotionEvent.ACTION_MOVE:
                drawAnchorPoints();
                connectionForm.relativeVertexMove(posX - lastTouchPosX,posY - lastTouchPosY, 0);
                lastTouchPosX = posX;
                lastTouchPosY = posY;
                break;
            case MotionEvent.ACTION_UP:
                tryToAnchor(connectionForm);
                connectionForm.finishResize();
                SocketManager.currentInstance.modifyElements(new CollabShape[] {createCollabShape(connectionForm)});
                break;
        }

    }
    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void setTouchListener() {
        iView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (refreshCpt++ >= 1){
                    updateCanvas();
                    refreshCpt = 0;
                }

                boolean continueListening = false;

                int posX = (int)event.getX(0);
                int posY = (int)event.getY(0);

                // Check if an showEditingDialog button was clicked
                 if (event.getAction() != MotionEvent.ACTION_MOVE &&
                   !client.getSelectedShapes().isEmpty() && checkEditButton(posX, posY)) { /*Do nothing*/ }
                // Check if canvas is being resized
                //if (isResizingCanvas || checkCanvasResizeHandle(posX, posY))
                  //  return resizeCanvas(event);
                else switch (currentMode) {
                    case selection:
                        if (canResize() && client.getSelectedShapes().get(0).canResize(posX,posY)){
                            resizeShape(event);
                            continueListening = true;
                        }
                        else
                            checkSelection(posX,posY);
                        break;
                    case rotate:
                        rotationDetector.onTouchEvent(event,posX,posY);
                        continueListening = true;
                        break;

                    case lasso:
                        doLassoSelection(event);
                        continueListening = true;
                        break;
                    case creation:
                        //ArrayList stackElems = new ArrayList();
                        //stackElems.add(addShape(posX, posY));
                        //addToStack(stackElems, ADD_ACTION);
                        addShape(posX,posY);
                        // SocketManager.currentInstance.addElement(createCollabShape(posX,posY));
                        break;
                    case move:
                        moveSelectedShape(event);
                        continueListening = true;
                        break;
                }

                drawAllShapes();
                view.invalidate();
                return continueListening;
            }
        });

        canvasBGLayout.setOnTouchListener(new LinearLayout.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int posX = (int)event.getX(0);
                int posY = (int)event.getY(0);

                if (isResizingCanvas || checkCanvasResizeHandle(posX, posY)) {
                    return resizeCanvas(event);
                }

                return false;
            }
        });
    }
    @Override
    public void onStop() {
        ImageEditingDialogManager.getInstance().unsubscribe(this);
        super.onStop();
    }
    private Player findPlayer(String name){
        for (Player player : players){
            if (player.getName().equals(name))
                return player;
        }
        return null;
    }

   @Override
   protected boolean checkEditButton(int x, int y) {
       ArrayList<GenericShape> selections = client.getSelectedShapes();

       for (int i = selections.size() - 1; i >= 0; i--) {
           if (selections.get(i).getEditButton().contains(x, y)){
               GenericShape clicked = selections.get(i);
               currentShape = createCollabShape(clicked);
               currentShape.showEditingDialog(getFragmentManager());
               return true;
           }
       }
       return false;
   }

    private boolean isFreeToSelect(String id){
        for (GenericShape shape : client.getSelectedShapes()){
            if (shape.getId().equals(id))
                return true;
        }
        for (Player player : players){
            for (GenericShape shape : player.getSelectedShapes()){
                if (shape.getId().equals(id))
                    return false;
            }
        }
        return true;
    }
    private boolean isNewPlayer(String name){
        if (client.getName().equals(name))
            return false;
        else
        {
            for (Player player : players) {
                if (player.getName().equals(name)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected void drawAllShapes() {
        for (int i = 0; i < shapes.size(); i++){
            shapes.get(i).drawOnCanvas(canvas);
        }
        if (client != null)
            for (int j = 0; j < client.getSelectedShapes().size(); j++){
                client.getSelectedShapes().get(j).drawSelectionBox(canvas, client.getSelectionPaint());
            }

        if (players != null)
            for (int w = 0; w < players.size(); w++){
                for (int z = 0; z < players.get(w).getSelectedShapes().size(); z++){
                    players.get(w).getSelectedShapes().get(z).drawSelectionBox(canvas,players.get(w).getSelectionPaint());
                }
            }
    }
    protected GenericShape addShape(int posX, int posY) {
        currentShape = createCollabShape(posX,posY);
        
        if(currentShape.getProperties().getType() == "Arrow")
            SocketManager.currentInstance.addElement(currentShape);
        else {
            isEditingNewShape = true;
            currentShape.showEditingDialog(getFragmentManager());
        }

        return null;
    }


    @Override
    protected void checkSelection(int x, int y) {
        //String[] oldIds = new String[client.getSelectedShapes().size()];
        ArrayList<String> olds = new ArrayList();
        ArrayList<String> newIds = new ArrayList();
        for (GenericShape shape : client.getSelectedShapes()){
            olds.add(shape.getId());
        }
        for (GenericShape shape : shapes){
            if (shape.contains(x, y) && isFreeToSelect(shape.getId())){
                newIds.add(shape.getId());
                break;
            }
        }
        SocketManager.currentInstance.selectElements(olds.toArray(new String[olds.size()]), newIds.toArray(new String[newIds.size()]));
    }


    @Override
    protected void checkLassoSelection() {

        ArrayList<String> olds = new ArrayList();
        ArrayList<String> news = new ArrayList();
        for (GenericShape shape : client.getSelectedShapes()){
            olds.add(shape.getId());
        }
        for (GenericShape shape : shapes){
            canvas.clipPath(shape.getSelectionPath(), Region.Op.REPLACE);

            if (!canvas.clipPath(selectionPath, Region.Op.DIFFERENCE) && isFreeToSelect(shape.getId()))
                news.add(shape.getId());
        }
        SocketManager.currentInstance.selectElements(olds.toArray(new String[olds.size()]), news.toArray(new String[news.size()]));
        canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), Region.Op.REPLACE);

    }
    private String findGenShapeType(Class genClass){
        if (genClass.equals(UMLClass.class))
            return ShapeType.UmlClass.toString();
        else if (genClass.equals(UMLActivity.class))
            return ShapeType.Activity.toString();
        else if (genClass.equals(UMLRole.class))
            return ShapeType.Role.toString();
        else if (genClass.equals(UMLArtefact.class))
            return ShapeType.Artefact.toString();
        else if (genClass.equals(TextBox.class))
            return ShapeType.Text.toString();
        else if (genClass.equals(ConnectionForm.class))
            return ShapeType.Arrow.toString();
        else if (genClass.equals(UMLPhase.class))
            return ShapeType.Phase.toString();
        else if (genClass.equals(Comment.class))
            return ShapeType.Comment.toString();
        return null;
    }

    private CollabShape createCollabShape(GenericShape shape){
        String type = findGenShapeType(shape.getClass());
        CollabShape collabShape;
        CollabShapeProperties properties;
        if (type.equals(ShapeType.Arrow.toString())){
            ConnectionForm connection  = (ConnectionForm)shape;
            //connection.removeConnection(0);
            //connection.removeConnection(1);
            properties = new CollabConnectionProperties(connection.getVerticesXPos(0),connection.getVerticesYPos(0),connection.getThick(),
                    connection.getType(),connection.getConnectionType(),
                    connection.getFillingColor(),shape.getBorderColor(),connection.getTailShapeId(),connection.getFrontShapeId()
                    ,connection.getTailAnchorPointIndex(), connection.getFrontAnchorPointIndex(),connection.getQ1(),connection.getQ2());
        }
        else {
            properties = new CollabShapeProperties(type, shape.getFillingColor(),
                    shape.getBorderColor(),shape.getAttributes(),shape.getMethods(),shape.getLabel(),shape.getBorderType(),
                    shape.getCenterCoord(), shape.getHeight(),shape.getWidth(),(int)shape.getAngle());
        }

        collabShape = new CollabShape(shape.getId(),imageID, client.getName(),properties);
        return collabShape;
    }
    private CollabShape createCollabShape(int posX, int posY){
        CollabShapeProperties properties;
        String id = client.getName() + "_" + (new Date()).getTime();
        String hexColor;
        if(currentShapeType.toString().equals(ShapeType.Arrow.toString())){
            int fillingColor = ResourcesCompat.getColor(getResources(), R.color.DefaultConnectionFormFillingColor,null);
            int borderColor = ResourcesCompat.getColor(getResources(), R.color.DefaultConnectionFormBorderColor,null);
            String hexFillingColor = "#" + Integer.toHexString(fillingColor);
            String hexBorderColor = "#" + Integer.toHexString(borderColor);
            properties = new CollabConnectionProperties(ConnectionForm.generateDefaultX(posX), ConnectionForm.generateDefaultY(posY),
                    ConnectionForm.DEFAULT_THICK,currentShapeType.toString(), currentConnectionFormType.toString(),hexFillingColor,hexBorderColor,"","",-1,-1,"","");
        }
        else{
            String backgroundColor = "#" + Integer.toHexString(defaultStyle.getBackgroundPaint().getColor());
            String borderColor = "#" + Integer.toHexString(defaultStyle.getBorderPaint().getColor());
            String shapeType = currentShapeType.toString();

            properties = new CollabShapeProperties(shapeType, backgroundColor,
                borderColor,new ArrayList<String>(), new ArrayList<String>(), "", PaintStyle.StrokeType.Full,
                 new int[] {posX,posY},GenericShape.getDefaultHeight(currentShapeType),
                GenericShape.getDefaultWidth(currentShapeType),0);
        }

        return new CollabShape(id,imageID, client.getName(),properties);
    }
    @Override
    protected void reset() {
        ArrayList<String> ids = new ArrayList<>();
        for (GenericShape shape : shapes)
            ids.add(shape.getId());
        SocketManager.currentInstance.deleteElements(ids.toArray(new String[ids.size()]));
    }
    @Override
    public void onEndRotation() {
        if (rotatingShape != null){
            SocketManager.currentInstance.modifyElements(new CollabShape[]{createCollabShape(rotatingShape)});
            super.onEndRotation();
        }
    }
    @Override
    public void OnRotation(RotationGestureDetector rotationDetector, int posX, int posY) {
        if (rotatingShape != null)
            rotatingShape.rotate(-rotationDetector.getAngle());
        else{
            for (GenericShape shape : client.getSelectedShapes()) {
                if (shape.canRotate(posX, posY)) {
                    shape.rotate(-rotationDetector.getAngle());
                    rotatingShape = shape;
                    return;
                }
            }
        }
    }

    @Override
    protected void stack(){
        if (shapes.size() > 0) {
            SocketManager.currentInstance.stackElement(shapes.get(shapes.size() - 1).getId());
        }
    }
    @Override
    protected void unStack(){
        if (!stack.empty()) {
            SocketManager.currentInstance.unstackElement(createCollabShape(stack.peek()));
        }
    }
    @Override
    public void deleteSelection() {
        ArrayList<String> ids = new ArrayList<>();
        for (GenericShape shape : client.getSelectedShapes()){
            ids.add(shape.getId());
        }
        SocketManager.currentInstance.deleteElements(ids.toArray(new String[ids.size()]));
    }
    @Override
    public void duplicateSelection() {
        // Check whether to duplicate selected shapes or clipboard (or nothing)
        ArrayList<CollabShape> collabShapes = new ArrayList<>();
        if (!client.getSelectedShapes().isEmpty()){
            for (GenericShape shape : client.getSelectedShapes()){
                collabShapes.add(createCollabShape(shape.clone()));
            }
            SocketManager.currentInstance.duplicateElements(collabShapes.toArray(new CollabShape[collabShapes.size()]));

        }
        else if (!cutShapes.isEmpty()){
            for (GenericShape shape : cutShapes){
                collabShapes.add(createCollabShape(shape));
            }
            SocketManager.currentInstance.duplicateCutElements(collabShapes.toArray(new CollabShape[collabShapes.size()]));
        }
        else return;

    }
    @Override
    protected void moveSelectedShape(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (GenericShape shape : client.getSelectedShapes()){
                    if (shape.contains(posX, posY)) {
                        isMovingSelection = true;
                        lastTouchPosX = posX;
                        lastTouchPosY = posY;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMovingSelection) {
                    // ArrayList<CollabShape> shapes = new ArrayList<>();
                    for (GenericShape shape : client.getSelectedShapes()){
                        shape.relativeMove(posX - lastTouchPosX, posY - lastTouchPosY);
                        //CollabShape collShape = createCollabShape(shape);
                        //collShape.getProperties().setMiddlePointCoord(posX - lastTouchPosX, posY - lastTouchPosY);
                        //shapes.add(collShape);
                    }
                    //SocketManager.currentInstance.modifyElements(shapes.toArray(new CollabShape[shapes.size()]));
                    lastTouchPosX = posX;
                    lastTouchPosY = posY;
                    //updateCanvas();
                }
                break;
            case MotionEvent.ACTION_UP:
                ArrayList<CollabShape> shapes = new ArrayList<>();
                for (GenericShape shape : client.getSelectedShapes()){
                    if (shape.getClass().equals(ConnectionForm.class) && client.getSelectedShapes().size() == 1){
                        removeConnection(shape);
                    }
                    shapes.add(createCollabShape(shape));
                }
                SocketManager.currentInstance.modifyElements(shapes.toArray(new CollabShape[shapes.size()]));
                isMovingSelection = false;
                break;
        }
    }

    //EVENT SERVER
    @Override
    public void onJoinedSession(String drawingSessionId_) {
        imageID = drawingSessionId_;
        ArrayList<CollabShape> newShapes = RequestManager.currentInstance.getAllShapes(imageID);
        if (newShapes != null) {
            for (CollabShape s : newShapes) {
                shapes.add(createGenShape(s));
            }
            drawAllShapes();
        }
    }
    @Override
    public void onNewUserJoined(String[] players) {
        for (int i =  0; i < players.length; i++){
            if (isNewPlayer(players[i]))
                this.players.add(new Player(players[i],++selectedColorCpt % 4));
        }
    }

    @Override
    public void onResizeCanvas(final int width, final int height) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = iView.getLayoutParams();
                params.width = checkCanvasWidth(width) ? width : DEFAULT_CANVAS_WIDTH;
                params.height = checkCanvasHeight(height) ? height : DEFAULT_CANVAS_HEIGHT;
                iView.setLayoutParams(params);
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);

            }
        });
    }

    @Override
    public void onAddElement(CollabShape shape, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        GenericShape genShape = createGenShape(shape);
        shapes.add(genShape);
        player.clearSelectedShape();
        player.addSelectedShape(genShape);
        /*if (!client.getName().equals(shape.getAuthor()) && !playerAlreadyRegistered(shape.getAuthor())){
            selectedColorCpt++;
            players.add(new Player(shape.getAuthor(),selectedColorCpt));
        }*/
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //iView.invalidate();
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);

            }
        });
    }

    @Override
    public void onDuplicateElements(CollabShape[] shapes, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        player.clearSelectedShape();
        for (int i =0; i < shapes.length; i++){
            GenericShape shape = createGenShape(shapes[i]);
            player.addSelectedShape(shape);
            this.shapes.add(shape);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
            }
        });
    }

    @Override
    public void onDuplicateCutElements(CollabShape[] shapes, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i =0; i < shapes.length; i++){
            GenericShape shape = createGenShape(shapes[i]);
            player.addSelectedShape(shape);
            this.shapes.add(shape);
        }
        if (client.getName().equals(author)){
            this.cutShapes.clear();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
            }
        });
    }

    private void removeConnection(GenericShape shape){
        if (shape == null)
            return;
        if (shape.getClass().equals(ConnectionForm.class)) {
            ConnectionForm connection = (ConnectionForm)shape;
            if (!connection.getTailShapeId().isEmpty()) {
                GenericShape linkedShape = findGenShapeById(connection.getTailShapeId());
                linkedShape.removeAnchorConnection(connection.getTailAnchorPointIndex());
                connection.removeConnection(0);

            }
            if (!connection.getFrontShapeId().isEmpty()) {
                GenericShape linkedShape = findGenShapeById(connection.getFrontShapeId());
                linkedShape.removeAnchorConnection(connection.getFrontAnchorPointIndex());
                connection.removeConnection(1);
            }
        }
        else{
            for (int i = 0; i < shape.getAnchorPoints().size(); i ++){
                AnchorPoint anchor = shape.getAnchorPoints().get(i);
                if (anchor.isConnected()){
                    anchor.getConnectionVertex().getOwner().removeConnection(anchor.getConnectionVertex().getIndex());
                    anchor.removeConnection();
                }
            }
        }
    }
    @Override
    public void onDeleteElement(String[] ids, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i = 0; i < ids.length; i++){
            GenericShape shape = findGenShapeById(ids[i]);
            removeConnection(shape);
            player.removeSelectedShape(shape);
            shapes.remove(shape);
        }
        if (shapes.size() == 0){ //DONC UN RESET
            client.clearSelectedShape();
            for (Player playr : players)
                playr.clearSelectedShape();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
            }
        });
        //  Toast.makeText(getContext(),"DELETE UN ELEMENT", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCutElements(String[] ids, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i = 0; i < ids.length; i++){
            GenericShape shape = findGenShapeById(ids[i]);
            player.removeSelectedShape(shape);
            shapes.remove(shape);
            removeConnection(shape);
            if (client.getName().equals(author)){
                cutShapes.add(shape);
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
            }
        });

    }

    @Override
    public void onModifyElements(CollabShape[] collabShapes, String author) {
        //if (!client.getName().equals(author)) {
            Player player = findPlayer(author);
            player = (player == null) ? client : player;
            player.clearSelectedShape();
            for (int i = 0; i < collabShapes.length; i++) {
                GenericShape newShape = createGenShape(collabShapes[i]);
                GenericShape oldShape = findGenShapeById(newShape.getId());
                if (oldShape != null) {
                    if (!newShape.getClass().equals(ConnectionForm.class))
                        newShape.recuperateConnectionStatus(oldShape);
                    shapes.remove(oldShape);
                    shapes.add(newShape);
                    player.addSelectedShape(newShape);
                }
            }
        //}
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
            }
        });

    }

    @Override
    public void onSelectedElements(String[] oldSelections, String[] newSelections, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i = 0; i < oldSelections.length; i ++){
            player.removeSelectedShape(findGenShapeById(oldSelections[i]));
        }
        for (int j = 0; j < newSelections.length; j ++){
            if (isFreeToSelect(newSelections[j]))
                player.addSelectedShape(findGenShapeById(newSelections[j]));
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
            }
        });
        // Toast.makeText(getContext(),"SELECTIONNER UN ELEMENT", Toast.LENGTH_LONG).show();

    }
    private void removeSelectedShape(String id){
        for (GenericShape shape : client.getSelectedShapes()){
            if (shape.getId().equals(id)){
                client.removeSelectedShape(shape);
                break;
            }
        }
        for (Player player : players){
            for (GenericShape shape : player.getSelectedShapes()){
                if (shape.getId().equals(id)){
                    player.removeSelectedShape(shape);
                    break;
                }
            }
        }

    }
    @Override
    public void onStackElement(String id, String author) {


        GenericShape shape = findGenShapeById(id);
        removeSelectedShape(id);
        shapes.remove(shape);
        removeConnection(shape);
        if (client.getName().equals(author)){
            stack.push(shape);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
            }
        });
    }

    @Override
    public void onUnstackElement(CollabShape shape, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        GenericShape genShape = createGenShape(shape);
        shapes.add(genShape);
        player.clearSelectedShape();
        player.addSelectedShape(genShape);
        if (client.getName().equals(author)){
            stack.pop();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                iView.invalidate();
                RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
            }
        });
    }

    private GenericShape findGenShapeById(String id) {
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getId().equals(id))
                return shapes.get(i);
        }
        return null;
    }


    private GenericShape createGenShape(CollabShape collabShape){
        GenericShape genShape = null;
        CollabShapeProperties properties = collabShape.getProperties();
        switch(properties.getType()){
            case "UmlClass":
                genShape = new UMLClass(collabShape.getId(),properties.getMiddlePointCoord()[0],
                        properties.getMiddlePointCoord()[1], properties.getWidth(), properties.getHeight(),
                        properties.getLabel(), properties.getAttributesString(), properties.getMethodsString(),
                        properties.getStyle(), properties.getRotation());
                break;
            case "Artefact":
                genShape = new UMLArtefact(collabShape.getId(),properties.getMiddlePointCoord()[0],
                        properties.getMiddlePointCoord()[1], properties.getWidth(),
                        properties.getHeight(), properties.getStyle(), properties.getRotation());
                break;
            case "Activity":
                genShape = new UMLActivity(collabShape.getId(),properties.getMiddlePointCoord()[0],
                        properties.getMiddlePointCoord()[1], properties.getWidth(),
                        properties.getHeight(), properties.getStyle(), properties.getRotation());
                break;
            case "Role":
                genShape = new UMLRole(collabShape.getId(),properties.getMiddlePointCoord()[0],
                        properties.getMiddlePointCoord()[1], properties.getWidth(),
                        properties.getHeight(), properties.getStyle(), properties.getRotation());
                break;
            case "Arrow":
                CollabConnectionProperties connectionProperties = ((CollabConnectionProperties)collabShape.getProperties());
                genShape = new ConnectionForm(collabShape.getId(),connectionProperties.getConnectionType(),connectionProperties.getFillingColor(),
                        connectionProperties.getBorderColor(), connectionProperties.getThick(),
                        connectionProperties.getVerticesX(), connectionProperties.getVerticesY(),connectionProperties.getQ1(),connectionProperties.getQ2());
                if (!connectionProperties.getIdShape1().isEmpty() && connectionProperties.getIndex1() != -1){
                    GenericShape shape = findGenShapeById(connectionProperties.getIdShape1());
                    shape.linkAnchorPoint(connectionProperties.getIndex1(),((ConnectionForm) genShape).getFirst());
                    ((ConnectionForm) genShape).setConnection(connectionProperties.getIndex1(),shape.getId(),0);
                }
                if (!connectionProperties.getIdShape2().isEmpty() && connectionProperties.getIndex2() != -1){
                    GenericShape shape = findGenShapeById(connectionProperties.getIdShape2());
                    shape.linkAnchorPoint(connectionProperties.getIndex2(),((ConnectionForm) genShape).getLast());
                    ((ConnectionForm) genShape).setConnection(connectionProperties.getIndex2(),shape.getId(),1);

                }
                break;
            case "Text":
                genShape = new TextBox(collabShape.getId(), properties.getMiddlePointCoord()[0],
                        properties.getMiddlePointCoord()[1], properties.getWidth(),
                        properties.getHeight(), properties.getStyle(), properties.getLabel(), properties.getRotation());
                break;
            case "Phase":
                genShape = new UMLPhase(collabShape.getId(), properties.getMiddlePointCoord()[0],
                        properties.getMiddlePointCoord()[1], properties.getWidth(),
                        properties.getHeight(), properties.getStyle(), properties.getLabel(), properties.getRotation());
                break;
            case "Comment":
                genShape = new Comment(collabShape.getId(), properties.getMiddlePointCoord()[0],
                        properties.getMiddlePointCoord()[1], properties.getWidth(),
                        properties.getHeight(), properties.getStyle(), properties.getLabel(), properties.getRotation());
                break;
        }
        return genShape;
    }

    protected void cutSelection() {
        if (client.getSelectedShapes().size() > 0){
            ArrayList<String> ids = new ArrayList<>();
            for (GenericShape shape: client.getSelectedShapes()){
                ids.add(shape.getId());
            }
            SocketManager.currentInstance.cutElements(ids.toArray(new String[ids.size()]));
            RequestManager.currentInstance.postThumbnail(Bitmap.createBitmap(bitmap), imageID);
        }
        //cutShapes.addAll(selections);
        //deleteSelection();
    }

    // ------------------------- Dialogs -------------------------
    @Override
    public void onTextDialogPositiveResponse(String contents, PaintStyle style){
        if (currentShape != null) {
            currentShape.getProperties().setLabel(contents);
            currentShape.getProperties().setStyle(style);
            if (isEditingNewShape) {
                SocketManager.currentInstance.addElement(currentShape);
                isEditingNewShape = false;
            } else {
                CollabShape[] shape = {currentShape};
                SocketManager.currentInstance.modifyElements(shape);
            }
            currentShape = null;
        }

    }
    @Override
    public void onTextDialogNegativeResponse(){
        currentShape = null;
        isEditingNewShape = false;
    }
    @Override
    public void onStyleDialogPositiveResponse(PaintStyle style){
        System.out.println("onStyleDialogPositiveResponse");
        if (currentShape != null) {
            currentShape.getProperties().setStyle(style);
            if (isEditingNewShape) {
                SocketManager.currentInstance.addElement(currentShape);
                isEditingNewShape = false;
            }
            else {
                CollabShape[] shape = {currentShape};
                SocketManager.currentInstance.modifyElements(shape);
            }
            currentShape = null;
        }
    }
    @Override
    public void onStyleDialogNegativeResponse(){
        if (currentShape != null) {
            currentShape.getProperties().setStyle(defaultStyle);
            if (isEditingNewShape) {
                SocketManager.currentInstance.addElement(currentShape);
                isEditingNewShape = false;
            }
            else {
                CollabShape[] shape = {currentShape};
                SocketManager.currentInstance.modifyElements(shape);
            }
            currentShape = null;
        }
    }
    @Override
    public void onClassDialogPositiveResponse(String name, String attributes, String methods, PaintStyle style){
        if ( currentShape != null) {
            currentShape.getProperties().setTextFields(name, attributes, methods);
            currentShape.getProperties().setStyle(style);
            if (isEditingNewShape) {
                SocketManager.currentInstance.addElement(currentShape);
                isEditingNewShape = false;
            }
            else {
                CollabShape[] shape = {currentShape};
                SocketManager.currentInstance.modifyElements(shape);
            }
            currentShape = null;
        }

    }
    @Override
    public void onClassDialogNeutralResponse(String name, String attributes, String methods){
        if ( currentShape != null) {
            currentShape.getProperties().setTextFields(name, attributes, methods);
            currentShape.getProperties().setStyle(defaultStyle);
            if (isEditingNewShape) {
                SocketManager.currentInstance.addElement(currentShape);
                isEditingNewShape = false;
            }
            else {
                CollabShape[] shape = {currentShape};
                SocketManager.currentInstance.modifyElements(shape);
            }
            currentShape = null;
        }

    }
}
