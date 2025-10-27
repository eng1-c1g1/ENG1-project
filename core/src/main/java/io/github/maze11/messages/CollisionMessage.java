package io.github.maze11.messages;

import com.badlogic.ashley.core.Entity;

public class CollisionMessage extends Message {

    public final Entity entityA;
    public final Entity entityB;

    public CollisionMessage(Entity entityA, Entity entityB) {
        super(MessageType.COLLISION);
        this.entityA = entityA;
        this.entityB = entityB;
    }

}
