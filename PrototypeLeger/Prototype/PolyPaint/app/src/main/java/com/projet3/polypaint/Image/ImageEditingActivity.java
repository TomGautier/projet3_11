package com.projet3.polypaint.Image;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.media.Image;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.Chat.Chat;
import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.Chat.SocketManager;
import com.projet3.polypaint.HomeActivity;
import com.projet3.polypaint.R;
import com.projet3.polypaint.UserInformation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;

public class ImageEditingActivity extends Fragment {


    private Button buttonClass;
    private Button buttonRole;
    private Button buttonActivity;
    private Button buttonArtefact;
    private Button buttonCanvas;
    private Button buttonSelection;
    private Button buttonLasso;
    private Button buttonReset;
    private Button buttonDuplicate;
    private Button buttonDelete;
    private ImageButton buttonRestore;
    private ImageButton buttonBack;

    private enum Mode{selection, lasso, creation}
    private enum ShapeType{uml_class, uml_activity, uml_artefact, uml_role}

    private final float DEFAULT_STROKE_WIDTH = 2f;
    private final float SELECTION_STROKE_WIDTH = 4f;
    private final String ADD_ACTION = "ADD";
    private final String REMOVE_ACTION = "REMOVE";

    private Canvas canvas;
    private PaintStyle defaultStyle;
    private Bitmap bitmap;
    private ImageView iView;
    private ArrayList<GenericShape> shapes;
    private Stack<Pair<ArrayList<GenericShape>, String>> addStack;
    private Stack<Pair<ArrayList<GenericShape>, String>> removeStack;

    private Mode currentMode = Mode.creation;
    private ShapeType currentShapeType = ShapeType.uml_class;

    private Paint selectionPaint;
    private boolean selectionMode = false;
    private ArrayList<GenericShape> selections = null;
    private Path selectionPath = new Path();
    private View rootView;


