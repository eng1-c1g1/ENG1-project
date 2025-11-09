package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

import io.github.maze11.messages.InteractableMessage;
import io.github.maze11.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object that the player can interact with via a collision
 */
public class InteractableComponent implements Component {
    /** The message sent when the item is collected */
    public InteractableMessage activationMessage;
    /** This contains any messages that should be sent out along with the activation message */
    public List<Message> additionalMessages =  new ArrayList<>();
    /** This being true causes the entity to be destroyed upon interaction */
    public boolean disappearOnInteract = false;
    /** Interactions are only registered while this is set to true */
    public boolean interactionEnabled = true;
}
