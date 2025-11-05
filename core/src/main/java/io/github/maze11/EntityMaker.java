package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.AssetLoader;
import io.github.maze11.components.CameraFollowComponent;
import io.github.maze11.components.GooseComponent;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TimerComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.messages.CoffeeCollectMessage;
import io.github.maze11.messages.GooseBiteMessage;
import io.github.maze11.messages.InteractableMessage;
import io.github.maze11.messages.MessageType;
import io.github.maze11.systems.physics.PhysicsSystem;

/**
 * Used to create entities within a scene, within an engine
 */
public class EntityMaker {
    private final PooledEngine engine;
    private final AssetLoader assetLoader;

    public EntityMaker(PooledEngine engine, MazeGame game) {
        this.engine = engine;
        this.assetLoader = game.getAssetLoader();
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
        return makeVisibleEntity(x, y, 1f, 1f, 0f, assetLoader.get(textureId, Texture.class));
    }

    // Adds a box collider to an entity
    private void addBoxCollider(Entity entity, float x, float y,
                                float width, float height,
                                BodyDef.BodyType bodyType,
                                boolean fixedRotation) {
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        physics.setBox(width, height);

        var world = engine.getSystem(PhysicsSystem.class).getWorld();
        physics.body = createPhysicsBody(entity, world, physics, x, y, bodyType, fixedRotation, 0f);

        entity.add(physics);
    }

    // Adds a box collider with offset to an entity
    private void addBoxCollider(Entity entity, float x, float y,
                                float width, float height,
                                float offsetX, float offsetY,
                                BodyDef.BodyType bodyType,
                                boolean fixedRotation) {
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        physics.setBox(width, height, offsetX, offsetY);

        var world = engine.getSystem(PhysicsSystem.class).getWorld();
        physics.body = createPhysicsBody(entity, world, physics, x, y, bodyType, fixedRotation, 0f);

        entity.add(physics);
    }

    // Adds a circle collider to an entity
    private void addCircleCollider(Entity entity, float x, float y,
                                   float radius, float xOffset, float yOffset,
                                   BodyDef.BodyType bodyType) {
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        physics.setCircle(radius, xOffset, yOffset);

        var world = engine.getSystem(PhysicsSystem.class).getWorld();
        physics.body = createPhysicsBody(entity, world, physics, x, y,  bodyType, false, 0f);

        entity.add(physics);
    }
    // Creates a wall entity with box collider
    public Entity makeWall(float x, float y, float width, float height) {
        Entity entity = makeEmptyEntity();
        addBoxCollider(entity, x + width/2, y + height/2, width, height,
                      BodyDef.BodyType.StaticBody, false);
        return entity;
    }

    // Creates the player entity with sprite, camera follow, and physics
    public Entity makePlayer(float x, float y){
        Entity entity = makeVisibleEntity(x, y, AssetId.PLAYER_TEXTURE);

        entity.add(engine.createComponent(PlayerComponent.class));
        entity.add(engine.createComponent(CameraFollowComponent.class));

        addBoxCollider(entity, x, y, 0.9f, 0.9f, 0f, 0.5f,
                      BodyDef.BodyType.DynamicBody, true);

        return entity;
    }

    // Creates a countdown timer entity
    public Entity makeTimer(float durationSeconds) {
        Entity entity = makeEmptyEntity();

        TimerComponent timer = engine.createComponent(TimerComponent.class);
        timer.timeRemaining = durationSeconds;
        timer.totalTime = durationSeconds;
        timer.isRunning = true;
        timer.hasExpired = false;

        entity.add(timer);
        return entity;
    }



    // Helper method to create physics body based on PhysicsComponent settings
    private Body createPhysicsBody(Entity entity, World world, PhysicsComponent physics,
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


        // Create shape based on collider type
        Shape shape;
        if (physics.getColliderType() == PhysicsComponent.ColliderType.CIRCLE) {
            // Create circle shape
            CircleShape circle = new CircleShape();
            circle.setRadius(physics.getColliderRadius());
            circle.setPosition(physics.getColliderOffset());
            shape = circle;
        } else {
            // Create box shape
            PolygonShape box = new PolygonShape();
            box.setAsBox(
                physics.getColliderWidth() / 2f,
                physics.getColliderHeight() / 2f,
                physics.getColliderOffset(),
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

        var fixture = body.createFixture(fixtureDef);

        // Make the entity the user data so that the game can track which objects collide with which
        fixture.setUserData(entity);

        shape.dispose();

        return body;
    }

    private Entity makeInteractable(float x, float y, InteractableMessage message, boolean disappearOnInteract, AssetId assetId) {
        Entity entity = makeVisibleEntity(x, y, assetId);
        var interactableComponent = new InteractableComponent();
        interactableComponent.activationMessage = message;
        interactableComponent.disappearOnInteract = disappearOnInteract;
        entity.add(interactableComponent);
        return entity;
    }

    public Entity makeCoffee(float x, float y){
        Entity entity = makeInteractable(x, y, new CoffeeCollectMessage(),true, AssetId.COFFEE);
        addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    public Entity makeCheckInCode(float x, float y){
        Entity entity = makeInteractable(x, y, new InteractableMessage(MessageType.CHECK_IN_CODE_COLLECT),true, AssetId.CHECK_IN);
        addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    public Entity makeGoose(float x, float y){
        Entity entity = makeInteractable(x, y, new GooseBiteMessage(),false, AssetId.GOOSE);

        // Add behaviour and physics
        var gooseComponent = engine.createComponent(GooseComponent.class);
        gooseComponent.homePosition = new Vector2(x, y);
        entity.add(gooseComponent);
        addBoxCollider(entity, x, y, 0.9f, 0.9f, 0f, 0.5f,
            BodyDef.BodyType.DynamicBody, true);

        return entity;
    }

    public Entity makeExit(float x, float y){
        Entity entity = makeInteractable(x, y, new InteractableMessage(MessageType.EXIT_MAZE),false, AssetId.EXIT);
        addBoxCollider(entity, x, y, 1f, 1f, 0f, 0.5f,
            BodyDef.BodyType.StaticBody, true);
        return entity;
    }
}
