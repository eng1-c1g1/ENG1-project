package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Causes the entity to be the "ears" of the player, listening for sounds at its position
 */
public class AudioListenerComponent implements Component {
    /**
     * The offset of the point around which the sound is centred from the object origin
     */
    public Vector2 offset = new Vector2();
}
