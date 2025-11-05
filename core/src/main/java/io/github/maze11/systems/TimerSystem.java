package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.maze11.components.TimerComponent;
import io.github.maze11.messages.Message;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.MessageType;

/**
 * updates countdown timers and handles expiration
 */
public class TimerSystem extends IteratingSystem {
    private final ComponentMapper<TimerComponent> timerM = ComponentMapper.getFor(TimerComponent.class);
    private final MessagePublisher messagePublisher;
    // constructor to define the family of entities this system will process
    public TimerSystem(MessagePublisher messagePublisher) {
        super(Family.all(TimerComponent.class).get());
        this.messagePublisher = messagePublisher;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TimerComponent timer = timerM.get(entity); // get the timer component for the entity

        // if the timer is not running or has expired, do nothing
        if (!timer.isRunning || timer.hasExpired) {
            return;
        }

        // decrease the remaining time by the delta time (time since last frame)
        timer.timeRemaining -= deltaTime;

        // check if timer has expired
        if (timer.timeRemaining <= 0) {
            timer.timeRemaining = 0; // set to 0
            timer.hasExpired = true; // mark as expired

            // publish timer expired message for other systems to react to
            messagePublisher.publish(new Message(MessageType.TIMER_EXPIRED));
            System.out.println("Timer expired! Publishing TimerExpiredMessage..."); // log expiration
        }
    }
}
