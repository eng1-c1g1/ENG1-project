package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class CameraFollowComponent implements Component {
    public boolean active = true;
    public Vector2 offset;
}
