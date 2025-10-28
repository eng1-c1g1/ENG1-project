package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import io.github.maze11.messages.CollectableMessage;

public class CollectableComponent implements Component {
    // This message is sent when the item is collected
    public CollectableMessage activationMessage;
}
