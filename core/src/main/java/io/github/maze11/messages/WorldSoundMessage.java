package io.github.maze11.messages;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

/** Represents a sound at a specific location in the world, with falloff depending on distance from the listener */
public class WorldSoundMessage extends SoundMessage{
    /** The position the sound effect is emitted from */
    public final Vector2 worldPosition;
    /** This is the minimum distance at which the volume of the sound effect is 0 */
    public final float maxRange;

    public WorldSoundMessage(Sound sound, float defaultVolume, Vector2 worldPosition, float maxRange) {
        super(sound, defaultVolume);
        this.worldPosition = worldPosition;
        this.maxRange = maxRange;
    }

    @Override
    public float getEffectiveVolume(Vector2 listenerPosition) {
        // This formula is not accurate to the real world, but it makes balancing volume easier.
        // Thus, it was the preferred approach

        float volumeMultiplier = 1f - (worldPosition.dst(listenerPosition) / maxRange);
        // If out of range
        if (volumeMultiplier < 0f) {
            return 0f;
        }
        return volumeMultiplier * defaultVolume;
    }

}
