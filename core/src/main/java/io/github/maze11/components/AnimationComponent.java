package io.github.maze11.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains information that allows this entity to have animations.
 * @param <T> An enum, each value of which represents the name of an animation this component contains.
 */
public class AnimationComponent<T extends Enum<T>> implements Component {
    public Map<T, Animation<TextureRegion>> animations = new HashMap<>();
    /** The current animation that this is in */
    public T currentState;
    /** The time since the start of the animation */
    public float elapsed = 0f;
    /** The texture to use to render this object */
    public TextureRegion currentFrame;
}
