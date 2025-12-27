package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

import io.github.maze11.messages.ToastMessage;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PauseSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.TimerSystem;
import io.github.maze11.systems.gameState.GameStateSystem;

public class PauseSystemTests extends AbstractHeadlessGdxTest{

    @Test
    public void activatePauseTests() {

        // Testing whether the game can pause and unpause correctly
        PauseSystem.pauseGame();
        assertTrue(PauseSystem.gamePaused, "gamePaused boolean should be true");
        PauseSystem.pauseGame();
        assertTrue(PauseSystem.gamePaused, "gamePaused boolean should remain true");
        PauseSystem.unpauseGame();
        assertFalse(PauseSystem.gamePaused, "gamePaused boolean should be false");
        PauseSystem.unpauseGame();
        assertFalse(PauseSystem.gamePaused, "gamePaused boolean should remain false");
    }

    @Test
    public void noUpdatesWhilePausedTests() {

        // Loading systems into variables
        GameStateSystem testGameStateSystem = testEngine.getSystem(GameStateSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        PlayerSystem testPlayerSystem = testEngine.getSystem(PlayerSystem.class);
        Entity testTimer = testEntMaker.makeTimer(1f);
        
        TimerSystem testTimerSystem = new TimerSystem(testPublisher);

        // Adding some systems that aren't added by default by the tests.
        testEngine.addSystem((EntitySystem)testTimerSystem);
        
        // Publishing a message to all systems to test whether it is processed when their update method is called
        testPublisher.publish(new ToastMessage("This is a test", 1f));

        PauseSystem.pauseGame();

        // Update each system using their specific update function
        testGameStateSystem.update(0);
        testInteractSystem.update(0);
        testPlayerSystem.fixedUpdate(0);
        testTimerSystem.processEntity(testTimer,0);

        // Check if any of the systems have processed their message queue
        // ToastMessages are deleted once processed, so if any listener doesn't have a message in its queue, then it will have updated
        assertTrue(testPublisher.listeners.get(0).hasNext(), "testListener should have a toast message");   // Control listener checks to see if publish was successful
        assertTrue(testPublisher.listeners.get(1).hasNext(), "GameStateSystem should not have updated queue");
        assertTrue(testPublisher.listeners.get(2).hasNext(), "InteractableSystem should not have updated queue");
        assertTrue(testPublisher.listeners.get(3).hasNext(), "PlayerSystem should not have updated queue");
        assertTrue(testPublisher.listeners.get(4).hasNext(), "TimerSystem should not have updated queue");

        // Check that messages can start being processed again after unpausing
        PauseSystem.unpauseGame();

        // Triggering updates again
        testGameStateSystem.update(0);
        testInteractSystem.update(0);
        testPlayerSystem.fixedUpdate(0);
        testTimerSystem.update(0);

        assertFalse(testPublisher.listeners.get(1).hasNext(), "GameStateSystem should have updated queue");
        assertFalse(testPublisher.listeners.get(2).hasNext(), "InteractableSystem should have updated queue");
        assertFalse(testPublisher.listeners.get(3).hasNext(), "PlayerSystem should have updated queue");
        assertFalse(testPublisher.listeners.get(4).hasNext(), "TimerSystem should have updated queue");
    }
}
