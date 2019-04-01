package com.projet3.polypaint.CanvasElement;

public abstract class GenericTextShape extends GenericShape {
    public static final int FONT_SIZE = 25;
    protected final int PADDING = 8;
    protected String text = "";

    public GenericTextShape(String id, int x, int y, PaintStyle style) {
        super(id, x, y, 0, FONT_SIZE, style);
        text = "";
        adjustWidthToText();
    }
    protected GenericTextShape(String id, int x, int y, PaintStyle style, String contents) {
        super(id, x, y, 0, FONT_SIZE, style);
        text = contents;
        adjustWidthToText();
    }

    public void setText(String text) {
        this.text = text;
        adjustWidthToText();
    }
    public String getText() {
        return text;
    }

    protected void adjustWidthToText() {
        if (text.length() != 0) {
            // Font is monospace, all characters are same width
            float[] charWidth = new float[1];
            style.getTextPaint().getTextWidths(text, 0, 1, charWidth);
            width = (int) charWidth[0] * text.length();
        }
    }
}
