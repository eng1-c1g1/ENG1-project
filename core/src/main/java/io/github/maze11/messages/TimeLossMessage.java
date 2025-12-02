package io.github.maze11.messages;

/**
 * Published whenever player interacts with time lost item
 */

public class TimeLossMessage extends InteractableMessage{
    public TimeLossMessage(){
        super(MessageType.TIME_LOST);
    }

}
