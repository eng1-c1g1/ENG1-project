package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.maze11.components.AnimationComponent;
import io.github.maze11.components.GooseComponent;
import io.github.maze11.components.GooseComponent.GooseAnimState;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.fixedStep.IteratingFixedStepSystem;
import io.github.maze11.messages.GooseBiteMessage;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.MessageType;

public class GooseSystem extends IteratingFixedStepSystem {

    ComponentMapper<GooseComponent> gooseMapper = ComponentMapper.getFor(GooseComponent.class);
    ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    ComponentMapper<InteractableComponent> interactableMapper = ComponentMapper.getFor(InteractableComponent.class);

    ComponentMapper<AnimationComponent> animMapper = ComponentMapper.getFor(AnimationComponent.class);

    private Entity target;
    private final MessageListener messageListener;

    public GooseSystem(FixedStepper fixedStepper, MessagePublisher publisher) {
        super(
                fixedStepper,
                Family.all(
                        GooseComponent.class,
                        TransformComponent.class,
                        PhysicsComponent.class,
                        InteractableComponent.class,
                        AnimationComponent.class
                ).get()
        );

        this.messageListener = new MessageListener(publisher);
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {

        handleMessages();

        GooseComponent goose = gooseMapper.get(entity);

        var transform = transformMapper.get(entity);
        var targetTransform = transformMapper.get(target);
        var physics = physicsMapper.get(entity);
        var interactable = interactableMapper.get(entity);

        Vector2 displacement = new Vector2(targetTransform.position).sub(transform.position);

        var data = new ProcessData(entity, deltaTime, goose, transform, physics, interactable, targetTransform, displacement);

        // AI behaviour
        switch (goose.state) {
            case WANDER -> processWander(data);
            case CHASE -> processChase(data);
            case RETREAT -> processRetreat(data);
        }

        @SuppressWarnings("unchecked")
        AnimationComponent<GooseAnimState> anim = (AnimationComponent<GooseAnimState>) animMapper.get(entity);

        Vector2 vel = physics.body.getLinearVelocity();
        GooseAnimState newAnimState = resolveAnimation(vel, goose.animState);

        // Reset animation when switching states
        if (anim.currentState != newAnimState) {
            anim.currentState = newAnimState;
            anim.elapsed = 0f;
        } else {
            anim.elapsed += deltaTime;
        }

        var animation = anim.animations.get(anim.currentState);
        if (animation != null) {
            anim.currentFrame = animation.getKeyFrame(anim.elapsed, true);
        }

        // Save for next frame
        goose.animState = newAnimState;
    }

    /**
     * Determines which animation the goose should play according to its velocity
     * @param vel The current velocity the goose is moving at
     * @param last The last animation: used as a default if the goose is not moving
     * @return
     */
    private GooseAnimState resolveAnimation(Vector2 vel, GooseAnimState last) {
        if (vel.len2() < 0.01f) {
            // Idle version of last direction
            return switch (last) {
                case WALK_UP, IDLE_UP -> GooseAnimState.IDLE_UP;
                case WALK_RIGHT, IDLE_RIGHT -> GooseAnimState.IDLE_RIGHT;
                case WALK_DOWN, IDLE_DOWN -> GooseAnimState.IDLE_DOWN;
                case WALK_LEFT, IDLE_LEFT -> GooseAnimState.IDLE_LEFT;
            };
        }

        // Movement → direction-based walk
        if (Math.abs(vel.x) > Math.abs(vel.y)) {
            return vel.x > 0 ? GooseAnimState.WALK_RIGHT : GooseAnimState.WALK_LEFT;
        } else {
            return vel.y > 0 ? GooseAnimState.WALK_UP : GooseAnimState.WALK_DOWN;
        }
    }

    /**
     * Consumes any new messages it received and reacts.
     * If the goose has bitten a player, it turns to flee.
     */
    private void handleMessages() {
        while (messageListener.hasNext()) {
            var message = messageListener.next();

            if (message.type == MessageType.GOOSE_BITE) {
                Entity entity = ((GooseBiteMessage) message).getInteractable();
                var gooseComponent = gooseMapper.get(entity);
                enterRetreat(gooseComponent);
            }
        }
    }

    /**
     * Perform fixed update processing for a goose in the wander state
     */
    private void processWander(ProcessData data) {
        if (magnitudeIsWithin(data.displacementFromTarget, data.goose.detectionRadius)) {
            enterChase(data.goose);
            return;
        }

        var goose = data.goose;

        if (goose.currentWanderWaypoint == null) {
            goose.currentWanderWaypoint = new Vector2();
            randomiseWaypoint(goose);
        }
        else if (magnitudeIsWithin(
                data.transform.position.cpy().sub(goose.currentWanderWaypoint),
                goose.wanderAcceptDistance
        )) {
            randomiseWaypoint(goose);
        }

        Vector2 direction = new Vector2(goose.currentWanderWaypoint)
                .sub(data.transform.position)
                .nor();

        data.physics.body.setLinearVelocity(direction.scl(goose.wanderSpeed));
    }

    /**
     * Perform fixed update processing for a goose in the chase state
     */
    private void processChase(ProcessData data) {
        if (!magnitudeIsWithin(data.displacementFromTarget, data.goose.forgetRadius)) {
            enterWander(data.goose);
            return;
        }

        Vector2 velocity = new Vector2(data.displacementFromTarget)
                .nor()
                .scl(data.goose.chaseSpeed);

        data.physics.body.setLinearVelocity(velocity);
    }

    /**
     * Perform fixed update processing for a goose in the retreat state
     */
    private void processRetreat(ProcessData data) {

        data.goose.retreatTimeElapsed += data.deltaTime;

        if (data.goose.retreatTimeElapsed > data.goose.retreatTime) {
            data.interactable.interactionEnabled = true;
            enterChase(data.goose);
        }

        Vector2 velocity = new Vector2(data.displacementFromTarget)
                .nor()
                .scl(-data.goose.retreatSpeed);

        data.physics.body.setLinearVelocity(velocity);
    }

    /**
     * Switches the goose provided to the wander state from whichever state it is currently in
     */
    private void enterWander(GooseComponent goose) {
        goose.state = GooseComponent.GooseState.WANDER;
    }

    /**
     * Switches the goose provided to the chase state from whichever state it is currently in
     */
    private void enterChase(GooseComponent goose) {
        goose.state = GooseComponent.GooseState.CHASE;
    }

    /**
     * Switches the goose provided to the retreat state from whichever state it is currently in
     */
    private void enterRetreat(GooseComponent goose) {
        goose.retreatTimeElapsed = 0f;
        goose.state = GooseComponent.GooseState.RETREAT;
    }

    private boolean magnitudeIsWithin(Vector2 v, float limit) {
        return v.len2() <= limit * limit;
    }

    /**
     * Picks a new waypoint for this goose to walk towards
     * @param goose The goose in need of a waypoint
     */
    private void randomiseWaypoint(GooseComponent goose) {
        float dist = (float) Math.sqrt(MathUtils.random()) * goose.wanderRadius;

        goose.currentWanderWaypoint
                .setToRandomDirection()
                .scl(dist)
                .add(goose.homePosition);
    }

    private record ProcessData(
            Entity entity,
            float deltaTime,
            GooseComponent goose,
            TransformComponent transform,
            PhysicsComponent physics,
            InteractableComponent interactable,
            TransformComponent target,
            Vector2 displacementFromTarget
    ) {}
}
