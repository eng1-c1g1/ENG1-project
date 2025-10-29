package io.github.maze11.messages;

/**
 * Base class for all messages sent across the messaging system.
 * Can also be used to represent messages with a type and no additional information
 */
public class Message {
    // Storing the type as an enum means that the type of the message can be checked without using if (instanceof) chains
    // Enum comparison is used for performance and ease of use, Eg with the switch statement
    public final MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }
}
