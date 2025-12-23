package io.github.maze11.messages;

// CHANGED: Created PiActivatedMessage class
// Is sent once all 3 pi's have been activated
public class PiActivatedMessage extends Message{

    public static boolean cowsayActivated = false;
    public PiActivatedMessage() {
        super(MessageType.PI_ACTIVATED);
        cowsayActivated = true;
    }
}
