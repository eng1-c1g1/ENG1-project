package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

import io.github.maze11.messages.InteractableMessage;

/**
 * Represents an object that the player can interact with via a collision
 */
public class InteractableComponent implements Component {
    // This message is sent when the item is collected
    public InteractableMessage activationMessage;
    public boolean disappearOnInteract = false;
    public boolean interactionEnabled = true;
}
