package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.ashley.core.EntitySystem;
import io.github.maze11.messages.ToastMessage;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.TimerSystem;
import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.components.TimerComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.systems.InteractableSystem;

public class TimeLossTests extends AbstractHeadlessGdxTest{

    @Test
    public void yapperInteractTest() {
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        PlayerSystem testPlayerSystem = testEngine.getSystem(PlayerSystem.class);
        Entity testTimer = testEntMaker.makeTimer(100f);
        Entity testTimeLoss = testEntMaker.makeTimeLoss(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0f, 0f);

        TimerSystem testTimerSystem = new TimerSystem(testPublisher);

        // Adding some systems that aren't added by default by the tests.
        testEngine.addSystem((EntitySystem)testTimerSystem);

        TimerComponent testTC = testTimer.getComponent(TimerComponent.class);
        PlayerComponent testPC = testPlayer.getComponent(PlayerComponent.class);

        // Publishing a message to all systems to test whether it is processed when their update method is called
        testPublisher.publish(new ToastMessage("This is a test", 1f));

        assertEquals(100f, testTC.timeRemaining, "Time Remaining == 100 seconds");

        testPublisher.publish(new CollisionMessage(testPlayer, testTimeLoss));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testPlayerSystem.fixedUpdate(0f);  // Actually processes the event that occurred from the collision
        testTimerSystem.update(0f);

        assertEquals(50f, testTC.timeRemaining, "Time Remaining equals 100 seconds - 50 seconds");

    }
}
