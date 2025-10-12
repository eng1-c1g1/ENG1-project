package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.MazeGame;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

/**
 * Renders any entity with both a TransformComponent and a SpriteComponent
 */
public class EntityRenderingSystem  extends SortedIteratingSystem {
    private final MazeGame game;
    private final ComponentMapper<SpriteComponent> spriteM;
    private final ComponentMapper<TransformComponent> transformM;

    // Debug tools
    private boolean isDebugging = false;
    private Texture originTexture;
    private Sprite originSprite;

    public EntityRenderingSystem startDebugging() {
        isDebugging = true;
        if (originTexture == null) {
            originTexture = new Texture("origin_indicator.png");
            originSprite = new Sprite(originTexture);
            originSprite.setSize(1f, 1f);
        }
        return this;
    }
    public void stopDebugging() {
        isDebugging = false;
    }

    public EntityRenderingSystem(MazeGame game) {
        super(Family.all(SpriteComponent.class).get(), new RenderOrderComparator());
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

        //TODO: check that this scaling method works in an sensible manner for sizes other than (1f,1f)
        //This was written quickly and may yield curious results
        batch.draw(sprite.texture,
            transform.position.x + sprite.textureOffset.x,
            transform.position.y + sprite.textureOffset.y,
            sprite.size.x, sprite.size.y
        );

        if (isDebugging) {
            //draws an indicator at the origin of the item
            originSprite.setPosition(transform.position.x,  transform.position.y);
            originSprite.draw(batch);
        }
    }
}
