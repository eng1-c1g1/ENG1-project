package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.components.*;
import io.github.maze11.messages.GooseBiteMessage;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.MessageType;
import io.github.maze11.systemTypes.FixedStepper;
import io.github.maze11.systemTypes.IteratingFixedStepSystem;

public class GooseSystem extends IteratingFixedStepSystem {
    ComponentMapper<GooseComponent> gooseMapper =  ComponentMapper.getFor(GooseComponent.class);
    ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    ComponentMapper<InteractableComponent> interactableMapper = ComponentMapper.getFor(InteractableComponent.class);

    private Entity target;
    private final MessageListener messageListener;

    public GooseSystem(FixedStepper fixedStepper, MessagePublisher publisher) {
        super(fixedStepper, Family.all(GooseComponent.class, TransformComponent.class, PhysicsComponent.class, InteractableComponent.class).get());
        this.messageListener = new MessageListener(publisher);
    }

    public void setTarget(Entity target){
        this.target = target;
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {

        handleMessages();

        // Find useful references and values here, to avoid doing this in every state
        var transform = transformMapper.get(entity);
        var targetTransform = transformMapper.get(target);
        var interactable = interactableMapper.get(entity);
        // Displacement vector to get to the target
        Vector2 displacement = new Vector2(targetTransform.position).sub(transform.position);
        var data = new ProcessData(entity, deltaTime, gooseMapper.get(entity), transform,
            physicsMapper.get(entity), interactable, targetTransform, displacement);

        // Determine what to do based on the state
        // Behaviour is handled separately for separate states for clarity and maintainability
        switch (gooseMapper.get(entity).state) {
            case WANDER:
                processWander(data);
                break;
            case CHASE:
                processChase(data);
                break;
            case RETREAT:
                processRetreat(data);
                break;
            default:
                System.out.println("Unknown state: " + gooseMapper.get(entity).state);
                break;

        }
    }

    private void handleMessages(){
        while (messageListener.hasNext()){
            var message = messageListener.next();
            if (message.type == MessageType.GOOSE_BITE){
                //Should be safe to cast since only messages of this type should be called GOOSE_BITE
                // Throwing an exception on invalid cast is acceptable for this reason
                Entity entity = ((GooseBiteMessage)message).getGooseEntity();
                var gooseComponent = gooseMapper.get(entity);
                // After successfully biting the player, the goose should flee for a short time
                enterRetreat(gooseComponent);
            }
        }
    }

    private void processWander(ProcessData data) {
        // If it is within range, switch to goose state
        if (magnitudeIsWithin(data.displacementFromTarget, data.goose.detectionRadius)){
            enterChase(data.goose);
            return;
        }

        //If the goose is too close to the waypoint or has not decided a waypoint yet, chooses a new waypoint
        var goose = data.goose;

        //If the goose was just created, it will not have a waypoint, create one
        if (goose.currentWanderWaypoint == null){
            goose.currentWanderWaypoint = new Vector2();
            randomiseWaypoint(goose);
        }
        // If the goose is at a waypoint, find a new one
        else if (magnitudeIsWithin(data.transform.position.cpy().sub(goose.currentWanderWaypoint), goose.wanderAcceptDistance)){
            randomiseWaypoint(goose);
        }

        //Move towards the current waypoint
        Vector2 directionToWaypoint = new Vector2(goose.currentWanderWaypoint).sub(data.transform.position).nor();
        data.physics.body.setLinearVelocity(directionToWaypoint.scl(data.goose.wanderSpeed));

    }

    private void processChase(ProcessData data) {
        // If it is too far away, switches to idle state
        if (!magnitudeIsWithin(data.displacementFromTarget, data.goose.forgetRadius)){
            enterWander(data.goose);
            return;
        }

        // Calculate velocity vector from speed and direction
        var velocity = new Vector2(data.displacementFromTarget).nor().scl(data.goose.chaseSpeed);
        data.physics.body.setLinearVelocity(velocity);
    }

    private void processRetreat(ProcessData data) {
        data.goose.retreatTimeElapsed += data.deltaTime;

        // When the time elapses, the goose resumes aggression
        if (data.goose.retreatTimeElapsed > data.goose().retreatTime){
            // Re-enable interactions so that the goose can bite the player again
            data.interactable.interactionEnabled = true;
            enterChase(data.goose);
        }

        // Calculate velocity vector to move away from the player based on speed and direction
        var velocity = new Vector2(data.displacementFromTarget).nor().scl(-data.goose.retreatSpeed);
        data.physics.body.setLinearVelocity(velocity);

        // Note: the goose cannot bite the player in the retreat phase, since interactions are disabled when a bite happens
        // The bite behaviour is re-enabled when the goose exits this state
    }

    // These 3 methods ensure that any additional transition logic is carried out

    private void enterWander(GooseComponent gooseComponent){
        gooseComponent.state = GooseState.WANDER;
    }
    private void enterChase(GooseComponent gooseComponent){
        gooseComponent.state = GooseState.CHASE;
    }
    private void enterRetreat(GooseComponent gooseComponent){
        gooseComponent.retreatTimeElapsed = 0f;
        gooseComponent.state = GooseState.RETREAT;
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

    /**
     * Sets the waypoint of this goose to a random position within the wander radius from the home position
     */
    private void randomiseWaypoint(GooseComponent goose){
        // Calculate distance from the home position to place the goose
        // use square root to avoid increased probability density at the centre of the circle
        float distanceFromHome = (float)Math.sqrt(MathUtils.random()) * goose.wanderRadius;
        // Picks a random direction, scales by distance to get offset from home
        goose.currentWanderWaypoint.setToRandomDirection().scl(distanceFromHome);
        // Add home to get final position
        goose.currentWanderWaypoint.add(goose.homePosition);
    }

    private record ProcessData(Entity entity, float deltaTime, GooseComponent goose, TransformComponent transform,
                               PhysicsComponent physics, InteractableComponent interactable, TransformComponent target,
                               Vector2 displacementFromTarget) {}
}
