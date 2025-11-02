package io.github.maze11.messages;

import com.badlogic.ashley.core.Entity;

/**
 * Sent whenever the player is bitten by a goose
 */
public class GooseBiteMessage extends Message{
    // Represents the entity that bit the player
    private Entity gooseEntity;

    public GooseBiteMessage() {
        super(MessageType.GOOSE_BITE);
    }

    /**
     * Call this after constructing the message. This is separated from the constructor because the message is often
     * created before the entity it is assigned to
     * @param gooseEntity the entity this message is associated with
     */
    public void setGooseEntity(Entity gooseEntity) {
        if (this.gooseEntity != null) {
            throw new RuntimeException("Cannot modify GooseBiteMessage after it has been set.");
        }
        this.gooseEntity = gooseEntity;
    }

    /** Gets the instance of goose that bit the player
     */
    public Entity getGooseEntity() {
        return gooseEntity;
    }
}
