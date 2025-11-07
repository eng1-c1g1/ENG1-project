package io.github.maze11.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent<T extends Enum<T>> implements Component {
    public Map<T, Animation<TextureRegion>> animations = new HashMap<>();
    public T currentState;
    public float elapsed = 0f;
    public TextureRegion currentFrame;
}