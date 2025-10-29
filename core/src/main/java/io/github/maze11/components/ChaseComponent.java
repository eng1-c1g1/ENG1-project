package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

/**
 * Makes the entity chase a target
 */
public class ChaseComponent implements Component {
    public ChaseState state = ChaseState.IDLE;
    public float speed = 4f;
    /** The distance to the target below which it begins chasing */
    public float detectionRadius = 5f;
    /** If it is chasing and the target goes outside this radius, it stops chasing */
    public float forgetRadius = 8f;
}
