package io.github.maze11.messages;

// CHANGED: Created PiCollectMessage class
public class PiCollectMessage extends InteractableMessage{
    
    public static int numPis = 0;
    
    public PiCollectMessage() {
        super(MessageType.PI_COLLECT);
    }
}
