package factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.AssetLoader;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.messages.InteractableMessage;
import io.github.maze11.systems.physics.PhysicsSystem;

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
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        sprite.texture = assetLoader.get(textureId, Texture.class);
        entity.add(sprite);
    }

    void addInteractable(Entity entity, InteractableMessage message, boolean disappearOnInteract) {
        var interactableComponent = new InteractableComponent();
        interactableComponent.activationMessage = message;
        interactableComponent.disappearOnInteract = disappearOnInteract;
        entity.add(interactableComponent);
    }

    void addBoxCollider(Entity entity, float x, float y,
                                float width, float height,
                                float offsetX, float offsetY,
                                BodyDef.BodyType bodyType,
                                boolean fixedRotation) {
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        physics.setBox(width, height);

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
}
