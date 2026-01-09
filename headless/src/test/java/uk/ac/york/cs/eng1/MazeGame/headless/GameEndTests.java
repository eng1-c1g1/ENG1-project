package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.messages.MessageType;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.TimerSystem;
import io.github.maze11.systems.gameState.GameStateSystem;

public class GameEndTests extends AbstractHeadlessGdxTest {

    @Test
    public void exitInteractionTest() {
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        Entity testExit = testEntMaker.makeExit(0, 0);
        
        TimerSystem testTimerSystem = new TimerSystem(testPublisher);
        testEngine.addSystem(testTimerSystem);

        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        GameStateSystem testGSS = testEngine.getSystem(GameStateSystem.class);

        testPublisher.publish(new CollisionMessage(testPlayer, testExit));
        testInteractSystem.update(0);
        
        testPublisher.listeners.get(0).next();  // Clearing  queue
        assertEquals(MessageType.EXIT_MAZE, testPublisher.listeners.get(0).next().type,
    "No Exit message sent");

    }

    @Test
    public void timerExpiredTest() {
        Entity testTimer = testEntMaker.makeTimer(2f);
        TimerSystem testTimerSystem = new TimerSystem(testPublisher);
        testEngine.addSystem((EntitySystem)testTimerSystem);

        simulate(2.01f);    // Simulating for just over the duration of the timer

        assertEquals(MessageType.TIMER_EXPIRED, testPublisher.listeners.get(4).next().type,
        "Timer should have sent Timer Expired message");

    }
}
