package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import io.github.maze11.messages.*;
import io.github.maze11.MazeGame;
import io.github.maze11.GameOverScreen;
import io.github.maze11.WinScreen;

/**
 * systemm responsive for managing game state transitions betwween screens.
 * listens for game events (timer's expiry, win condition..) and tells MazeGame to switchs screen when needed
 * 
 * this system runs in levelScreen's ECS and monitors messages from other systems/
 * when a game-ending condition occurs, it instructs MazeGame to switch to appropriate screen.
 */
public class GameStateSystem extends EntitySystem {
    private final MessageListener messageListener;
    private final MazeGame game;

    // creates a new game state system.
    public GameStateSystem(MessagePublisher messagePublisher, MazeGame game) {
        this.messageListener = new MessageListener(messagePublisher);
        this.game = game;
    }
    /**
     * updates systems by processsing incomming messages.
     * called every frame by the engine. 
     * 
     * checks for:
     * - TIMER_EXPIRED: 5-min timer ran out -> switch to gameOverScreen
     * - WIN: player reached exit -> switch to WinScreen (//TODO: implement win condition handling)
     */
      @Override
      public void update(float deltaTime) {
        // process all messages received since last update
        while (messageListener.hasNext()) {
            Message msg = messageListener.next();

            // handle timer expiration 
            if (msg.type == MessageType.TIMER_EXPIRED) {
                System.out.println("Timer Expired! Switching to Game Over Screen...");
                // switch to GameOverScreen with current score 
                // TODO: Replace 0 with actual score when scoring system is implemented
                game.setScreen(new GameOverScreen(game, 0));
            }

            //TODO: Add win condition handling

        }
      }
    
}
