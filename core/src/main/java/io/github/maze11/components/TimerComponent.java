package io.github.maze11.components;

import com.badlogic.ashley.core.Component;


/**
 * Component for countdown timer functionality
 */
public class TimerComponent implements Component{
    public float timeRemaining; //in seconds
    public float totalTime; // total durations
    public boolean isRunning = true;
    public boolean hasExpired = false;

    // No-argument constructor required by Ashley's PooledEngine
    public TimerComponent() {
        this.timeRemaining = 0f;
        this.totalTime = 0f;
    }

}
