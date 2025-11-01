package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a goose, which wanders aimlessly, but attacks the player if they get too close
 */
public class GooseComponent implements Component {
    public GooseState state = GooseState.WANDER;
    public float chaseSpeed = 4f;
    public float retreatSpeed = 3f;
    public float wanderSpeed =  1f;

    /** The maximum distance from the home position within which the goose chooses a point to wander towards */
    public float wanderRadius = 2f;
    /** The distance to the wander destination below which the goose picks a new destination */
    public float wanderAcceptDistance = 0.2f;
    /** The point towards which the goose wanders when in the wander state */
    public Vector2 currentWanderWaypoint;

    /** The distance to the target below which it begins chasing */
    public float detectionRadius = 5f;
    /** If it is chasing and the target goes outside this radius, it stops chasing */
    public float forgetRadius = 10f;

    /** The time the goose retreats for after biting the player */
    public float retreatTime = 2f;
    /** How long the goose has been retreating for */
    public float retreatTimeElapsed;
    /** The position around which the goose idles */
    public Vector2 homePosition;
}
