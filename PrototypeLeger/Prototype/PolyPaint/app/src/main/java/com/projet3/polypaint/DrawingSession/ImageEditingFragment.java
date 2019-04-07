package com.projet3.polypaint.DrawingSession;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.projet3.polypaint.CanvasElement.Comment;
import com.projet3.polypaint.CanvasElement.ConnectionForm;
import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.GenericTextShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.RotationGestureDetector;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLPhase;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.DrawingCollabSession.CollabImageEditingFragment;
import com.projet3.polypaint.Network.SocketManager;
import com.projet3.polypaint.R;
import com.projet3.polypaint.Network.FetchManager;

import java.util.ArrayList;
import java.util.Stack;

public class ImageEditingFragment extends Fragment implements ImageEditingDialogManager.ImageEditingDialogSubscriber, RotationGestureDetector.OnRotationGestureListener {

    protected Button buttonClass;
    protected Button buttonRole;
    protected Button buttonActivity;
    protected Button buttonArtefact;
    protected Button buttonPhase;
    protected Button buttonComment;
    protected Button buttonText;
    protected Button buttonConnectionForm;
    protected Button buttonCanvas;
    protected Button buttonMove;
    protected Button buttonRotate;
    protected Button buttonSelection;
    protected Button buttonLasso;
    protected Button buttonReset;
    protected Button buttonCut;
    protected Button buttonDuplicate;
    protected Button buttonDelete;
    protected Button buttonStack;
    protected Button buttonUnstack;
    protected ImageButton buttonRestore;
    protected ImageButton buttonBack;
    protected final int DEFAULT_CANVAS_WIDTH = 1727;
    protected final int DEFAULT_CANVAS_HEIGHT = 1185;
    protected final int CANVAS_BACKGROUND_PADDING = 75;
    protected enum Mode{selection, lasso, creation, move, rotate}
    public enum ShapeType{none, UmlClass, Activity, Artefact, Role, Text, Arrow, Phase, Comment}
    public enum ConnectionFormType{Aggregation, Composition, Inheritance, Bidirectional, Line, Unidirectional}
    protected final float DEFAULT_STROKE_WIDTH = 2f;
    protected final float SELECTION_STROKE_WIDTH = 4f;
    protected final String ADD_ACTION = "ADD";
    protected final String REMOVE_ACTION = "REMOVE";

    protected Canvas canvas;
    protected PaintStyle defaultStyle;
    protected Bitmap bitmap;
    protected ImageView iView;
    protected LinearLayout canvasBGLayout;
    protected ArrayList<GenericShape> shapes;
    protected ArrayList<GenericShape> cutShapes;
    protected Stack<Pair<ArrayList<GenericShape>, String>> addStack;
    protected Stack<Pair<ArrayList<GenericShape>, String>> removeStack;
    protected Stack<GenericShape> stack;

    protected Mode currentMode = Mode.creation;
    protected ShapeType currentShapeType = ShapeType.UmlClass;
    protected ConnectionFormType currentConnectionFormType = ConnectionFormType.Inheritance;

    protected Paint selectionPaint;
    protected ArrayList<GenericShape> selections = null;
    protected Path selectionPath = new Path();
    protected boolean isMovingSelection = false;
    protected boolean isResizingShape = false;
    protected Path resizeConnectionFormPath = new Path();
    protected int lastTouchPosX;
    protected int lastTouchPosY;
    protected int lastBGTouchPosX;
    protected int lastBGTouchPosY;

    protected View rootView;

    protected boolean isResizingCanvas = false;
    protected boolean isLongPressed = false;
    protected int idCpt;
    protected String id;
    protected RotationGestureDetector rotationDetector;
    protected GenericShape rotatingShape = null;
    protected int refreshCpt = 0;

