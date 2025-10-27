package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsComponent implements Component {
    public Body body;
    
    // Collider shape type
    public enum ColliderType { 
        BOX,      // Rectangle collider
        CIRCLE,   // Circle collider
        
    }
    
    public ColliderType colliderType = ColliderType.BOX;
    
    // For BOX: width and height
    public float colliderWidth = 32f;
    public float colliderHeight = 32f;
    
    // For CIRCLE: radius
    public float colliderRadius = 16f;
    
   
    
    // Offset from body center (for BOX and CIRCLE only)
    public Vector2 colliderOffset = new Vector2(0f, 0f);
}