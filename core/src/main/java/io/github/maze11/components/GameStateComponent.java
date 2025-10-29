package io.github.maze11.components;

import com.badlogic.ashley.core.Component;

/**
 * Component that tracks the current state of the game.
 * Used to determine what UI to show and what systems should be active.
 */
public class GameStateComponent implements Component {
    /**
     * Possible states the game can be in.
     */
    public enum State {
        MENU,    // Main menu screen
        PLAYING, // Active gameplay
        WIN,     // Player has won
        LOSE     // Player has lost (timer expired or other failure)
    }
    
    /**
     * The current state of the game.
     * Starts in PLAYING state (will be MENU when issue #43 is complete).
     */
    public State currentState = State.PLAYING;
}