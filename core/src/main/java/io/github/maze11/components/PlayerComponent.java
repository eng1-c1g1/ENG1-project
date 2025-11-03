package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public final float maxSpeed = 10f;
    public final float acceleration = 65f;
    public final float deceleration = 55f;
}
