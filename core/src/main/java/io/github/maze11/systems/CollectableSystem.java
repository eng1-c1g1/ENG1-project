package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import io.github.maze11.EntityMaker;
import io.github.maze11.components.CollectableComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.MessageType;

public class CollectableSystem extends EntitySystem {
    ComponentMapper<CollectableComponent> collectableMapper = ComponentMapper.getFor(CollectableComponent.class);
    ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    MessageListener messageListener;
    PooledEngine engine;
    EntityMaker entityMaker;

    public CollectableSystem(MessagePublisher messagePublisher, PooledEngine engine, EntityMaker entityMaker) {
        this.messageListener = new MessageListener(messagePublisher);
        this.engine = engine;
        this.entityMaker = entityMaker;
    }

    @Override
    public void update(float deltaTime) {

        // Dequeue all events and process them
        while (messageListener.hasNext()){
            var message = messageListener.next();

            // Not interested in messages that do not represent collisions
            if (message.type != MessageType.COLLISION) {
                return;
            }
            var collisionMessage = (CollisionMessage) message;

            // Determine if the collision is between a collectable and a player
            if (collectableMapper.has(collisionMessage.entityA) && playerMapper.has(collisionMessage.entityB)) {
                activateCollectable(collisionMessage.entityB, collisionMessage.entityA);
            }
            else if (playerMapper.has(collisionMessage.entityA) && collectableMapper.has(collisionMessage.entityB)){
                activateCollectable(collisionMessage.entityA, collisionMessage.entityB);
            }
            // If not between a collectable and a player, ignore this collision

        }
    }

    private void activateCollectable(Entity player, Entity collectable){
        // Sends a message causing the collectable effect to occur
        var message = collectableMapper.get(collectable).activationMessage;
        messageListener.publisher.publish(message);

        // Remove the collectable object and all its components
        entityMaker.destroy(collectable);
    }
}
