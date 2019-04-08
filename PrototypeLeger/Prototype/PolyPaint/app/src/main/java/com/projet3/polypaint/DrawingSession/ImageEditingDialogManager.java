package com.projet3.polypaint.DrawingSession;

import android.app.FragmentManager;

import com.projet3.polypaint.CanvasElement.PaintStyle;

import java.util.ArrayList;

public class ImageEditingDialogManager {

    public interface ImageEditingDialogSubscriber {
        void onTextDialogPositiveResponse(String contents, PaintStyle style);
        void onTextDialogNegativeResponse();
        void onStyleDialogPositiveResponse(PaintStyle style);
        void onStyleDialogNegativeResponse();
        void onClassDialogPositiveResponse(String name, String attributes, String methods, PaintStyle style);
        void onClassDialogNeutralResponse(String name, String attributes, String methods);
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
            if (contents != null && style != null)
                s.onTextDialogPositiveResponse(contents, style);
        }
    }
    void onClassDialogPositiveClick(PaintStyle style, String name, String attributes, String methods) {
        for (ImageEditingDialogSubscriber s : subscribers) {
            if (name != null && style != null)
                s.onClassDialogPositiveResponse(name, attributes, methods, style);
        }
    }
    public void onClassDialogNeutralClick(String name, String attributes, String methods) {
        for (ImageEditingDialogSubscriber s : subscribers) {
            if (name != null)
                s.onClassDialogNeutralResponse(name, attributes, methods);
        }
    }
    void onStyleDialogPositiveClick(PaintStyle style) {
        for (ImageEditingDialogSubscriber s : subscribers)
            s.onStyleDialogPositiveResponse(style);
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
