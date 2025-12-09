package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import io.github.maze11.components.AnimationComponent;
import io.github.maze11.components.BullyComponent;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.components.BullyComponent.BullyAnimState;
import io.github.maze11.components.BullyComponent.BullyState;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.fixedStep.IteratingFixedStepSystem;
import io.github.maze11.messages.InteractableMessage;
import io.github.maze11.messages.Message;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.MessageType;
import io.github.maze11.messages.ToastMessage;

public class BullySystem extends IteratingFixedStepSystem {

    ComponentMapper<BullyComponent> bullyMapper = ComponentMapper.getFor(BullyComponent.class);
    ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    ComponentMapper<AnimationComponent> animMapper = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<InteractableComponent> interactableMapper = ComponentMapper
            .getFor(InteractableComponent.class);
    private final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);

    private final MessageListener listener;

    public BullySystem(FixedStepper stepper, MessagePublisher publisher, Engine engine) {
        super(
                stepper,
                Family.all(
                        BullyComponent.class,
                        TransformComponent.class,
                        PhysicsComponent.class,
                        InteractableComponent.class,
                        AnimationComponent.class)
                        .get());

        this.listener = new MessageListener(publisher);
    }

    @Override
    public void fixedUpdate(float deltaTime) {
        handleMessages();
        super.fixedUpdate(deltaTime);
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {

        BullyComponent bully = bullyMapper.get(entity);
        TransformComponent bullyPos = transformMapper.get(entity);
        PhysicsComponent physics = physicsMapper.get(entity);

        @SuppressWarnings("unchecked")
        AnimationComponent<BullyAnimState> anim = animMapper.get(entity);

        // Different behaviour states of the bully
        switch (bully.state) {

            case BLOCKING -> {
                physics.body.setLinearVelocity(0, 0);
                updateIdleAnimation(anim, deltaTime);

            }

            case MOVE -> {
                Vector2 dir = new Vector2(bully.targetPosition).sub(bullyPos.position);

                if (dir.len() < 0.05f) {
                    // Stop Moving
                    physics.body.setLinearVelocity(0, 0);
                    bully.state = BullyComponent.BullyState.DONE;
                    entity.getComponent(InteractableComponent.class).interactionEnabled = false;
                    updateIdleAnimation(anim, deltaTime);
                    return;
                }

                // Walk out the way of the doorway
                Vector2 vel = dir.nor().scl(bully.moveSpeed);
                physics.body.setLinearVelocity(vel);
                updateWalkAnimation(anim, dir, deltaTime);

            }

            case DONE -> {
                physics.body.setLinearVelocity(0, 0);
                anim.currentState = BullyAnimState.IDLE_DOWN;
                updateIdleAnimation(anim, deltaTime);
            }
        }
    }

    /**
     * Handles the messages between the bully and the player
     * Updates the bully state depnding on what message is triggered
     */

    private void handleMessages() {
        while (listener.hasNext()) {
            Message message = listener.next();

            if (message.type != MessageType.BULLY_INTERACT) {
                continue;
            }

            InteractableMessage im = (InteractableMessage) message;
            Entity playerEntity = im.getPlayer();
            Entity bullyEntity = im.getInteractable();

            PlayerComponent pc = playerMapper.get(playerEntity);
            BullyComponent bully = bullyMapper.get(bullyEntity);
            TransformComponent bullyPos = transformMapper.get(bullyEntity);
            InteractableComponent interactable = interactableMapper.get(bullyEntity);

            if (!pc.hasBribe) {
                listener.publisher.publish(new ToastMessage("Back off! You can pass if you give me something!", 3f));
                interactable.interactionEnabled = true;
                return;

            }

            pc.hasBribe = false;
            listener.publisher.publish(new ToastMessage("Thanks for the drink", 3f));

            bully.state = BullyState.MOVE;
            bully.targetPosition.set(bullyPos.position.x + 2.2f, bullyPos.position.y + -1f);

            interactable.interactionEnabled = false;

        }
    }

    /**
     * Handles the transition between the walking animation to idle
     * 
     * @param anim      The set of animations the bully can display
     * @param deltaTime The time since last frame
     */

    private void updateIdleAnimation(AnimationComponent<BullyAnimState> anim, float deltaTime) {
        if (anim.currentState == null) {
            anim.currentState = BullyAnimState.IDLE_DOWN;
        } else {

            anim.currentState = switch (anim.currentState) {
                case WALK_UP, IDLE_UP -> BullyAnimState.IDLE_UP;
                case WALK_RIGHT, IDLE_RIGHT -> BullyAnimState.IDLE_RIGHT;
                case WALK_DOWN, IDLE_DOWN -> BullyAnimState.IDLE_DOWN;
                case WALK_LEFT, IDLE_LEFT -> BullyAnimState.IDLE_LEFT;

            };
        }
        anim.elapsed += deltaTime;

        var animation = anim.animations.get(anim.currentState);

        if (animation != null) {
            anim.currentFrame = animation.getKeyFrame(anim.elapsed, true);
        }
    }

    /**
     * Updates the walking animation of the bully depending its current direction
     * 
     * @param anim      The set of animations the bully can play
     * @param dir       The current velocity the bully is moving
     * @param deltaTime The time since the last frame
     */

    private void updateWalkAnimation(AnimationComponent<BullyAnimState> anim, Vector2 dir, float deltaTime) {

        BullyAnimState newState;

        if (Math.abs(dir.x) > Math.abs(dir.y)) {
            newState = dir.x > 0 ? BullyAnimState.WALK_RIGHT : BullyAnimState.WALK_LEFT;
        } else {
            newState = dir.y > 0 ? BullyAnimState.WALK_UP : BullyAnimState.WALK_DOWN;
        }

        if (anim.currentState != newState) {
            anim.currentState = newState;
            anim.elapsed = 0f;

        } else {
            anim.elapsed += deltaTime;
        }

        var animation = anim.animations.get(anim.currentState);

        if (animation != null) {
            anim.currentFrame = animation.getKeyFrame(anim.elapsed, true);
        }
    }

}
