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

    public DebuggingIndicatorManager(PooledEngine engine){
        this.engine = engine;

        testSquare = new Texture("Test_Square.png");
    }

    //use overloading instead of default values to prevent people specifying one dimension but not the other
    public void CreateDebugSquare(float x, float y){
        CreateDebugSquare(x, y, 1f, 1f);
    }

    public void CreateDebugSquare(float x, float y, float xSize, float ySize){
        Entity entity = engine.createEntity();

        //create components
        SpriteComponent spriteComponent = engine.createComponent(SpriteComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);

        //set components
        transformComponent.position.set(x, y);
        transformComponent.scale.set(xSize, ySize);
        spriteComponent.texture = testSquare;

        //add components to the entity
        entity.add(spriteComponent);
        entity.add(transformComponent);

        engine.addEntity(entity);
    }

}
