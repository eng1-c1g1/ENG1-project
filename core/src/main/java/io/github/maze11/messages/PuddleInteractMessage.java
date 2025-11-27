package io.github.maze11.messages;

public class PuddleInteractMessage extends InteractableMessage{

     /** The duration in seconds of the speed effect to give the player */
    public final float duration = 5f;
    /** The amount in tiles/second to increase the maximum speed of the player by */
    public final float speedBonusAmount = -5f;

    public PuddleInteractMessage() {
        super(MessageType.PUDDLE_INTERACT);
    }
    
}
