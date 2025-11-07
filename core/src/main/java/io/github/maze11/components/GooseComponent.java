package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a goose, which wanders aimlessly, but attacks the player if they get too close
 */
public class GooseComponent implements Component {
    public enum GooseState {
        WANDER, CHASE, RETREAT
    }

    public enum GooseAnimState {
        IDLE_UP,
        IDLE_RIGHT,
        IDLE_DOWN,
        IDLE_LEFT,

        WALK_UP,
        WALK_RIGHT,
        WALK_DOWN,
        WALK_LEFT
    }

    public GooseState state = GooseState.WANDER;
    public GooseAnimState animState = GooseAnimState.IDLE_DOWN;

    public float chaseSpeed = 7f;
    public float retreatSpeed = 7f;
    public float wanderSpeed =  1.5f;

    /** The maximum distance from the home position within which the goose chooses a point to wander towards */
    public float wanderRadius = 3f;
    /** The distance to the wander destination below which the goose picks a new destination */
    public float wanderAcceptDistance = 0.2f;
    /** The point towards which the goose wanders when in the wander state */
    public Vector2 currentWanderWaypoint;

    /** The distance to the target below which it begins chasing */
    public float detectionRadius = 6f;
    /** If it is chasing and the target goes outside this radius, it stops chasing */
    public float forgetRadius = 12f;

    /** The time the goose retreats for after biting the player */
    public float retreatTime = 2f;
    /** How long the goose has been retreating for */
    public float retreatTimeElapsed;
    /** The position around which the goose idles */
    public Vector2 homePosition;
}
