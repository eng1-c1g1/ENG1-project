package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a bully, which blocks the player until they are given an item
 */

public class BullyComponent implements Component {

    /**
     * The set of states that the bully can be in.
     */

    public enum BullyState {
        BLOCKING, WARNING, UNLOCKED, MOVE, DONE
    }

    /**
     * The animations that the bully can play
     */

    public enum BullyAnimState {
        IDLE_UP,
        IDLE_RIGHT,
        IDLE_DOWN,
        IDLE_LEFT,

        WALK_UP,
        WALK_RIGHT,
        WALK_DOWN,
        WALK_LEFT
    }

    // Sets default settings for the bully
    public BullyState state = BullyState.BLOCKING;
    public BullyAnimState animState = BullyAnimState.IDLE_DOWN;
    public float moveSpeed = 2f;
    // Creates a vector which is used for the bullies end position
    public Vector2 targetPosition = new Vector2();

}
