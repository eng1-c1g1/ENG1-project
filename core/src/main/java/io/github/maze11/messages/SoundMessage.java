package io.github.maze11.messages;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

/** This message tells the AudioSystem to play a sound effect */
public class SoundMessage extends Message{

    /** The sound effect that this sound plays */
    public final Sound sound;
    /** When this is set to 0, the sound is always played at the same pitch.
     * At 1, the pitch variance is highest*/
    public final float pitchRandomness;
    /** The volume between 0 and 1 before any modifiers are applied */
    protected final float defaultVolume;

    public SoundMessage(Sound sound, float defaultVolume) {
        super(MessageType.SOUND_EFFECT);
        this.sound = sound;
        this.defaultVolume = defaultVolume;
        this.pitchRandomness = 0f;
    }

    public SoundMessage(Sound sound, float defaultVolume, float pitchRandomness) {
        super(MessageType.SOUND_EFFECT);
        this.sound = sound;
        this.defaultVolume = defaultVolume;
        this.pitchRandomness = pitchRandomness;
    }

    /**
     * Calculates the volume to play this sound at. This is different for derived classes of SoundMessage
     * */
    public float getEffectiveVolume(Vector2 listenerPosition){
        // Base SoundMessage is unaffected by distance
        return defaultVolume;
    }
}
