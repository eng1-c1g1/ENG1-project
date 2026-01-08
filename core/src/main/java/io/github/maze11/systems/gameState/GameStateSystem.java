package io.github.maze11.systems.gameState;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;

import io.github.maze11.MazeGame;
import io.github.maze11.components.TimerComponent;
import io.github.maze11.messages.Message;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.screens.GameOverScreen;
import io.github.maze11.screens.WinScreen;
import io.github.maze11.systems.PauseSystem;

/**
 * system responsive for managing game state transitions between screens.
 * listens for game events (timer's expiry, win condition.) and tells MazeGame to switches screen when needed.
 * When a game-ending condition occurs, it instructs MazeGame to switch to appropriate screen.
 * Keeps track of any events the numbers of which need to be recorded.
 */
public class GameStateSystem extends EntitySystem {
    private final MessageListener messageListener;
    private final MazeGame game;
    public final EventCounter eventCounter;
    private final Engine engine;

    public GameStateSystem(MessagePublisher messagePublisher, MazeGame game, Engine engine) {
        this.messageListener = new MessageListener(messagePublisher);
        this.game = game;
        this.eventCounter = new EventCounter();
        this.engine = engine;
    }

      @Override
      public void update(float deltaTime) {
        
        // CHANGED: Don't update game state whilst paused 
        //Check if game is paused
        if (!PauseSystem.gamePaused) {
            // process all messages received since last update
            while (messageListener.hasNext()) {
                Message msg = messageListener.next();
                eventCounter.receiveMessage(msg.type);

                switch (msg.type) {
                    // handle timer expiration
                    case TIMER_EXPIRED -> {
                        System.out.println("Timer Expired! Switching to Game Over Screen...");
                        int totalScore = eventCounter.makeScoreCard(true, 0).totalScore();
                        game.switchScreen(new GameOverScreen(game, totalScore));
                    }
                    case EXIT_MAZE -> {
                        System.out.println("Maze exit reached! Switching to Win Screen...");
                        var scoreCard = eventCounter.makeScoreCard(true, (int) getSecondsRemaining());
                        game.switchScreen(new WinScreen(game, scoreCard));
                    }
                    default -> {}
                }
            }
        }
        
      }
    private float getSecondsRemaining(){
          var timers = engine.getEntitiesFor(Family.all(TimerComponent.class).get());
          if (timers.size() != 1){
              throw new RuntimeException("There must be exactly one timer in the scene. Found " +  timers.size());
          }
          return timers.get(0).getComponent(TimerComponent.class).timeRemaining;
    }

}
