package io.github.maze11.systems.physics;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.maze11.messages.CollisionManager;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.systemTypes.FixedStepSystem;
import io.github.maze11.systemTypes.FixedStepper;

/**
 *  Handles the Box2D physics simulation for the game.
 * uses a fixed timeste for stable physics update
 */

public class PhysicsSystem extends FixedStepSystem {
    private final World world;

    public PhysicsSystem(FixedStepper stepper, MessagePublisher messagePublisher) {
        super(stepper);
        world = new World(new Vector2(0, 0), true); // No gravity for top-down
        world.setContactListener(new CollisionManager(messagePublisher));
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void removedFromEngine(com.badlogic.ashley.core.Engine engine) {
        world.dispose();
    }

    @Override
    public void fixedUpdate(float deltaTime) {
        world.step(deltaTime, 6, 2);
    }
}
