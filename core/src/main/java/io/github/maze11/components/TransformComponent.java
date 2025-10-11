package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component {
    public Vector2 position;
    public Vector2 scale = new Vector2(1f,1f);
    public float rotation;
}
