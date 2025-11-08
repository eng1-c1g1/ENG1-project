package io.github.maze11.messages;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

/** This message tells the AudioSystem to play a sound effect */
public class SoundMessage extends Message{

    /** The sound effect that this sound plays */
    public final Sound sound;
    /** The volume between 0 and 1 before any modifiers are applied */
    protected final float defaultVolume;

    public SoundMessage(Sound sound, float defaultVolume) {
        super(MessageType.SOUND_EFFECT);
        this.sound = sound;
        this.defaultVolume = defaultVolume;
    }

    /**
     * Calculates the volume to play this sound at. This is different for derived classes of SoundMessage
     * */
    public float getEffectiveVolume(Vector2 listenerPosition){
        // Base SoundMessage is unaffected by distance
        return defaultVolume;
    }
}
