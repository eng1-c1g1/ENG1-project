package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import io.github.maze11.MazeGame;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.AssetLoader;
import io.github.maze11.components.*;
import io.github.maze11.components.PlayerComponent.PlayerState;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.fixedStep.IteratingFixedStepSystem;
import io.github.maze11.messages.CoffeeCollectMessage;
import io.github.maze11.messages.GooseBiteMessage;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.PiActivatedMessage;
import io.github.maze11.messages.PiCollectMessage;
import io.github.maze11.messages.PuddleInteractMessage;
import io.github.maze11.messages.SoundMessage;

/**
 * Handles input, player movement and other player logic.
 * This includes sending footsteps when the player moves.
 */
public class PlayerSystem extends IteratingFixedStepSystem {

    private final ComponentMapper<PlayerComponent> playerMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<PhysicsComponent> physicsMapper;
    // Generic animation component for PlayerState
    private final ComponentMapper<AnimationComponent> animMapper;

    private final MessageListener messageListener;
    private final MessagePublisher messagePublisher;
    private final AssetLoader assetLoader;

    public PlayerSystem(FixedStepper fixedStepper, MessagePublisher messagePublisher, MazeGame game) {
        super(fixedStepper,
                Family.all(PlayerComponent.class, TransformComponent.class, PhysicsComponent.class).get());

        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
        this.messagePublisher = messagePublisher;
        // Ashley ignores generics at runtime, so this is correct:
        animMapper = ComponentMapper.getFor(AnimationComponent.class); // ComponentMapper<AnimationComponent>>

        this.messageListener = new MessageListener(messagePublisher);
        this.assetLoader = game.getAssetLoader();
    }

    /**
     * Calculate a direction vector from movement input
     */
    private Vector2 getDirectionalInput() {
        Vector2 direction = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            direction.x += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            direction.x -= 1f;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            direction.y += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            direction.y -= 1f;

        if (direction.len2() > 0)
            direction.nor();

        return direction;
    }

    @Override
    public void fixedUpdate(float deltaTime) {
        while (messageListener.hasNext()) {
            var message = messageListener.next();

            switch (message.type) {
                case COLLECT_COFFEE -> processCoffeeCollect((CoffeeCollectMessage) message);
                case PUDDLE_INTERACT -> processPuddleInteract((PuddleInteractMessage) message);
                case GOOSE_BITE -> processGooseBite((GooseBiteMessage) message);
                case PI_COLLECT -> processPiCollect((PiCollectMessage) message);
                default -> {
                }
            }
        }

        super.fixedUpdate(deltaTime);
    }

    private void processCoffeeCollect(CoffeeCollectMessage message) {
        PlayerComponent player = playerMapper.get(message.getPlayer());
        player.speedBonuses.add(new PlayerComponent.SpeedBonus(message.speedBonusAmount, message.duration));
    }

    private void processPuddleInteract(PuddleInteractMessage message) {
         PlayerComponent player = playerMapper.get(message.getPlayer());
          player.speedBonuses.add(new PlayerComponent.SpeedBonus(message.speedBonusAmount, message.duration));

    }

    private void processPiCollect(PiCollectMessage message) {
        PiCollectMessage.numPis++;
        if (PiCollectMessage.numPis == 3) {
            //TODO: Add Cowsay

            messagePublisher.publish(new PiActivatedMessage());
        }
    }

    private void processGooseBite(GooseBiteMessage message) {
        Vector2 playerPos = transformMapper.get(message.getPlayer()).position;
        Vector2 goosePos = transformMapper.get(message.getInteractable()).position;

        Vector2 knockDir = new Vector2(playerPos).sub(goosePos).nor();
        addKnockback(playerMapper.get(message.getPlayer()), knockDir.scl(message.knockbackSpeed));
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);
        PhysicsComponent physics = physicsMapper.get(entity);

        // Generic component pulled as correct type
        AnimationComponent<PlayerState> anim = animMapper.get(entity);

