package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * component that stores the Box2D physics body for an entity.
 * Used by systems to access and update physics properties.
 */
public class PhysicsComponent implements Component {
    // holds the Box2D body for physics/collision
    public Body body;
}
