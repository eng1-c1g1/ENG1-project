package io.github.maze11.systems.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.maze11.systemTypes.FixedStepSystem;
import io.github.maze11.systemTypes.FixedStepper;

/**
 *  Handles the Box2D physics simulation for the game.
 * uses a fixed timeste for stable physics update
 */

public class PhysicsSystem extends FixedStepSystem {
    private final World world;

    public PhysicsSystem(FixedStepper stepper) {
        super(stepper);
        world = new World(new Vector2(0, 0), true); // No gravity for top-down
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void removedFromEngine(com.badlogic.ashley.core.Engine engine) {
        SafeBodyDestroy.drain(world); // Destroy any remaining unwanted bodies
        world.dispose();
    }

    @Override
    public void fixedUpdate(float deltaTime) {
        world.step(deltaTime, 6, 2);

        // Drain the destruction queue after stepping the world
        SafeBodyDestroy.drain(world);
    }
}
