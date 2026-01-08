package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.gameState.EventCounter;
import io.github.maze11.systems.gameState.GameStateSystem;

public class CheckInTests extends AbstractHeadlessGdxTest {

    @Test
    public void checkInCodeInteractTest() throws Exception {
        Entity testCheckIn = testEntMaker.makeCheckInCode(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0f, 0f);

        GameStateSystem testGameStateSystem = testEngine.getSystem(GameStateSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);


         // Testing whether the check-in code sends a message when activated.
        testPublisher.publish(new CollisionMessage(testPlayer, testCheckIn));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f); 
        testGameStateSystem.update(0f); 
        
        EventCounter eventCounter = testGameStateSystem.eventCounter;

        assertEquals(20, eventCounter.makeScoreCard(false, 0).totalScore());
    }
    
    
}
