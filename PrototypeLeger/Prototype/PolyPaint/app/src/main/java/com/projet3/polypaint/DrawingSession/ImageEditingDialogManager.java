package com.projet3.polypaint.DrawingSession;

import android.app.FragmentManager;

import com.projet3.polypaint.CanvasElement.PaintStyle;

import java.util.ArrayList;

public class ImageEditingDialogManager {
    public interface ImageEditingDialogSubscriber {
        void onTextDialogPositiveResponse(String contents);
        void onTextDialogNegativeResponse();
        void onStyleDialogPositiveResponse(PaintStyle style);
        void onStyleDialogNegativeResponse();
        void onClassDialogPositiveResponse(String name, String attributes, String methods);
    }

    private static ImageEditingDialogManager instance = null;
    private static ArrayList<ImageEditingDialogSubscriber> subscribers;

    private ImageEditingDialogManager() {
        subscribers = new ArrayList<>();
    }


    public static ImageEditingDialogManager getInstance() {
        if (instance == null) {
            instance = new ImageEditingDialogManager();
        }

        return instance;
    }


    public void subscribe(ImageEditingDialogSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(ImageEditingDialogSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void showTextEditingDialog(FragmentManager fragmentManager, PaintStyle style, String contents) {
        TextEditingDialog dialog = new TextEditingDialog();
        dialog.setStyle(style);
        dialog.setContents(contents);
        dialog.show(fragmentManager, "text editing");
    }

    public void showStyleDialog(FragmentManager fragmentManager, PaintStyle style) {
        StyleEditingDialog dialog = new StyleEditingDialog();
        dialog.setStyle(style);
        dialog.show(fragmentManager, "style editing");
    }

    public void showTextAndStyleDialog(FragmentManager fragmentManager, PaintStyle style, String contents) {
        TextAndStyleEditingDialog dialog = new TextAndStyleEditingDialog();
        dialog.setStyle(style);
        dialog.setContents(contents);
        dialog.show(fragmentManager, "text and style editing");
    }

    public void showClassEditingDialog(FragmentManager fragmentManager, PaintStyle style, String name, String attributes, String methods) {
        ClassEditingDialog dialog = new ClassEditingDialog();
        dialog.setStyle(style);
        dialog.setContents(name, attributes, methods);
        dialog.show(fragmentManager, "class editing");
    }


    void onDialogPositiveClick(PaintStyle style, String contents) {
        for (ImageEditingDialogSubscriber s : subscribers) {
            if (style != null)
                s.onStyleDialogPositiveResponse(style);
            if (contents != null && !contents.isEmpty())
                s.onTextDialogPositiveResponse(contents);
        }
    }
    void onClassDialogPositiveClick(PaintStyle style, String name, String attributes, String methods) {
        for (ImageEditingDialogSubscriber s : subscribers) {
            if (style != null)
                s.onStyleDialogPositiveResponse(style);
            if (name != null && !name.isEmpty())
                s.onClassDialogPositiveResponse(name, attributes, methods);
        }
    }
    void onStyleDialogNegativeClick() {
        for (ImageEditingDialogSubscriber s : subscribers)
            s.onStyleDialogNegativeResponse();
    }
    void onTextDialogNegativeClick() {
        for (ImageEditingDialogSubscriber s : subscribers)
            s.onTextDialogNegativeResponse();
    }
}
