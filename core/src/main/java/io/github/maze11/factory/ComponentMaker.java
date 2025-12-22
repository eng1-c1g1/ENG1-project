package io.github.maze11.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import io.github.maze11.MazeGame;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.AssetLoader;
import io.github.maze11.components.*;
import io.github.maze11.messages.InteractableMessage;
import io.github.maze11.messages.Message;
import io.github.maze11.systems.physics.PhysicsSystem;

import java.util.List;

/**
 * Used by EntityMaker to add components to entities
 */
class ComponentMaker {

    private final Engine engine;
    private final AssetLoader assetLoader;

    ComponentMaker (Engine engine, AssetLoader assetLoader) {
        this.engine = engine;
        this.assetLoader = assetLoader;
    }

    void addTransform(Entity entity, float x, float y, float xScale, float yScale) {
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.Initialize(x, y, xScale, yScale);
        entity.add(transform);
    }

    void addTransform(Entity entity, float x, float y) {
        addTransform(entity, x, y, 1f, 1f);
    }

    void addSprite(Entity entity, AssetId textureId) {
        addSprite(entity, textureId, 1f, 1f, 0f, 0f);
    }

    void addSprite(Entity entity, AssetId textureId, float sizeX, float sizeY, float textureOffsetX, float textureOffsetY) {
        
        // If no sprite batch fetched (e.g. if a headless test is being ran), don't add a sprite
        if (MazeGame.batch == null) {
            return;
        }

        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);

        //If texture not provided, it should be null
        if (textureId != null) {
            sprite.texture = assetLoader.get(textureId, Texture.class);
        }

        sprite.size.set(sizeX, sizeY);
        sprite.textureOffset.set(textureOffsetX, textureOffsetY);

        entity.add(sprite);
    }

    void addInteractable(Entity entity, InteractableMessage message, boolean disappearOnInteract, List<Message> additionalMessages) {
        var interactableComponent = new InteractableComponent();
        interactableComponent.activationMessage = message;

        if (additionalMessages != null) {
            interactableComponent.additionalMessages = additionalMessages;
        }
        interactableComponent.disappearOnInteract = disappearOnInteract;
        interactableComponent.interactionEnabled = true;
        entity.add(interactableComponent);
    }

    void addBoxCollider(Entity entity, float x, float y,
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

    void addCircleCollider(Entity entity, float x, float y,
                                   float radius, float xOffset, float yOffset,
                                   BodyDef.BodyType bodyType) {
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        physics.setCircle(radius, xOffset, yOffset);

        var world = engine.getSystem(PhysicsSystem.class).getWorld();
        physics.body = createPhysicsBody(entity, world, physics, x, y, bodyType, false, 0f);

        entity.add(physics);
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
                0f);
            shape = box;
        }

        // Create fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.0f;

        var fixture = body.createFixture(fixtureDef);

        // Make the entity the user data so that the game can track which objects
        // collide with which
        fixture.setUserData(entity);

        shape.dispose();

        return body;
    }

    void addTimer(Entity entity, float durationSeconds) {
        TimerComponent timer = engine.createComponent(TimerComponent.class);
        timer.timeRemaining = durationSeconds;
        timer.totalTime = durationSeconds;
        timer.isRunning = true;
        timer.hasExpired = false;
        entity.add(timer);
    }

    void addHiddenWall(Entity entity, String triggeredBy){
        HiddenWallComponent hiddenWall = engine.createComponent(HiddenWallComponent.class);
        hiddenWall.triggeredBy = triggeredBy;
        entity.add(hiddenWall);
    }

    void addGoose(Entity entity, float x, float y){
        GooseComponent goose = engine.createComponent(GooseComponent.class);
        goose.homePosition = new Vector2(x, y);
        goose.currentWanderWaypoint = null;
        entity.add(goose);
    }

    void addAudioListener(Entity entity, Vector2 offset){
        var audioListener = new AudioListenerComponent();
        audioListener.offset.set(0f, 1f);
        entity.add(audioListener);
    }
}
