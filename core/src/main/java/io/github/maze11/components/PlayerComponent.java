package io.github.maze11.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a player character
 */
public class PlayerComponent implements Component {
    /**
     * Contains the names of the various animations the player can take
     */
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

    /**
     * The current animation of the player
     */
    public PlayerState currentState = PlayerState.IDLE_DOWN;

    /** The default maximum speed of the player */
    public final float maxSpeed = 10f;

    /** Accumulator for any bonuses to speed collected */
    public List<SpeedBonus> speedBonuses = new ArrayList<>();

    /**
     * Represents a bonus to speed the player received such as the coffee power-up
     */
    public static class SpeedBonus {
        public float amount;
        public float timeRemaining;

        public SpeedBonus(float amount, float timeRemaining) {
            this.amount = amount;
            this.timeRemaining = timeRemaining;
        }
    }

    /** whether or not player is invulnerable **/

    public boolean isInvulnerable = false;

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

    /** The time elapsed since the last footstep sound was played */
    public float timeSinceLastFootstep;
    /** The time between two footstep sounds playing */
    public final float timeBetweenFootsteps = 0.3f;
    /** */
    public final float boostFootstepMultiplier = 1.5f;
}
