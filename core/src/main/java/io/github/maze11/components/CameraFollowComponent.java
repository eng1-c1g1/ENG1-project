package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Makes the camera follow the transform of this entity
 */
public class CameraFollowComponent implements Component {
    public boolean active = true;
    public Vector2 offset = new Vector2();
}
