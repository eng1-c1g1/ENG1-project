package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import io.github.maze11.factory.EntityMaker;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.messages.*;

/**
 * Manages interactable objects, making them publish messages on collision with
 * a player.
 * Interactable objects are objects that cause an effect when they come into
 * contact with the player.
 * For example, collectables and geese.
 */
public class InteractableSystem extends EntitySystem {
    ComponentMapper<InteractableComponent> interactableMapper = ComponentMapper.getFor(InteractableComponent.class);
    ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    MessageListener messageListener;
    PooledEngine engine;
    EntityMaker entityMaker;

    public InteractableSystem(MessagePublisher messagePublisher, PooledEngine engine, EntityMaker entityMaker) {
        this.messageListener = new MessageListener(messagePublisher);
        this.engine = engine;
        this.entityMaker = entityMaker;
    }

    @Override
    public void update(float deltaTime) {

        // Dequeue all events and process them
        while (messageListener.hasNext()) {
            var message = messageListener.next();

            // Not interested in messages that do not represent collisions
            if (message.type != MessageType.COLLISION) {
                return;
            }
            var collisionMessage = (CollisionMessage) message;

            // Determine if the collision is between an interactable and a player
            if (interactableMapper.has(collisionMessage.entityA) && playerMapper.has(collisionMessage.entityB)) {
                handleInteraction(collisionMessage.entityB, collisionMessage.entityA);
            } else if (playerMapper.has(collisionMessage.entityA) && interactableMapper.has(collisionMessage.entityB)) {
                handleInteraction(collisionMessage.entityA, collisionMessage.entityB);
            }
            // If not between an interactable and a player, ignore this collision
        }
    }

    private void handleInteraction(Entity player, Entity interactable) {
        var interactableComponent = interactableMapper.get(interactable);

        if (!interactableComponent.interactionEnabled) {
            return;
        }

        // Sends a message signalling the interaction has occurred
        InteractableMessage specialisedMessage = (InteractableMessage) interactableComponent.activationMessage.clone();
        specialisedMessage.specifyInteraction(player, interactable);
        messageListener.publisher.publish(specialisedMessage);

        // Sends any additional messages that are attached, for example, sound effects
        for (Message message : interactableComponent.additionalMessages) {
            messageListener.publisher.publish(message);
        }

        // Interactions are disabled once it has occurred once, to prevent the
        // interaction firing multiple times in
        // quick succession. Can be re-enabled by other systems if necessary
        interactableComponent.interactionEnabled = false;

        if (interactableComponent.disappearOnInteract) {
            // Remove the interactable object and all its components
            engine.removeEntity(interactable);
        }
    }
}
