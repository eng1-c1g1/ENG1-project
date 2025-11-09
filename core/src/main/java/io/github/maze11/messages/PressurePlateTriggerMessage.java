package io.github.maze11.messages;

/**
 * Published whenever the player collects a coffee item
 */
public class PressurePlateTriggerMessage extends InteractableMessage {
    public String triggers;

    public PressurePlateTriggerMessage(String triggers) {
        super(MessageType.PRESSURE_PLATE_TRIGGER);
        this.triggers = triggers;
    }
}
