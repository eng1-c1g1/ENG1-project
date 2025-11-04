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
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) direction.x += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) direction.x -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) direction.y += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) direction.y -= 1f;

        if (direction.len2() > 0f) direction.nor();
        return direction;
    }

    @Override
    public void fixedUpdate(float deltaTime) {

        //Process new messages
        while (messageListener.hasNext()){
            var message = messageListener.next();

            if (message instanceof CoffeeCollectMessage){
                System.out.println("Coffee collected");
            }
        }
        super.fixedUpdate(deltaTime);
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);
        PhysicsComponent physics = physicsMapper.get(entity);
        Vector2 direction = getDirectionalInput();

        Vector2 velocity = physics.body.getLinearVelocity();
        Vector2 desiredVelocity = new Vector2(direction).scl(player.maxSpeed);

        if (direction.len2() > 0) {
            //Accelerate
            Vector2 toTarget = desiredVelocity.sub(velocity);
            float accelStep = player.acceleration * deltaTime;

            if (toTarget.len2() > accelStep * accelStep) {
                toTarget.nor().scl(accelStep);
            }
            velocity.add(toTarget);
        } else {
            //Decelerate
            float speed = velocity.len();
            if (speed > 0) {
                float decelAmount = player.deceleration * deltaTime;
                speed = Math.max(speed - decelAmount, 0);
                velocity.nor().scl(speed);
            }
        }

        //Clamp to max speed just in case
        if (velocity.len2() > player.maxSpeed * player.maxSpeed) {
            velocity.nor().scl(player.maxSpeed);
        }

        //modify velocity, to be handled by physics system for clean collisions
        physics.body.setLinearVelocity(velocity);
    }

    /** Adds knockback to the player. Performs calculations to avoid two knockbacks in the same direction stacking */
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
