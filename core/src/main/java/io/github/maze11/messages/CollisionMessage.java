package io.github.maze11.messages;

import com.badlogic.ashley.core.Entity;

/**
 * Sent when two objects start colliding in the physics engine. Contains references to both references involved
 * in no particular order
 */
public class CollisionMessage extends Message {

    public final Entity entityA;
    public final Entity entityB;

    public CollisionMessage(Entity entityA, Entity entityB) {
        super(MessageType.COLLISION);
        this.entityA = entityA;
        this.entityB = entityB;
    }

}
