package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.components.BullyComponent;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.systems.BullySystem;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;

public class BullyTests extends AbstractHeadlessGdxTest {

    @Test
    public void bullyBlocksWithoutBribeTest() {

        Entity testBully = testEntMaker.makeBully(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0f, 0f);
        
        BullySystem testBS = testEngine.getSystem(BullySystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        PlayerComponent testPC = testPlayer.getComponent(PlayerComponent.class);
        BullyComponent testBC = testBully.getComponent(BullyComponent.class);
        InteractableComponent testIC = testBully.getComponent(InteractableComponent.class);

        testPC.hasBribe = false;

         // Testing whether the bully sends a message when collided with no bribe
        testPublisher.publish(new CollisionMessage(testPlayer, testBully));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testBS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision

        assertEquals(BullyComponent.BullyState.BLOCKING, testBC.state, "Without a bribe, bully should stay blocking");

        assertTrue(testIC.interactionEnabled, "Bully interaction should remain enabled when no bribe is given");

    }

    @Test
    public void bullyMovesAfterBribeTest() {

        Entity testBully = testEntMaker.makeBully(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0f, 0f);
        Entity testBribe = testEntMaker.makeBribe(0f, 0f);

        BullySystem testBS = testEngine.getSystem(BullySystem.class);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        PlayerComponent testPC = testPlayer.getComponent(PlayerComponent.class);
        BullyComponent testBC = testBully.getComponent(BullyComponent.class);
        InteractableComponent testIC = testBully.getComponent(InteractableComponent.class);

         // Testing whether the bribe sends a message when collected
        testPublisher.publish(new CollisionMessage(testPlayer, testBribe));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testPS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision

        // Testing whether the bully sends a message when collided with a bribe
        testPublisher.publish(new CollisionMessage(testPlayer, testBully));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testBS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision

        assertFalse(testPC.hasBribe, "Bully interaction should consume the bribe");

        assertEquals(BullyComponent.BullyState.MOVE, testBC.state, "Bully should enter Move state after receiving bribe");

        assertFalse(testIC.interactionEnabled, "Bully interaction should be disabled while the bully moves away");

    }

}
