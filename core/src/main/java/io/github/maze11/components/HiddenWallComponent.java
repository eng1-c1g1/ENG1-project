package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

/**
 * Represents a wall that disappears when the corresponding pressure plate is activated.
 */
public class HiddenWallComponent implements Component {
    /**
     * When a pressure plate with this string is triggered, the wall disappears
     */
    public String triggeredBy;
}
