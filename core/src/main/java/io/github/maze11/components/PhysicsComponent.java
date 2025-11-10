package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * component that holds physics data for box2d bodies
 * uses private field with getters and setters to prevent inconsistent data when pooling
 */

public class PhysicsComponent implements Component {
    // Box2D body associated with this component
    public Body body;

    // enum defining collider types
    public enum ColliderType {
        BOX,      // Rectangle collider
        CIRCLE,   // Circle collider

    }
    // type of collider (box or circle)
    private ColliderType colliderType = ColliderType.BOX;
    // For BOX: width and height
    private float colliderWidth = 0f;
    private float colliderHeight = 0f;

    // For CIRCLE: radius
    private float colliderRadius = 0f;
    // Offset from body center (for BOX and CIRCLE only)
    private Vector2 colliderOffset = new Vector2(0f, 0f);

    // Gets the collider type
    public ColliderType getColliderType() {
        return colliderType;
    }
    // gets the width of box collider
    public float getColliderWidth() {
        return colliderWidth;
    }
    // gets the height of box collider
    public float getColliderHeight() {
        return colliderHeight;
    }
    // gets radius of circle collider
    public float getColliderRadius() {
        return colliderRadius;
    }
    // gets the offset from body centre for both
    public Vector2 getColliderOffset() {
        return colliderOffset;
    }

     /**
      * Configures this component to act as a box collider
      */
    public PhysicsComponent setBox(float width, float height) {
        this.colliderType = ColliderType.BOX;
        this.colliderWidth = width;
        this.colliderHeight = height;
        this.colliderOffset.set(0f, 0f);
        return this;
    }

    /**
     * Configures this component to act as a box collider with offset
     */
    public PhysicsComponent setBox(float width, float height, float offsetX, float offsetY) {
        this.colliderType = ColliderType.BOX;
        this.colliderWidth = width;
        this.colliderHeight = height;
        this.colliderOffset.set(offsetX, offsetY);
        return this;
    }

    /**
    * Configures this component to act as a circle collider
    */
    public PhysicsComponent setCircle(float radius) {
        this.colliderType = ColliderType.CIRCLE;
        this.colliderRadius = radius;
        this.colliderOffset.set(0f, 0f);
        return this;
    }

    /**
     * Configures this component to act as a circle collider with an offset
     */
    public PhysicsComponent setCircle(float radius, float offsetX, float offsetY) {
        this.colliderType = ColliderType.CIRCLE;
        this.colliderRadius = radius;
        this.colliderOffset.set(offsetX, offsetY);
        return this;
    }
}
