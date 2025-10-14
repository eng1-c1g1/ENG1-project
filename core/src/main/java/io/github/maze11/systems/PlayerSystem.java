package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.MazeGame;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.TransformComponent;

public class PlayerSystem extends IteratingSystem {
    private final MazeGame game;
    ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    public PlayerSystem(MazeGame game) {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());

        this.game = game;
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Ideally, this should be refactored to update in line with a constant frame rate, via the same system
        //used by the physics simulation. This will ensure the player input runs the same on all machines
        PlayerComponent player = playerMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);

        Vector2 offset = getDirectionalInput().scl(deltaTime * player.moveSpeed);
        transform.position.add(offset);
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
}
