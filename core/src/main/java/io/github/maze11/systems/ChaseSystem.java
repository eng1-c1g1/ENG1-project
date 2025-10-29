package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.components.*;
import io.github.maze11.systemTypes.FixedStepper;
import io.github.maze11.systemTypes.IteratingFixedStepSystem;

public class ChaseSystem extends IteratingFixedStepSystem {
    ComponentMapper<ChaseComponent> chaseMapper =  ComponentMapper.getFor(ChaseComponent.class);
    ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

    private Entity target;

    public ChaseSystem(FixedStepper fixedStepper) {
        super(fixedStepper, Family.all(ChaseComponent.class, TransformComponent.class, PhysicsComponent.class).get());
    }

    public void setTarget(Entity target){
        this.target = target;
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {

        // Find useful references and values here, to avoid doing this in every state
        var transform = transformMapper.get(entity);
        var targetTransform = transformMapper.get(target);
        // Displacement vector to get to the target
        Vector2 displacement = new Vector2(targetTransform.position).sub(transform.position);
        var data = new ProcessData(entity, deltaTime, chaseMapper.get(entity), transform,
            physicsMapper.get(entity), targetTransform, displacement);

        // Determine what to do based on the state
        switch (chaseMapper.get(entity).state) {
            case IDLE:
                processIdle(data);
                break;
            case CHASE:
                processChase(data);
                break;
            default:
                System.out.println("Unknown state: " + chaseMapper.get(entity).state);
                break;

        }
    }

    private void processIdle(ProcessData data) {
        // If it is within range, switch to chase state
        if (magnitudeIsWithin(data.displacementFromTarget, data.chase.detectionRadius)){
            data.chase.state = ChaseState.CHASE;
            return;
        }
        // Does not need to do anything else while idle

        // Ensure velocity is zero in case it was non-zero from previous state
        data.physics.body.setLinearVelocity(0f, 0f);
    }

    private void processChase(ProcessData data) {
        // If it is too far away, switches to idle state
        if (magnitudeIsWithin(data.displacementFromTarget, data.chase.forgetRadius)){
            data.chase.state = ChaseState.IDLE;
            return;
        }

        // Calculate velocity vector from speed and direction
        var velocity = new Vector2(data.displacementFromTarget).nor().scl(data.chase.speed);
        data.physics.body.setLinearVelocity(velocity);
    }

    /**
     * Returns true if the magnitude of the vector is less than or equal to the limit, false otherwise.
     * @param vector The vector being tested
     * @param limit The maximum magnitude for which the method returns true
     */
    private boolean magnitudeIsWithin(Vector2 vector, float limit){
        // Compare square length to avoid using slow square root operation
        return vector.len2() <= limit * limit;
    }

    private record ProcessData(Entity entity, float deltaTime, ChaseComponent chase, TransformComponent transform,
                               PhysicsComponent physics, TransformComponent target, Vector2 displacementFromTarget) {}
}
