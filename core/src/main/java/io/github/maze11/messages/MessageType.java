package io.github.maze11.messages;

/**
 * Represents the type of message sent, used to distinguish what an event represents.
 * This avoids unnecessary type casts and allows easy event sorting using switch statements
 */
public enum MessageType {
    COLLISION, COFFEE_COLLECTED
}
