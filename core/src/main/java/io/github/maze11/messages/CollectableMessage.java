package io.github.maze11.messages;

/**
 * Base class for all messages sent when a message is broadcasted
 */
public abstract class CollectableMessage extends Message {
    public CollectableMessage() {
        super(MessageType.COLLECTABLE_OBTAINED);
    }
}
