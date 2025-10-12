package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

/**
 * Used to create visual indicator sprites for debugging purposes. For example, indicators may
 * help test the rendering system.
 */
public class DebuggingIndicatorManager {
    final PooledEngine engine;

    private final Texture testSquare;
    private final MazeGame game;

    public DebuggingIndicatorManager(PooledEngine engine, MazeGame game) {
        this.engine = engine;
        this.game = game;

        // Load the texture using the game's AssetManager
        testSquare = game.getAssetManager().get("Test_Square.png", Texture.class);
    }

    public void CreateDebugSquare(float x, float y){
        Entity entity = engine.createEntity();

        //create components
        SpriteComponent spriteComponent = engine.createComponent(SpriteComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);

        //set components
        transformComponent.position.set(x, y);
        spriteComponent.texture = testSquare;

        //add components to the entity
        entity.add(spriteComponent);
        entity.add(transformComponent);

        engine.addEntity(entity);
    }

}
