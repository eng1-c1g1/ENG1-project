package io.github.maze11.messages;

/**
 * Published whenever the player collects a coffee item
 */
public class CoffeeCollectMessage extends Message {
    public final float duration = 5f;
    public final float speedMultiplier = 2f;

    public CoffeeCollectMessage() {
        super(MessageType.COLLECT_COFFEE);
    }
}
