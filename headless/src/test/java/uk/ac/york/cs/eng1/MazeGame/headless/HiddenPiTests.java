package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.messages.*;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;

public class HiddenPiTests extends AbstractHeadlessGdxTest{

    
    @Test
    public void activatePiTests() {
        Entity testPi = testEntMaker.makePi(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        int prevNumPis = PiCollectMessage.numPis;

        // Testing whether the pi sends a message when activated.
        testPublisher.publish(new CollisionMessage(testPlayer, testPi));
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testPS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision

        assertEquals(prevNumPis + 1, PiCollectMessage.numPis);
        // Testing whether the same pi can be activated multiple times

    }
}
