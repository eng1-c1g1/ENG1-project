package io.github.maze11.messages;

import com.badlogic.gdx.math.Vector2;

/**
 * Teleportation System message
 */

public class TeleportationMessage extends InteractableMessage{
    public Vector2 location = new Vector2(50f, 40f);
    public TeleportationMessage(){super(MessageType.TELEPORTATION);}
}

