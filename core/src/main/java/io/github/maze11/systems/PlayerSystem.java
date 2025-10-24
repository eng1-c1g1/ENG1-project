package io.github.maze11.systems;
import io.github.maze11.components.PhysicsComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.systemTypes.FixedStepper;
import io.github.maze11.systemTypes.IteratingFixedStepSystem;

public class PlayerSystem extends IteratingFixedStepSystem {
    ComponentMapper<PlayerComponent> playerMapper;
    ComponentMapper<TransformComponent> transformMapper;
    ComponentMapper<PhysicsComponent> physicsMapper;

    public PlayerSystem(FixedStepper fixedStepper) {
        // now requires physics component for physics based movement
        super(fixedStepper, Family.all(PlayerComponent.class, TransformComponent.class, PhysicsComponent.class).get());
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Movement is processed in fixed step
    }

    private Vector2 getDirectionalInput(){

        Vector2 direction = new Vector2(0f, 0f);

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction.x = 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            direction.y = 1f;
        }

        // If negative direction is pressed, reduce by 1
        //Reduce instead of setting to handle scenario where both left and right are pressed
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction.x -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            direction.y -= 1f;
        }
        direction.nor();
        return direction;
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        PhysicsComponent physics = physicsMapper.get(entity);

        Vector2 direction = getDirectionalInput();

        //modify velocity, to be handled by physics system for clean collisions
        physics.body.setLinearVelocity(direction.scl(player.moveSpeed));
    }
}
