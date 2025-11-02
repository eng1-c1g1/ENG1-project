package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import io.github.maze11.messages.*;
import io.github.maze11.MazeGame;
import io.github.maze11.GameOverScreen;
import io.github.maze11.WinScreen;

/**
 * systemm responsive for managing game state transitions.
 * listens for game events and tells MazeGame to switchs screen when needed
 */
public class GameStateSystem extends EntitySystem {
    private final MessageListener messageListener;
    private final MazeGame game;

    // creates a new game state system.
    public GameStateSystem(MessagePublisher messagePublisher, MazeGame game) {
        this.messageListener = new MessageListener(messagePublisher);
        this.game = game;
    }

      @Override
      public void update(float deltaTime) {
        while (messageListener.hasNext()) {
            Message msg = messageListener.next();

            // handle timer expiration 
            if (msg.type == MessageType.TIMER_EXPIRED) {
                System.out.println("Timer Expired! Switching to Game Over Screen...");
                // TODO: Replace 0 with actual score when scoring system is implemented
                game.setScreen(new GameOverScreen(game, 0));
            }

            //TODO: Add win condition handling

        }
      }
    
}
