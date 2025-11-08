package io.github.maze11.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.fixedStep.IteratingFixedStepSystem;

/**
 * syncs position of entities' transform components with their box2d physics bodies
 * ensures visual representation matches physics simulation
 */
public class PhysicsSyncSystem extends IteratingFixedStepSystem {
    private final ComponentMapper<PhysicsComponent> physicsM = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);

    public PhysicsSyncSystem(FixedStepper stepper) {
        super(stepper, Family.all(PhysicsComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = physicsM.get(entity);
        TransformComponent transform = transformM.get(entity);

        // Sync the physics with the transform in case the transform is edited outside the physics engine
        physics.body.setTransform(transform.position.x, transform.position.y, 0f);
    }
}
