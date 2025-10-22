package io.github.maze11.components;

import com.badlogic.ashley.core.Component;


/**
 * Component for countdown timer funcitonality 
 */
public class TimerComponent implements Component{
    public float timeRemaining; //in seconds
    public float totalTime; // total durations
    public boolean isRunning = true; 
    public boolean hasExpired = false;

    public TimerComponent(float durationInSeconds) {
        this.timeRemaining = durationInSeconds;
        this.totalTime = durationInSeconds;
    }
    
}
