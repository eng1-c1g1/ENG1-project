package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.Assets;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.systems.PhysicsSystem;

/**
 * Used to create entities within a scene, within an engine
 */
public class EntityMaker {
    private final PooledEngine engine;
    private final Assets assets;

    public EntityMaker(PooledEngine engine, MazeGame game) {
        this.engine = engine;
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
        public Entity makeWall(float x, float y, float width, float height) {
        Entity entity = makeEmptyEntity();
        
        // Add physics component for collision
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        var world = engine.getSystem(PhysicsSystem.class).getWorld();
        
        // Create static body (walls don't move)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width/2, y + height/2);
        Body body = world.createBody(bodyDef);
        
        // Create box shape for the wall
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        body.createFixture(shape, 0f);
        shape.dispose();
        
        physicsComponent.body = body;
        entity.add(physicsComponent);
        return entity;
    }
    // makes the player entity with physics component and sprite 
    public Entity makePlayer(float x, float y){
        Entity entity = makeVisibleEntity(x, y, AssetId.PlayerTexture);

        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        entity.add(playerComponent);

        // get the player's sprite component to determine size

        // add physics component and create box2d body 
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        var world = engine.getSystem(PhysicsSystem.class).getWorld();

        // create  physics body for the player
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true; // prevent rotation when hitting walls
        Body body = world.createBody(bodyDef);

        // create box shape that matches sprite size
        PolygonShape box = new PolygonShape();

        float halfWidth = 0.45f;
        float halfHeight = 0.45f;
        
        // offset fro box upwards so it matches sprite visually 
        box.setAsBox(halfWidth, halfHeight, new Vector2(0f, 0.5f), 0f);
        // attach box shape to body via fixture
        body.createFixture(box, 1.0f);
        box.dispose();
         // store body in physics component
        physicsComponent.body = body;
        entity.add(physicsComponent);
        return entity;
    }

}
