package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.MazeGame;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

public class EntityRenderingSystem  extends SortedIteratingSystem {
    private final MazeGame game;
    private ComponentMapper<SpriteComponent> spriteM;
    private ComponentMapper<TransformComponent> transformM;

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


        game.getBatch().draw(sprite.texture,
            transform.position.x + sprite.textureOffset.x,
            transform.position.y + sprite.textureOffset.y,
            sprite.size.x, sprite.size.y
        );
    }
}
