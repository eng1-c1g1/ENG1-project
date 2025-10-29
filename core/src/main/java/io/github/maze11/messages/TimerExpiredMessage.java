package io.github.maze11.messages;

/**
 * Message published when a timer entity expires (reaches zero).
 * Used to trigger game over state or other time-based events.
 */
public class TimerExpiredMessage extends Message {
    /**
     * Creates a new timer expired message.
     */
    public TimerExpiredMessage() {
        super(MessageType.TIMER_EXPIRED);
    }
}