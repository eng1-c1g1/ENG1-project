package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.maze11.components.TimerComponent;
import io.github.maze11.messages.*;

/**
 * Updates countdown timers.
 * Sends a message when a timer expires.
 */
public class TimerSystem extends IteratingSystem {
    private final ComponentMapper<TimerComponent> timerM = ComponentMapper.getFor(TimerComponent.class);
    private final MessagePublisher messagePublisher;
    private final MessageListener messageListener;

    // constructor to define the family of entities this system will process
    public TimerSystem(MessagePublisher messagePublisher) {
        super(Family.all(TimerComponent.class).get());
        this.messageListener = new MessageListener(messagePublisher);
        this.messagePublisher = messagePublisher;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TimerComponent timer = timerM.get(entity); // get the timer component for the entity

        // CHANGED: Made it so no entity updates occur when paused.
        // if the timer is not running or has expired, or the game is paused do nothing
        if (!timer.isRunning || timer.hasExpired || PauseSystem.gamePaused) {
            return;
        }

        while (messageListener.hasNext()) {
            var message = messageListener.next();

            if (message.type == MessageType.TIME_LOST) {
                timer.timeRemaining -= 50f;
            }
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
