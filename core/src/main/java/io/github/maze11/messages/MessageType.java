package io.github.maze11.messages;

/**
 * Represents the type of message sent, used to distinguish what an event represents.
 * This avoids unnecessary type casts and allows easy event sorting using switch statements
 */
public enum MessageType {
    COLLISION,
    TIMER_EXPIRED, // timer has reached zero
    COLLECT_COFFEE,
    GOOSE_BITE,
    EXIT_MAZE,
    CHECK_IN_CODE_COLLECT
}
