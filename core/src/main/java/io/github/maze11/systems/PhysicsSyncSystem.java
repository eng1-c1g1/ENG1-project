package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.TransformComponent;

/**
 * syncs position of entities' transform components with their box2d ohysics bodies
 * ensures visual representation matches physics simulation
 */
public class PhysicsSyncSystem extends IteratingSystem {
    private final ComponentMapper<PhysicsComponent> physicsM = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);

    public PhysicsSyncSystem() {
        super(Family.all(PhysicsComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = physicsM.get(entity);
        TransformComponent transform = transformM.get(entity);
        // transform is authoritative for position
        float vx = (transform.position.x - physics.body.getPosition().x) / deltaTime;
        float vy = (transform.position.y - physics.body.getPosition().y) / deltaTime;

        physics.body.setLinearVelocity(vx, vy);

    }
}