package io.github.maze11.messages;

/**
 * Base class for all messages sent across the messaging system
 */
public abstract class Message {
    public final MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }
}
