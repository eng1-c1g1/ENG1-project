package io.github.maze11.messages;

public class PelletInteractMessage extends InteractableMessage{

     /** The duration in seconds of the invulnerability effect to give the player */
    public final float duration = 15f;

    /** the invulnerability effect to give player**/

    public PelletInteractMessage() {
        super(MessageType.PELLET_INTERACT);
    }
    
}
