package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import io.github.maze11.components.GameStateComponent;
import io.github.maze11.messages.*;

/**
 * systemm responsive for managing game state transitions.
 * listens for game events (like timer expiry) and updates the game state accordingly 
 * 
 */
public class GameStateSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private final MessageListener messageListener;

    // creates a new game state system.
    public GameStateSystem(MessagePublisher messagePublisher) {
        this.messageListener = new MessageListener(messagePublisher);
    }

    /**
     * called when this system is added to engine
     * sets up familty of entities this system processes (entities with GameStateCOmponent)
     */

     @Override
     public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(GameStateComponent.class).get());
     }

     /**
      * updates the game state based on received messages.
      called every frame by engine
      */
      @Override
      public void update(float deltaTime) {
        // process al lmessages recived since last update
        while (messageListener.hasNext()) {
            Message msg = messageListener.next();

            // handle timer expiration 
            if (msg.type == MessageType.TIMER_EXPIRED) {
                //change state to LOSE when timer expires
                for (Entity entity : entities) {
                    GameStateComponent state = entity.getComponent(GameStateComponent.class);
                    // only tranasition to LOSE if currently playing
                    if (state.currentState == GameStateComponent.State.PLAYING) {
                        state.currentState = GameStateComponent.State.LOSE;
                        System.out.println("Game Over! Timer Expired. State changed to LOSE.");
                    }
                }
            }
        }
      }
    
}
