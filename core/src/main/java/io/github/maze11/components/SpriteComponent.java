package io.github.maze11.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class SpriteComponent implements Component {

    public Texture texture;
    public boolean isShown;
    public RenderLayer renderLayer;

    // The position specified here is the local position of the texture relative to the object origin
    // It should be used to create an offset between the two features
    //For characters, the feet of the character should be placed at the object origin
    public Vector2 textureOffset;
    public Vector2 size;
}
