package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

/**
 * Makes the entity chase a target
 */
public class GooseComponent implements Component {
    public GooseState state = GooseState.IDLE;
    public float speed = 4f;
    /** The distance to the target below which it begins chasing */
    public float detectionRadius = 5f;
    /** If it is chasing and the target goes outside this radius, it stops chasing */
    public float forgetRadius = 8f;
}
