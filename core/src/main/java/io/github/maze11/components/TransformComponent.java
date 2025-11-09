package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents the physical positioning of an entity. Any entity that exists somewhere in the world
 * should expect to include this component.
 */
public class TransformComponent implements Component {
    public Vector2 position = new Vector2();
    public Vector2 scale = new Vector2(1f,1f);

    public void Initialize(float x, float y, float xScale, float yScale)
    {
        position.x = x;
        position.y = y;
        scale.x = xScale;
        scale.y = yScale;
    }
}
