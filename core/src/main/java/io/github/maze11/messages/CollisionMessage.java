package io.github.maze11.messages;

import javax.swing.text.html.parser.Entity;

public class CollisionMessage extends Message {

    public final Entity entity1;
    public final Entity entity2;

    public CollisionMessage(MessageType type, Entity entity1, Entity entity2) {
        super(MessageType.COLLISION);
        this.entity1 = entity1;
        this.entity2 = entity2;
    }

}
