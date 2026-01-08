package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        testPublisher.publish(new CollisionMessage(testPlayer, testPi));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testPS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision

        assertEquals(prevNumPis + 1, PiCollectMessage.numPis,
            "Number of active Pi's hasn't been incremented"
        );

        // Making sure the same pi can't be activated multiple times
        testPublisher.publish(new CollisionMessage(testPi, testPlayer));
        testInteractSystem.update(0f);
        testPS.fixedUpdate(0f);

        prevNumPis++;
        assertEquals(prevNumPis, PiCollectMessage.numPis,
            "A Pi shouldn't activate more than once"
        );
    }

    @Test
    public void cowsayActivationTests() {
        Entity testPi = testEntMaker.makePi(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        PiCollectMessage.numPis = 2;    // Cowsay should trigger once 3rd pi activated

        testPublisher.publish(new CollisionMessage(testPi, testPlayer));
        testInteractSystem.update(0f);
        testPS.fixedUpdate(0f);

        assertTrue(PiActivatedMessage.cowsayActivated, "Cowsay should have activated");

        // Ensuring additional triggers over 3 don't retrigger the cowsay.
        Entity testPi2 = testEntMaker.makePi(0f, 0f);
        testPublisher.publish(new CollisionMessage(testPi2, testPlayer));
        
        testInteractSystem.update(0f);
        testPS.fixedUpdate(0f);

        assertEquals(4, PiCollectMessage.numPis);   // Checking the new Pi has actually been triggered
        assertFalse(PiActivatedMessage.cowsayActivated, "Cowsay shouldn't have triggered again");   // Checking cowsay hasn't been triggered
    }
}
