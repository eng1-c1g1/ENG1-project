package io.github.maze11.messages;

/**
 * Sent whenever the player is bitten by a goose
 */
public class GooseBiteMessage extends InteractableMessage{
    public final float knockbackSpeed = 15f;
    public GooseBiteMessage() {
        super(MessageType.GOOSE_BITE);
    }
}
