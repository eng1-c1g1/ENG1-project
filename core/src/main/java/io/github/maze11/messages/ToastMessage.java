package io.github.maze11.messages;

public class ToastMessage extends Message {

    public final String text;
    public final float duration;

    public ToastMessage(String text, float duration) {
        super(MessageType.TOAST_SHOW);
        this.text = text;
        this.duration = duration;
    }
}