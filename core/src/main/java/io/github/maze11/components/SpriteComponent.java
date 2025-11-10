package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a graphic to be displayed on the current entity.
 * Will only display on entities that also have a transform.
 */
public class SpriteComponent implements Component {

    /** The texture that is rendered for the entity */
    public Texture texture;
    /** The entity is only rendered when isShown is true */
    public boolean isShown = true;

    /** The layer used to render the object.
     *Any objects on higher layers are rendered in front of objects of lower layers
     */
    public RenderLayer renderLayer = RenderLayer.OBJECT;

    /**
     * The offset applied to the texture relative to the transform it is attached to. A value of (0f,0f) places
     * the object origin in the middle of the bottom-left edge. The offset should be chosen such that the object
     * origin is wherever the object touches the ground.
     */
    public Vector2 textureOffset = new Vector2();
    /**
     * The size of the texture on the screen in world units when the transform has a scale of (1f,1f)
     */
    public Vector2 size = new Vector2(1f, 1f);

}