        Vector2 direction = getDirectionalInput();

        // Movement
        float maxSpeed = player.maxSpeed;
        for (int i = 0; i < player.speedBonuses.size(); i++) {
            var bonus = player.speedBonuses.get(i);
            maxSpeed += bonus.amount;
            bonus.timeRemaining -= deltaTime;

            if (bonus.timeRemaining <= 0) {
                player.speedBonuses.remove(i);
                i--;
            }
        }

        Vector2 naturalVelocity = player.naturalVelocity;
        Vector2 desiredVelocity = new Vector2(direction).scl(maxSpeed);

        if (direction.len2() > 0) {
            Vector2 toTarget = desiredVelocity.sub(naturalVelocity);
            float accelStep = player.acceleration * deltaTime;

            if (toTarget.len2() > accelStep * accelStep)
                toTarget.nor().scl(accelStep);

            naturalVelocity.add(toTarget);
        } else {
            float speed = naturalVelocity.len();
            if (speed > 0) {
                float dec = player.deceleration * deltaTime;
                speed = Math.max(speed - dec, 0);
                naturalVelocity.nor().scl(speed);
            }
        }

        if (naturalVelocity.len2() > maxSpeed * maxSpeed)
            naturalVelocity.nor().scl(maxSpeed);

        // Knockback decay
        Vector2 knockback = player.currentKnockback;
        if (knockback.len2() > 0.001f) {
            float r = player.knockbackRecovery * deltaTime;
            float newSpeed = Math.max(knockback.len() - r, 0);
            knockback.nor().scl(newSpeed);
        }

        physics.body.setLinearVelocity(
                naturalVelocity.x + knockback.x,
                naturalVelocity.y + knockback.y);

        // Animation
        PlayerState newState;

        if (direction.len2() == 0) {
            PlayerState last = anim.currentState;

            switch (last) {
                case WALK_UP, IDLE_UP -> newState = PlayerState.IDLE_UP;
                case WALK_DOWN, IDLE_DOWN -> newState = PlayerState.IDLE_DOWN;
                case WALK_LEFT, IDLE_LEFT -> newState = PlayerState.IDLE_LEFT;
                case WALK_RIGHT, IDLE_RIGHT -> newState = PlayerState.IDLE_RIGHT;

                default -> newState = PlayerState.IDLE_DOWN;
            }

        } else {
            if (Math.abs(direction.x) > Math.abs(direction.y)) {
                newState = direction.x > 0 ? PlayerState.WALK_RIGHT : PlayerState.WALK_LEFT;
            } else {
                newState = direction.y > 0 ? PlayerState.WALK_UP : PlayerState.WALK_DOWN;
            }
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

    private void addKnockback(PlayerComponent pc, Vector2 extra) {
        Vector2 current = pc.currentKnockback;
        float maxMag = Math.max(current.len(), extra.len());

        current.add(extra);

        if (current.len() > maxMag)
            current.nor().scl(maxMag);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime){
        PlayerComponent player = playerMapper.get(entity);

        // Only play footsteps while the player is moving
        if (player.naturalVelocity.len2() > 0) {
            accumulateFootstep(player, deltaTime);
        }
    }

    /**
     * Advances the footstep time and play a footstep if it is due
     */
    private void accumulateFootstep(PlayerComponent player, float deltaTime){

        // Footsteps happen faster while boosted
        float timeMultiplier = 1f;
        if (!player.speedBonuses.isEmpty()) {
            timeMultiplier = player.boostFootstepMultiplier;
        }

        // Accumulate the time
        player.timeSinceLastFootstep += deltaTime *  timeMultiplier;

        // If it is time for another footstep, take it
        if (player.timeSinceLastFootstep > player.timeBetweenFootsteps){
            player.timeSinceLastFootstep = 0f;
            messageListener.publisher.publish(new SoundMessage(assetLoader.get(AssetId.FOOTSTEP, Sound.class), 0.5f, 0.6f));
        }
    }
}
