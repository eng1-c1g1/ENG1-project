package io.github.maze11.systems.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.maze11.MazeGame;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.components.AnimationComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

public class RenderingSystem extends SortedIteratingSystem {
    private final MazeGame game;

    private final ComponentMapper<SpriteComponent> spriteM;
    private final ComponentMapper<TransformComponent> transformM;
    private final ComponentMapper<AnimationComponent> animM;

    private boolean isDebugging = false;
    private Texture originTexture;

    public RenderingSystem(MazeGame game) {
        super(Family.all(SpriteComponent.class, TransformComponent.class).get(), new RenderOrderComparator());
        this.game = game;

        spriteM = ComponentMapper.getFor(SpriteComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);
        animM = ComponentMapper.getFor(AnimationComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        forceSort();
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteComponent sprite = spriteM.get(entity);
        TransformComponent transform = transformM.get(entity);
        if (!sprite.isShown) return;

        // Determine what texture frame to draw
        TextureRegion region;

        AnimationComponent anim = animM.get(entity);
        if (anim != null && anim.currentFrame != null) {
            region = anim.currentFrame;  // Animated entity
        } else {
            region = new TextureRegion(sprite.texture);  // Static entity
        }

        var batch = game.getBatch();

        float width = sprite.size.x * transform.scale.x;
        float height = sprite.size.y * transform.scale.y;

        // offset logic
        float xOffset = (-0.5f) * width;

        batch.draw(region,
                transform.position.x + (sprite.textureOffset.x * transform.scale.x) + xOffset,
                transform.position.y + (sprite.textureOffset.y * transform.scale.y),
                width,
                height
        );

        // Debug: draw origin indicator
        if (isDebugging) {
            batch.draw(originTexture,
                    transform.position.x - 0.5f,
                    transform.position.y - 0.5f,
                    1f, 1f);
        }
    }

    public RenderingSystem startDebugView() {
        isDebugging = true;
        if (originTexture == null) {
            originTexture = game.getAssetLoader().get(AssetId.ORIGIN_INDICATOR, Texture.class);
        }
        return this;
    }

    public void stopDebugView() {
        isDebugging = false;
    }
}