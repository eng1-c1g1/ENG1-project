package io.github.maze11.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.fixedStep.IteratingFixedStepSystem;

/**
 * Physics -> Transform: copy Box2D position back after simulation.
 */
public class PhysicsToTransformSystem extends IteratingFixedStepSystem {
    private final ComponentMapper<PhysicsComponent> physicsM = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);

    public PhysicsToTransformSystem(FixedStepper stepper) {
        super(stepper, Family.all(PhysicsComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {
        var physics = physicsM.get(entity);
        var transform = transformM.get(entity);
        transform.position.set(physics.body.getPosition());
    }
}
