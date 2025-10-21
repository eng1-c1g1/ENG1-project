package io.github.maze11.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 *  Handles the Box2D physics simulation for the game.
 * uses a fixed timeste for stable physics update
 */

public class PhysicsSystem extends EntitySystem {
    private final World world;
    private final float TIME_STEP = 1 / 60f;
    private float accumulator = 0f;

    public PhysicsSystem() {
        world = new World(new Vector2(0, 0), true); // No gravity for top-down
    }

    @Override
    public void update(float deltaTime) {
        accumulator += deltaTime;
        // step the physics world in fixed increments
        while (accumulator >= TIME_STEP) {
            // 6 velocity, 2 positions iterations (standard values)
            world.step(TIME_STEP, 6, 2); 
            accumulator -= TIME_STEP;
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void removedFromEngine(com.badlogic.ashley.core.Engine engine) {
        world.dispose();
    }
}