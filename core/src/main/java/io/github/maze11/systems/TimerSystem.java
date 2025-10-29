package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.maze11.components.TimerComponent;

/**
 * updates countdown timers and handles expiration
 *
 */
public class TimerSystem extends IteratingSystem {
    private final ComponentMapper<TimerComponent> timerM = ComponentMapper.getFor(TimerComponent.class);
    // constructor to define the family of entities this system will process
    public TimerSystem() {
        super(Family.all(TimerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TimerComponent timer = timerM.get(entity); // get the timer component for the entity

        // if the timer is not running or has expured, do nothing
        if (!timer.isRunning || timer.hasExpired) {
            return;
        }

        // decrease the remaining time by the delta time (time since last frame)
        timer.timeRemaining -= deltaTime;

        // check if timer has expired
        if (timer.timeRemaining <= 0) {
            timer.timeRemaining = 0; // set to 0
            timer.hasExpired = true; // mark as expired
            System.out.println("Timer expired!"); // log expiration
        }
    }
}
