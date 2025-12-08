package io.github.maze11.messages;

public class AnkhInteractMessage extends InteractableMessage{

     /** The duration in seconds of the invulnerability effect to give the player */
    public final float duration = 15f;


    public AnkhInteractMessage() {
        super(MessageType.ANKH_INTERACT);
        
    }
    
}
