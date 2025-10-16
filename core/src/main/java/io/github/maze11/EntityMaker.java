package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.Assets;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

public class EntityMaker {
    private final PooledEngine engine;
    private final Game game;
    private final Assets assets;

    public EntityMaker(PooledEngine engine, MazeGame game) {
        this.engine = engine;
        this.game = game;
        this.assets = game.getAssets();
    }

    private Entity makeEmptyEntity(){
        Entity entity = engine.createEntity();
        engine.addEntity(entity);
        return entity;
    }

    private Entity makeEntity(float x, float y, float xScale, float yScale, float rotation) {
        Entity entity = makeEmptyEntity();

        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.Initialize(x, y, xScale, yScale, rotation);
        entity.add(transform);
        return entity;
    }

    private Entity makeEntity(float x, float y){
        return makeEntity(x, y, 1f, 1f, 0f);
    }

    private Entity makeVisibleEntity(float x, float y, float xScale, float yScale, float rotation, Texture texture) {
        Entity entity = makeEntity(x, y, xScale, yScale, rotation);
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        sprite.texture = texture;
        entity.add(sprite);
        return entity;
    }

    private Entity makeVisibleEntity(float x, float y, AssetId textureId) {
        return makeVisibleEntity(x, y, 1f, 1f, 0f, assets.get(textureId, Texture.class));
    }

    public Entity makePlayer(float x, float y){
        Entity entity = makeVisibleEntity(x, y, AssetId.PlayerTexture);

        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        entity.add(playerComponent);
        return entity;
    }

}
