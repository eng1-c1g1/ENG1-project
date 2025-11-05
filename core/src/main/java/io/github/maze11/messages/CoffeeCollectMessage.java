package io.github.maze11.messages;

/**
 * Published whenever the player collects a coffee item
 */
public class CoffeeCollectMessage extends InteractableMessage {
    public final float duration = 5f;
    public final float speedBonusAmount = 5f;

    public CoffeeCollectMessage() {
        super(MessageType.COLLECT_COFFEE);
    }
}
