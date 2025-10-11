package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;

public class DebuggingIndicatorManager {
    final PooledEngine engine;

    private Texture testSquare;

    public DebuggingIndicatorManager(PooledEngine engine){
        this.engine = engine;

        testSquare = new Texture("Test_Square.png");
    }

    public void CreateDebugSquare(float x, float y){
        Entity entity = engine.createEntity();

        SpriteComponent spriteComponent = engine.createComponent(SpriteComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        transformComponent.position.set(x, y);

        entity.add(spriteComponent);
    }

}
