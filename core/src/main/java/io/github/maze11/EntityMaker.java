package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

public class EntityMaker {
    private final PooledEngine engine;

    public EntityMaker(PooledEngine engine, MazeGame game) {
        this.engine = engine;
    }

    public Entity makeEmptyEntity(){
        Entity entity = engine.createEntity();
        engine.addEntity(entity);
        return entity;
    }

    public Entity makeEntity(float x, float y, float xScale, float yScale, float rotation) {
        Entity entity = makeEmptyEntity();

        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.Initialize(x, y, xScale, yScale, rotation);
        entity.add(transform);
        return entity;
    }

    public Entity makeEntity(float x, float y){
        return makeEntity(x, y, 1f, 1f, 0f);
    }

    public Entity makeVisibleEntity(float x, float y, float xScale, float yScale, float rotation, Texture texture) {
        Entity entity = makeEntity(x, y, xScale, yScale, rotation);
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        sprite.texture = texture;
        entity.add(sprite);
        return entity;
    }

    public Entity makeVisibleEntity(float x, float y, Texture texture) {
        return makeVisibleEntity(x, y, 1f, 1f, 0f, texture);
    }

    public Entity makePlayer(float x, float y){
        // TODO: substitute the player texture here
        Entity entity = makeVisibleEntity(x, y, null);

        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        entity.add(playerComponent);
        return entity;
    }

}
