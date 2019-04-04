package com.projet3.polypaint.DrawingCollabSession;

public interface DrawingCollabSessionListener {

    void onJoinedSession(String drawingSessionId);
    //void onCreatedSession(boolean success);
    void onAddElement(CollabShape shape, String author);
    void onDuplicateElements(CollabShape[] shapes, String author);
    void onDuplicateCutElements(CollabShape[] shapes, String author);
    void onDeleteElement(String[] ids, String author);
    void onCutElements(String[] ids, String author);
    void onModifyElements(CollabShape[] shapes, String author);
    void onSelectedElements(String[] oldSelections, String[] newSelections, String author);
    void onStackElement(String id, String author);
    void onUnstackElement(CollabShape shape, String author);
    void onNewUserJoined(String[] players);
    void onResizeCanvas(int width, int height);

}
