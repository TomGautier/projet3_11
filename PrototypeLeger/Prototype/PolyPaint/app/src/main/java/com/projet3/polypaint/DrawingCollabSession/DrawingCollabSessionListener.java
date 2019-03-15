package com.projet3.polypaint.DrawingCollabSession;

public interface DrawingCollabSessionListener {

    void onJoinedSession(boolean success);
    void onCreatedSession(boolean success);
    void onAddElement(CollabShape shape);
    void onDeleteElement(String[] ids);
    void onModifyElements(CollabShape[] shapes);
    void onSelectedElements(String[] oldSelections, String[] newSelections);
}