    public ImageEditingFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.activity_image_editing, container, false);
        iView = (ImageView)rootView.findViewById(R.id.canvasView);
        canvasBGLayout = (LinearLayout)rootView.findViewById(R.id.canvasBackground);
        shapes = new ArrayList<>();
        selections = new ArrayList<>();
        cutShapes = new ArrayList<>();
        rotationDetector = new RotationGestureDetector(this);

        addStack = new Stack<>();
        removeStack = new Stack<>();
        stack = new Stack<>();
        idCpt = 0;
        id = FetchManager.currentInstance.getUserUsername() + idCpt;

        initializeButtons();
        initializePaint();
        ImageEditingDialogManager.getInstance().subscribe(this);

        setTouchListener();
        initializeCanvas();
        drawAllShapes();
        return rootView;
    }

    @Override
    public void onStop() {
        ImageEditingDialogManager.getInstance().unsubscribe(this);
        super.onStop();
    }

    private void initializeButtons(){
        //forms
        buttonActivity = (Button)rootView.findViewById(R.id.buttonActivity);
        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Activity);
            }
        });

        buttonRotate = (Button)rootView.findViewById(R.id.buttonRotate);
        buttonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(Mode.rotate);
            }
        });

        buttonArtefact = (Button)rootView.findViewById(R.id.buttonArtefact);
        buttonArtefact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Artefact);
            }
        });

        buttonClass = (Button)rootView.findViewById(R.id.buttonClass);
        buttonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.UmlClass);
            }
        });

        buttonRole = (Button)rootView.findViewById(R.id.buttonRole);
        buttonRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Role);
            }
        });

        buttonPhase = (Button)rootView.findViewById(R.id.buttonPhase);
        buttonPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Phase);
            }
        });

        buttonComment = (Button)rootView.findViewById(R.id.buttonComment);
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Comment);
            }
        });

        buttonText = (Button)rootView.findViewById(R.id.buttonTextBox);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Text);
            }
        });

        buttonConnectionForm = (Button)rootView.findViewById(R.id.buttonConnectionForm);
        buttonConnectionForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu dropDownMenu = new PopupMenu(getActivity(), buttonConnectionForm);
                dropDownMenu.getMenuInflater().inflate(R.menu.connection_forms_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.connectionFormAgregation:
                                setConnectionFormType(ConnectionFormType.Aggregation);
                                setShapeType(ShapeType.Arrow);
                                break;
                            case R.id.connectionFormInheritance:
                                setConnectionFormType(ConnectionFormType.Inheritance);
                                setShapeType(ShapeType.Arrow);
                                break;
                            case R.id.connectionFormBidirectional:
                                setConnectionFormType(ConnectionFormType.Bidirectional);
                                setShapeType(ShapeType.Arrow);
                                break;
                            case R.id.connectionFormComposition:
                                setConnectionFormType(ConnectionFormType.Composition);
                                setShapeType(ShapeType.Arrow);
                                break;
                            case R.id.connectionFormLine:
                                setConnectionFormType(ConnectionFormType.Line);
                                setShapeType(ShapeType.Arrow);
                                break;
                            case R.id.connectionFormUnidirectional:
                                setConnectionFormType(ConnectionFormType.Unidirectional);
                                setShapeType(ShapeType.Arrow);
                                break;
                        }
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });


        //actions
        buttonDelete = (Button)rootView.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelection();
            }
        });

        buttonCut = (Button)rootView.findViewById(R.id.buttonCut);
        buttonCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutSelection();
            }
        });

        buttonDuplicate = (Button)rootView.findViewById(R.id.buttonDuplicate);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicateSelection();
            }
        });

        buttonStack = (Button)rootView.findViewById(R.id.buttonStack);
        buttonStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stack();
            }
        });
        buttonUnstack = (Button)rootView.findViewById(R.id.buttonUnstack);
        buttonUnstack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unStack();
            }
        });


        buttonReset = (Button)rootView.findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        buttonBack = (ImageButton)rootView.findViewById(R.id.backButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backCanevas();
            }
        });

        buttonRestore = (ImageButton)rootView.findViewById(R.id.restoreButton);
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forthCanevas();
            }
        });

        buttonMove = (Button)rootView.findViewById(R.id.buttonMove);
        buttonMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(Mode.move);
            }
        });


        //selections
        buttonLasso = (Button)rootView.findViewById(R.id.buttonLasso);
        buttonLasso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(Mode.lasso);
            }
        });

        buttonSelection = (Button)rootView.findViewById(R.id.buttonSelection);
        buttonSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(Mode.selection);
            }
        });

    }

    protected void initializePaint() {
        // Border paint
        int borderColor = ResourcesCompat.getColor(getResources(), R.color.shape, null);
        System.out.println("Default shape color : " + borderColor);
        Paint borderPaint = new Paint();
        borderPaint.setColor(borderColor);

        // Background paint
        int backgroundColor = ResourcesCompat.getColor(getResources(), R.color.shapeFill, null);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        // Text paint
        Paint textPaint = new Paint();
        textPaint.setColor(borderColor);

        defaultStyle = new PaintStyle(borderPaint, backgroundPaint, textPaint, PaintStyle.StrokeType.full);

        // Selection paint
        int selectionColor = ResourcesCompat.getColor(getResources(), R.color.shapeSelection, null);
        selectionPaint = new Paint();
        selectionPaint.setColor(selectionColor);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(SELECTION_STROKE_WIDTH);
        selectionPaint.setStrokeCap(Paint.Cap.ROUND);
        selectionPaint.setAntiAlias(true);

    }
    protected boolean canResize(){
        return selections.size() == 1 && selections.get(0).getClass().equals(ConnectionForm.class);
    }
    /*protected boolean canRotate(){
        return selections.size() == 1;
    }*/
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
                    !selections.isEmpty() && checkEditButton(posX, posY)) { /*Do nothing*/ }
                // Check if canvas is being resized
                else if (isResizingCanvas || checkCanvasResizeHandle(posX, posY))
                    return resizeCanvas(event);
                 switch (currentMode) {
                    case selection:
                        if (canResize() && selections.get(0).canResize(posX,posY)){
                            resizeShape(event);
                            continueListening = true;
                        }
                        else
                            checkSelection(posX,posY);
                        break;
                        case rotate:
                            rotationDetector.onTouchEvent(event, posX, posY);
                            continueListening = true;
                            break;
                    case lasso:
                        doLassoSelection(event);
                        continueListening = true;
                        break;
                    case creation:
                        ArrayList stackElems = new ArrayList();
                        stackElems.add(addShape(posX, posY));
                        addToStack(stackElems, ADD_ACTION);
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

    protected void drawAnchorPoints(){
        for (GenericShape shape : shapes){
            shape.drawAnchorPoints(canvas);
        }
    }

    public void OnRotation(RotationGestureDetector rotationDetector, int posX, int posY) {
        if (rotatingShape != null)
            rotatingShape.rotate(-rotationDetector.getAngle());
        else{
            for (GenericShape shape : selections) {
                if (shape.canRotate(posX, posY)) {
                    shape.rotate(-rotationDetector.getAngle());
                    rotatingShape = shape;
                    return;
                }
            }
        }
    }

    @Override
    public void onEndRotation() {
        rotatingShape = null;
    }

    protected void stack(){
        GenericShape shape = shapes.get(shapes.size()-1);
        stack.push(shape);
        shapes.remove(shape);

    }
    protected void unStack(){
        GenericShape shape = stack.pop();
        shapes.add(shape);
    }
    protected void checkSelection(int x, int y) {
        selections.clear();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)){
                selections.add(shapes.get(i));
                return;
            }
        }
    }
    protected boolean checkEditButton(int x, int y) {
        for (int i = selections.size() - 1; i >= 0; i--) {
            if (selections.get(i).getEditButton().contains(x, y)){
                GenericShape clicked = selections.get(i);
                selections.clear();
                selections.add(clicked);
                clicked.showEditingDialog(getFragmentManager());
                return true;
            }
        }
        return false;
    }

    protected void doLassoSelection(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selectionPath.reset();
                selectionPath.moveTo(posX, posY);
                break;
            case MotionEvent.ACTION_MOVE:
                selectionPath.lineTo(posX, posY);
                Path tempPath = new Path(selectionPath);
                tempPath.close();
                canvas.drawPath(tempPath, selectionPaint);
                break;
            case MotionEvent.ACTION_UP:
                selectionPath.close();
                checkLassoSelection();
                selectionPath.reset();
                break;
        }
    }

    protected void checkLassoSelection() {
        selections.clear();

        for (GenericShape shape : shapes) {
            canvas.clipPath(shape.getSelectionPath(), Region.Op.REPLACE);

            // Check if entire bounding box is contained in selectionPath
            if (!canvas.clipPath(selectionPath, Region.Op.DIFFERENCE))
                selections.add(shape);
        }

        canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), Region.Op.REPLACE);
        // Reset clip to full canvas
    }

    protected GenericShape addShape(int posX, int posY) {
        selections.clear();
        GenericShape nShape = null;
        id = FetchManager.currentInstance.getUserUsername() + Integer.toString(idCpt++);
        switch (currentShapeType) {
            case UmlClass :
                nShape = new UMLClass(id,posX, posY, GenericShape.getDefaultWidth(currentShapeType),
                        GenericShape.getDefaultHeight(currentShapeType), defaultStyle,0);
                nShape.showEditingDialog(getFragmentManager());
                break;
            case Activity :
                nShape = new UMLActivity(id, posX, posY, GenericShape.getDefaultWidth(currentShapeType),
                        GenericShape.getDefaultHeight(currentShapeType), defaultStyle, 0);
                break;
            case Artefact :
                nShape = new UMLArtefact(id, posX, posY,GenericShape.getDefaultWidth(currentShapeType),
                        GenericShape.getDefaultHeight(currentShapeType), defaultStyle, 0);
                break;
            case Role :
                nShape = new UMLRole(id, posX, posY, GenericShape.getDefaultWidth(currentShapeType),
                        GenericShape.getDefaultHeight(currentShapeType), defaultStyle, 0);
                break;
            case Phase :
                nShape = new UMLPhase(Integer.toString(idCpt), posX, posY, GenericShape.getDefaultWidth(currentShapeType),
                        GenericShape.getDefaultHeight(currentShapeType), defaultStyle,0);
                nShape.showEditingDialog(getFragmentManager());
                break;
            case Comment :
                nShape = new Comment(Integer.toString(idCpt), posX, posY,GenericShape.getDefaultWidth(currentShapeType),
                        GenericShape.getDefaultHeight(currentShapeType),defaultStyle);
                nShape.showEditingDialog(getFragmentManager());
                break;
            case Text :
                nShape = new TextBox(Integer.toString(idCpt), posX, posY,GenericShape.getDefaultWidth(currentShapeType),
                        GenericShape.getDefaultHeight(currentShapeType), defaultStyle,0);
                nShape.showEditingDialog(getFragmentManager());
                break;
            case Arrow:
                nShape = new ConnectionForm(id, currentConnectionFormType.toString(),
                        String.format("#%06x", ContextCompat.getColor(getActivity(),
                                R.color.DefaultConnectionFormFillingColor)),String.format("#%06x",ContextCompat.getColor(getActivity(),
                        R.color.DefaultConnectionFormBorderColor)),ConnectionForm.DEFAULT_THICK, ConnectionForm.generateDefaultX(posX),
                        ConnectionForm.generateDefaultY(posY));
        }
        if (nShape != null) {
            shapes.add(nShape);
            selections.clear();
            selections.add(nShape);
        }
        return nShape;
    }

    protected void addToStack(ArrayList<GenericShape> nShapes, String action){
        Pair pair = new Pair(nShapes, action);
        addStack.push(pair);
    }

    protected void drawAllShapes() {
        for(GenericShape shape : shapes)
            shape.drawOnCanvas(canvas);

        if (selections.size() > 0)
            for (GenericShape shape : selections)
                shape.drawSelectionBox(canvas, selectionPaint);
    }
    //width = 1808, height = 1264
    protected void updateCanvas() {
        bitmap.recycle();
        bitmap = Bitmap.createBitmap(iView.getLayoutParams().width, iView.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        iView.setImageBitmap(bitmap);
        //canvas = new Canvas(bitmap);
        canvas.setBitmap(bitmap);

    }
    protected void initializeCanvas(){
        bitmap = Bitmap.createBitmap(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
        ViewGroup.LayoutParams params = iView.getLayoutParams();
        params.width = DEFAULT_CANVAS_WIDTH;
        params.height = DEFAULT_CANVAS_HEIGHT;
        iView.setLayoutParams(params);
        iView.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);
    }

    protected void setShapeType(ShapeType type) {
        currentShapeType = type;
        currentMode = Mode.creation;
    }
    protected void setConnectionFormType(ConnectionFormType type){
        currentConnectionFormType = type;
    }

    protected void setMode(Mode mode) {
        currentMode = mode;
    }

    public void deleteSelection() {
        if (selections.size() > 0) {
            ArrayList<GenericShape> stackElems = new ArrayList<>();
            for (GenericShape shape : selections) {
                if (shape.getClass().equals(ConnectionForm.class))
                    ((ConnectionForm)shape).clearConnection();
                shapes.remove(shape);
                stackElems.add(shape);
            }
            selections.clear();
            addToStack(stackElems,REMOVE_ACTION);


        }
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    protected void tryToAnchor(ConnectionForm connection, int x, int y){
        for (GenericShape shape : shapes) {
            if (shape != connection && !shape.getClass().equals(ConnectionForm.class)) {
                shape.updateAnchor(connection);
            }
        }
    }
    protected void resizeShape(MotionEvent event){
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);
        ConnectionForm connectionForm =((ConnectionForm) selections.get(0));

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
                tryToAnchor(connectionForm,posX, posY);
                connectionForm.finishResize();
                break;
        }

    }
    protected void moveSelectedShape(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (GenericShape shape : selections) {
                    if (shape.contains(posX, posY)) {
                        isMovingSelection = true;
                        lastTouchPosX = posX;
                        lastTouchPosY = posY;
                    }
                }
                if (!isMovingSelection)
                    selections.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMovingSelection) {
                    for (GenericShape shape : selections)
                        shape.relativeMove(posX - lastTouchPosX, posY - lastTouchPosY);

                    lastTouchPosX = posX;
                    lastTouchPosY = posY;

                }
                break;
            case MotionEvent.ACTION_UP:
                isMovingSelection = false;
                break;
        }
    }
    protected void cutSelection() {
        cutShapes.addAll(selections);
        deleteSelection();
    }
    public void duplicateSelection() {
        ArrayList<GenericShape> duplicatedShapes;
        // Check whether to duplicate selected shapes or clipboard (or nothing)
        if (!selections.isEmpty())
            duplicatedShapes = selections;
        else if (!cutShapes.isEmpty())
            duplicatedShapes = cutShapes;
        else return;

        // Same operation in either case
        ArrayList<GenericShape> stackElems = new ArrayList<>();
        for (GenericShape shape : duplicatedShapes){
            GenericShape nShape = shape.clone();
            shapes.add(nShape);
            stackElems.add(nShape);
        }

        if (selections.isEmpty()) {
            cutShapes = stackElems;
        }
        selections.clear();
        selections.addAll(stackElems);

        addToStack(stackElems, ADD_ACTION);
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    protected void reset() {

        if (shapes != null && shapes.size() > 0){
            addToStack(new ArrayList(shapes),REMOVE_ACTION);
            shapes.clear();
        }
        selections.clear();
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }

    public void backCanevas() {
        if (addStack != null && !addStack.empty()){
            Pair pair = addStack.pop();
            selections.clear();
            for (GenericShape shape : (ArrayList<GenericShape>)pair.first){
                if (pair.second.equals(ADD_ACTION)){
                        shapes.remove(shape);
                }
                else if(pair.second.equals(REMOVE_ACTION)){
                    shapes.add(shape);

                }

            }
            removeStack.push(pair);
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }


    }
    public void forthCanevas() {
        if (removeStack != null && !removeStack.empty()){
            Pair pair = removeStack.pop();
            selections.clear();
            for (GenericShape shape : (ArrayList<GenericShape>)pair.first){
                if (pair.second.equals(ADD_ACTION)){
                    shapes.add(shape);
                    selections.add(shape);
                }
                else if(pair.second.equals(REMOVE_ACTION)){
                    shapes.remove(shape);

                }

            }
            addStack.push(pair);
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }

    }

    public boolean checkCanvasResizeHandle(int x, int y) {
        final int cornerTolerance = 30;
        //return !canvas.getClipBounds().contains(x,y);
        return x > canvas.getWidth() - cornerTolerance &&
                x < canvas.getWidth() + cornerTolerance &&
                y > canvas.getHeight() - cornerTolerance &&
                y < canvas.getHeight() + cornerTolerance;

    }

    public boolean checkCanvasWidth(int posX){
        return posX <= canvasBGLayout.getMeasuredWidth() - CANVAS_BACKGROUND_PADDING;
    }
    public boolean checkCanvasHeight(int posY){
        return posY <= canvasBGLayout.getMeasuredHeight() - CANVAS_BACKGROUND_PADDING;
    }

    public boolean resizeCanvas(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isResizingCanvas = true;
                break;
            case MotionEvent.ACTION_MOVE:
                    ViewGroup.LayoutParams params = iView.getLayoutParams();
                    if (checkCanvasWidth(posX))
                        params.width = posX;
                    if (checkCanvasHeight(posY))
                        params.height = posY;
                    iView.setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                isResizingCanvas = false;
                if (this.getClass().equals(CollabImageEditingFragment.class))
                    SocketManager.currentInstance.resizeCanvas(iView.getLayoutParams().width,iView.getLayoutParams().height);
                break;
        }

        updateCanvas();
        drawAllShapes();

        return isResizingCanvas;
    }

    // ------------------------- Dialogs -------------------------
    // TextEditingDialog
    @Override
    public void onTextDialogPositiveResponse(String contents) {
        ((GenericTextShape)selections.get(0)).setText(contents);
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    @Override
    public void onTextDialogNegativeResponse() {
        if (((GenericTextShape)selections.get(0)).getText().equals("")) {
            shapes.removeAll(selections);
            selections.clear();
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }
    }

    // StyleEditingDialog
    @Override
    public void onStyleDialogPositiveResponse(PaintStyle style) {
        selections.get(0).setStyle(style);
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    @Override
    public void onStyleDialogNegativeResponse() {
        selections.get(0).setStyle(defaultStyle);
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }

    // ClassEditingDialog
    @Override
    public void onClassDialogPositiveResponse(String name, String attributes, String methods) {
        ((UMLClass)selections.get(0)).setText(name);
        ((UMLClass)selections.get(0)).setAttributes(attributes);
        ((UMLClass)selections.get(0)).setMethods(methods);
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
}
