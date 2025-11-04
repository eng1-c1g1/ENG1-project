package io.github.maze11.messages;

import com.badlogic.ashley.core.Entity;

/**
 * Sent whenever the player is bitten by a goose
 */
public class GooseBiteMessage extends InteractableMessage{
    public GooseBiteMessage() {
        super(MessageType.GOOSE_BITE);
    }
}
