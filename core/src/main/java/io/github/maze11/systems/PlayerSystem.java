package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.messages.CoffeeCollectMessage;
import io.github.maze11.messages.GooseBiteMessage;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.fixedStep.IteratingFixedStepSystem;

public class PlayerSystem extends IteratingFixedStepSystem {
    ComponentMapper<PlayerComponent> playerMapper;
    ComponentMapper<TransformComponent> transformMapper;
    ComponentMapper<PhysicsComponent> physicsMapper;
    MessageListener messageListener;

    public PlayerSystem(FixedStepper fixedStepper, MessagePublisher messagePublisher) {
        // now requires physics component for physics based movement
        super(fixedStepper, Family.all(PlayerComponent.class, TransformComponent.class, PhysicsComponent.class).get());
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        this.messageListener = new MessageListener(messagePublisher);
    }

    private Vector2 getDirectionalInput(){

        Vector2 direction = new Vector2(0f, 0f);

        // Reduce instead of setting to handle scenario where both left and right or up and down are pressed
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) direction.x += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) direction.x -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) direction.y += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) direction.y -= 1f;

        if (direction.len2() > 0f) direction.nor();
        return direction;
    }

    @Override
    public void fixedUpdate(float deltaTime) {

        //Process new messages
        while (messageListener.hasNext()){
            var message = messageListener.next();

            switch (message.type) {
                case COLLECT_COFFEE -> {
                    System.out.println("Coffee collected");
                }
                case GOOSE_BITE ->  {
                    processGooseBite((GooseBiteMessage) message);
                }
            }
        }
        super.fixedUpdate(deltaTime);
    }

    private void processGooseBite(GooseBiteMessage message){
        // Get vectors to calculate with
        Vector2 playerPos = transformMapper.get(message.getPlayer()).position;
        Vector2 goosePos = transformMapper.get(message.getInteractable()).position;

        // Work out the direction and apply the knockback
        Vector2 knockDirection = new Vector2(playerPos).sub(goosePos).nor();
        addKnockback(playerMapper.get(message.getPlayer()), knockDirection.scl(message.knockbackSpeed));
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);
        PhysicsComponent physics = physicsMapper.get(entity);
        Vector2 direction = getDirectionalInput();

        Vector2 naturalVelocity = player.naturalVelocity;
        Vector2 desiredVelocity = new Vector2(direction).scl(player.maxSpeed);

        if (direction.len2() > 0) {
            //Accelerate
            Vector2 toTarget = desiredVelocity.sub(naturalVelocity);
            float accelStep = player.acceleration * deltaTime;

            if (toTarget.len2() > accelStep * accelStep) {
                toTarget.nor().scl(accelStep);
            }
            naturalVelocity.add(toTarget);
        } else {
            //Decelerate
            float speed = naturalVelocity.len();
            if (speed > 0) {
                float decelAmount = player.deceleration * deltaTime;
                speed = Math.max(speed - decelAmount, 0);
                naturalVelocity.nor().scl(speed);
            }
        }

        //Clamp to max speed just in case
        if (naturalVelocity.len2() > player.maxSpeed * player.maxSpeed) {
            naturalVelocity.nor().scl(player.maxSpeed);
        }

        // Update the knockback values
        Vector2 knockbackVelocity = player.currentKnockback;
        // Compare to small value, not zero to account for any floating point precision errors
        if (knockbackVelocity.len2() > 0.001f) {
            float recoverAmount = player.knockbackRecovery * deltaTime;
            float newSpeed = Math.max(knockbackVelocity.len() - recoverAmount, 0);
            knockbackVelocity.nor().scl(newSpeed);
        }

        //modify velocity, to be handled by physics system for clean collisions
        physics.body.setLinearVelocity(naturalVelocity.x + knockbackVelocity.x,  naturalVelocity.y + knockbackVelocity.y);
    }

    /** Adds knockback to the player. Performs calculations to avoid knockbacks stacking with each other */
    private void addKnockback(PlayerComponent playerComponent, Vector2 extraKnockback) {
        var currentKnockback = playerComponent.currentKnockback;

        // Find what the maximum knockback is
        float maxMagnitude = currentKnockback.len2() > extraKnockback.len2() ? currentKnockback.len() : extraKnockback.len();
        currentKnockback.add(extraKnockback);

        // If the knockbacks were in the same direction, scale so that the result is no larger than the largest of the two
        // This prevents a player being launched very far if colliding with two sources of knockback at once
        if (currentKnockback.len2() > maxMagnitude * maxMagnitude) {
            currentKnockback.nor().scl(maxMagnitude);
        }
    }
}