    public ImageEditingActivity() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.activity_image_editing, container, false);
        iView = (ImageView)rootView.findViewById(R.id.canvasView);
        shapes = new ArrayList<>();
        addStack = new Stack<>();
        removeStack = new Stack<>();

        initializeButtons();
        initializePaint();

        setTouchListener();
        return rootView;
    }

    private void initializeButtons(){
        //forms
        buttonActivity = (Button)rootView.findViewById(R.id.buttonActivity);
        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeTypeToUmlActivity(v);
            }
        });

        buttonArtefact = (Button)rootView.findViewById(R.id.buttonArtefact);
        buttonArtefact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeTypeToUmlArtefact(v);
            }
        });

        buttonClass = (Button)rootView.findViewById(R.id.buttonClass);
        buttonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeTypeToUmlClass(v);
            }
        });

        buttonRole = (Button)rootView.findViewById(R.id.buttonRole);
        buttonRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeTypeToUmlRole(v);
            }
        });


        //actions
        buttonDelete = (Button)rootView.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelection(v);
            }
        });

        buttonDuplicate = (Button)rootView.findViewById(R.id.buttonDuplicate);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicateSelection(v);
            }
        });

        buttonReset = (Button)rootView.findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset(v);
            }
        });

        buttonBack = (ImageButton)rootView.findViewById(R.id.backButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backCanevas(v);
            }
        });

        buttonRestore = (ImageButton)rootView.findViewById(R.id.restoreButton);
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forthCanevas(v);
            }
        });


        //selections
        buttonLasso = (Button)rootView.findViewById(R.id.buttonLasso);
        buttonLasso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setModeToLasso(v);
            }
        });

        buttonSelection = (Button)rootView.findViewById(R.id.buttonSelection);
        buttonSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setModeToSelection(v);
            }
        });

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

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener() {
        iView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                updateCanvas();

                boolean continueListening = false;

                int posX = (int)event.getX(0);
                int posY = (int)event.getY(0);

                switch (currentMode) {
                    case selection :
                        checkSelection(posX, posY);
                        break;
                    case lasso :
                        doLassoSelection(event);
                        continueListening = true;
                        break;
                    case creation :
                        ArrayList stackElems = new ArrayList();
                        stackElems.add(addShape(posX, posY));
                        addToStack(stackElems, ADD_ACTION);
                        break;
                }

                drawAllShapes();
                view.invalidate();

                return continueListening;
            }
        });
    }

    private void checkSelection(int x, int y) {
        selections = new ArrayList<>();

        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).getBoundingBox().contains(x, y)){
                selections.add(shapes.get(i));
                return;
            }
        }

        selections = null;
    }

    private void doLassoSelection(MotionEvent event) {
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

    private void checkLassoSelection() {
        selections = new ArrayList<>();

        for (GenericShape shape : shapes) {
            canvas.clipRect(shape.getBoundingBox(), Region.Op.REPLACE);

            // Check if entire bounding box is contained in selectionPath
            if (!canvas.clipPath(selectionPath, Region.Op.DIFFERENCE))
                selections.add(shape);
        }

        // Reset clip to full canvas
        canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), Region.Op.REPLACE);

        if (selections.isEmpty())
            selections = null;
    }

    private GenericShape addShape(int posX, int posY) {
        selections = null;
        GenericShape nShape = null;
        switch (currentShapeType) {
            case uml_class :
                nShape = new UMLClass(posX, posY, defaultStyle);
                break;
            case uml_activity :
                nShape = new UMLActivity(posX, posY, defaultStyle);
                break;
            case uml_artefact :
                nShape = new UMLArtefact(posX, posY, defaultStyle);
                break;
            case uml_role :
                nShape = new UMLRole(posX, posY, defaultStyle);
                break;
        }
        if (nShape != null)
            shapes.add(nShape);

        return nShape;

    }
    private void addToStack(ArrayList<GenericShape> nShapes, String action){
        Pair pair = new Pair(nShapes, action);
        addStack.push(pair);
    }


    private void drawAllShapes() {
        for(GenericShape shape : shapes)
            shape.drawOnCanvas(canvas);

        if (selections != null)
            for (GenericShape shape : selections)
                shape.drawSelectionBox(canvas, selectionPaint);
    }

    private void updateCanvas() {
        bitmap = Bitmap.createBitmap(iView.getWidth(), iView.getHeight(), Bitmap.Config.ARGB_8888);
        iView.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);
    }



    public void setModeToSelection(View button) { currentMode = Mode.selection; }
    public void setModeToLasso(View button) { currentMode = Mode.lasso; }

    public void setShapeTypeToUmlClass(View button) { setShapeType(ShapeType.uml_class); }
    public void setShapeTypeToUmlActivity(View button) { setShapeType(ShapeType.uml_activity); }
    public void setShapeTypeToUmlArtefact(View button) { setShapeType(ShapeType.uml_artefact); }
    public void setShapeTypeToUmlRole(View button) { setShapeType(ShapeType.uml_role); }

    private void setShapeType(ShapeType type) {
        currentShapeType = type;
        currentMode = Mode.creation;
    }

    public void deleteSelection(View button) {
        if (selections != null) {
            ArrayList<GenericShape> stackElems = new ArrayList<>();
            for (GenericShape shape : selections) {
                shapes.remove(shape);
                stackElems.add(shape);
            }
            selections = null;
            addToStack(stackElems,REMOVE_ACTION);


        }
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    public void duplicateSelection(View button) {
        if (selections != null && selections.size() > 0){
            ArrayList<GenericShape> stackElems = new ArrayList<>();
            for (GenericShape shape : selections){
                GenericShape nShape = shape.clone();
                shapes.add(nShape);
                stackElems.add(nShape);
            }
            addToStack(stackElems, ADD_ACTION);
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }

    }
    public void reset(View button) {

        if (shapes != null && shapes.size() > 0){
            addToStack(new ArrayList(shapes),REMOVE_ACTION);
            shapes.clear();
        }
        selections = null;
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
        /*if (selections != null && selections.size() > 0)
            selections.clear();*/
    }

    public void backCanevas(View button) {
        if (addStack != null && !addStack.empty()){
            Pair pair = addStack.pop();
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
    public void forthCanevas(View button) {
        if (removeStack != null && !removeStack.empty()){
            Pair pair = removeStack.pop();
            for (GenericShape shape : (ArrayList<GenericShape>)pair.first){
                if (pair.second.equals(ADD_ACTION)){
                    shapes.add(shape);
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
}
