package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

/**
 * Makes the entity chase a target
 */
public class GooseComponent implements Component {
    public GooseState state = GooseState.IDLE;
    public float attackSpeed = 4f;
    public float retreatSpeed = 3f;
    /** The distance to the target below which it begins chasing */
    public float detectionRadius = 5f;
    /** If it is chasing and the target goes outside this radius, it stops chasing */
    public float forgetRadius = 10f;

    /** The time the goose retreats for after biting the player */
    public float retreatTime = 2f;
    /** How long the goose has been retreating for */
    public float retreatTimeElapsed;
}
