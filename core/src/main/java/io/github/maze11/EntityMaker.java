package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.Assets;
import io.github.maze11.components.CameraFollowComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.systems.physics.PhysicsSystem;

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
        physicsComponent.colliderType = PhysicsComponent.ColliderType.BOX;
        physicsComponent.colliderWidth = width;
        physicsComponent.colliderHeight = height;
        
        var world = engine.getSystem(PhysicsSystem.class).getWorld();
        physicsComponent.body = createPhysicsBody(world, physicsComponent, 
            x + width/2, y + height/2, 
            BodyDef.BodyType.StaticBody, false, 0f);
        
        entity.add(physicsComponent);
        return entity;
    }

    // makes the player entity with physics component and sprite
    public Entity makePlayer(float x, float y){
        Entity entity = makeVisibleEntity(x, y, AssetId.PLAYER_TEXTURE);

        var playerComponent = engine.createComponent(PlayerComponent.class);
        entity.add(playerComponent);
        var cameraFollowComponent = engine.createComponent(CameraFollowComponent.class);
        entity.add(cameraFollowComponent);

        // add physics component and create box2d body
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        physicsComponent.colliderType = PhysicsComponent.ColliderType.BOX;
        physicsComponent.colliderWidth = 0.9f;  // 2 * halfWidth
        physicsComponent.colliderHeight = 0.9f; // 2 * halfHeight
        physicsComponent.colliderOffset.set(0f, 0.5f); // offset upwards
        
        var world = engine.getSystem(PhysicsSystem.class).getWorld();
        physicsComponent.body = createPhysicsBody(world, physicsComponent, 
            x, y, 
            BodyDef.BodyType.DynamicBody, true, 0f);
        
        entity.add(physicsComponent);
        return entity;
    }

    // Helper method to create physics body based on PhysicsComponent settings
    private Body createPhysicsBody(World world, PhysicsComponent physics, 
                                   float x, float y, 
                                   BodyDef.BodyType type, 
                                   boolean fixedRotation, 
                                   float linearDamping) {
        // Create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.linearDamping = linearDamping;
        Body body = world.createBody(bodyDef);
        
        // Create collider based on type
        Shape shape;
        if (physics.colliderType == PhysicsComponent.ColliderType.CIRCLE) {
            // Circle collider
            CircleShape circle = new CircleShape();
            circle.setRadius(physics.colliderRadius);
            circle.setPosition(physics.colliderOffset);
            shape = circle;
        }  else {
            // Box collider (default)
            PolygonShape box = new PolygonShape();
            box.setAsBox(
                physics.colliderWidth / 2f, 
                physics.colliderHeight / 2f, 
                physics.colliderOffset, 
                0f
            );
            shape = box;
        }
        
        // Create fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.0f;
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
        return body;
    }
}