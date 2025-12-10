package io.github.maze11.messages;

import com.badlogic.gdx.math.Vector2;

/**
 * Teleportation System message
 */

public class TeleportationMessage extends InteractableMessage{
    public Vector2 location;
    public TeleportationMessage(Vector2 tpLocation){
        super(MessageType.TELEPORTATION);
        this.location = tpLocation;
    }
}

