package io.github.maze11.messages;

/** Requests a toast to appear on the screen */
public class ToastMessage extends Message {

    /** The text to display on the toast */
    public final String text;
    /** The duration to display the toast for */
    public final float duration;

    public ToastMessage(String text, float duration) {
        super(MessageType.TOAST_SHOW);
        this.text = text;
        this.duration = duration;
    }
}
