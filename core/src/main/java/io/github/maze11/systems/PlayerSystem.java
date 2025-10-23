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
    ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    public PlayerSystem(FixedStepper fixedStepper) {
        // now requires physics component for physics based movement
        super(fixedStepper, Family.all(PlayerComponent.class, TransformComponent.class, PhysicsComponent.class).get());
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);

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

        Vector2 direction = getDirectionalInput();

        // update transform directly (primary reference)
        // physics sync system will sync this to physics body

        transform.position.add(
            direction.x * player.moveSpeed * deltaTime,
            direction.y * player.moveSpeed * deltaTime
        );
    }
}
