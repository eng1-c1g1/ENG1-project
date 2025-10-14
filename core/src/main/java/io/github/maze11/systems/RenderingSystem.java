package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import io.github.maze11.MazeGame;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

/**
 * Renders any entity with both a TransformComponent and a SpriteComponent
 */
public class RenderingSystem extends SortedIteratingSystem {
    private final MazeGame game;
    private final ComponentMapper<SpriteComponent> spriteM;
    private final ComponentMapper<TransformComponent> transformM;

    // Debug tools
    private boolean isDebugging = false;
    private Texture originTexture;

    /**
     * Draws extra info onto the screen to help with debugging. Can impact performance. Call stopDebugView() to exit
     * @return Returns reference to itself, for convenient usage
     */
    public RenderingSystem startDebugView() {
        isDebugging = true;
        if (originTexture == null) {
            originTexture = game.getAssetManager().get("origin_indicator.png", Texture.class); // Load the texture using AssetManager
        }
        return this;
    }
    public void stopDebugView() {
        isDebugging = false;
    }

    public RenderingSystem(MazeGame game) {
        super(Family.all(SpriteComponent.class, TransformComponent.class).get(), new RenderOrderComparator());
        this.game = game;
        spriteM = ComponentMapper.getFor(SpriteComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);
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

        var batch = game.getBatch();

        float effectiveTextureSizeX = transform.scale.x * sprite.size.x;
        float effectiveTextureSizeY = transform.scale.y * sprite.size.y;
        //This offset ensures the texture renders centred in the middle of the bottom edge
        float xOffset = (-0.5f) * effectiveTextureSizeX;

        batch.draw(sprite.texture,
            transform.position.x + (sprite.textureOffset.x * transform.scale.x) + xOffset,
            transform.position.y + (sprite.textureOffset.y * transform.scale.y),
            effectiveTextureSizeX, effectiveTextureSizeY
        );

        if (isDebugging) {
            //draws an indicator at the origin of the item
            //offset -0.5f so that the centre displays the origin
            batch.draw(originTexture, transform.position.x - 0.5f,  transform.position.y - 0.5f, 1f, 1f);
        }
    }
}
