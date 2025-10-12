package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a graphic to be displayed on the current entity. Will only display on entities that also have a transform
 */
public class SpriteComponent implements Component {

    public Texture texture;
    public boolean isShown = true;
    public RenderLayer renderLayer = RenderLayer.OBJECT;

    // The position specified here is the local position of the texture relative to the object origin
    // It should be used to create an offset between the two features
    //For characters, the feet of the character should be placed at the object origin
    public Vector2 textureOffset = new Vector2();
    public Vector2 size =  new Vector2(1f, 1f);

}
