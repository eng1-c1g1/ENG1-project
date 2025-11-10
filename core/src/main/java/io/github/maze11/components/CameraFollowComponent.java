package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Makes the camera follow the transform of this entity
 */
public class CameraFollowComponent implements Component {
    /** When active, the camera follows the entity with this component */
    public boolean active = true;
    /** The point, relative to the object origin, that the camera should follow */
    public Vector2 offset = new Vector2();
}
