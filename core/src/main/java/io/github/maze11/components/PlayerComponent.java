package io.github.maze11.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a player character
 */
public class PlayerComponent implements Component {
    public enum PlayerState {
        IDLE_UP,
        IDLE_DOWN,
        IDLE_LEFT,
        IDLE_RIGHT,
        WALK_UP,
        WALK_DOWN,
        WALK_LEFT,
        WALK_RIGHT
    }

    public PlayerState currentState = PlayerState.IDLE_DOWN;

    /* The default maximum speed of the player */
    public final float maxSpeed = 10f;

    /* Accumulator for any bonuses to speed collected */
    public List<SpeedBonus> speedBonuses = new ArrayList<>();

    public static class SpeedBonus {
        public float amount;
        public float timeRemaining;

        public SpeedBonus(float amount, float timeRemaining) {
            this.amount = amount;
            this.timeRemaining = timeRemaining;
        }
    }

    public final float acceleration = 80f;
    public final float deceleration = 50f;
    public final float knockbackRecovery = 25f;

    /** The current knockback velocity the player is experiencing */
    public Vector2 currentKnockback = new Vector2();
    /**
     * The current velocity of the player's natural movement: eg key inputs.
     * Excludes knockback
     */
    public Vector2 naturalVelocity = new Vector2();
}