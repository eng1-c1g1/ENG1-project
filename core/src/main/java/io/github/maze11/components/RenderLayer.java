package io.github.maze11.components;

/**
 * These are the layers the entity rendering system uses to group entities and determine which should render
 * before which
 */
public enum RenderLayer {
    //The layers are sorted in order of priority:
    // Layers are rendered in the order they are declared here
    TILE, OBJECT
}
