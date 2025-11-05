package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a player character
 */
public class PlayerComponent implements Component {
    public final float maxSpeed = 10f;
    public final float acceleration = 80f;
    public final float deceleration = 50f;
    public final float knockbackRecovery = 25f;

    /** The current knockback velocity the player is experiencing */
    public Vector2 currentKnockback = new Vector2();
    /** The current velocity of the player's natural movement: eg key inputs. Excludes knockback */
    public Vector2 naturalVelocity = new Vector2();
}
