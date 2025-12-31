package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.components.PlayerComponent;
import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;

public class PuddleTests extends AbstractHeadlessGdxTest {

    @Test
    public void puddleInteractTest() {
        Entity testPuddle = testEntMaker.makePuddle(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0f, 0f);

        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        PlayerComponent testPC = testPlayer.getComponent(PlayerComponent.class);

         // Testing whether the puddle sends a message when activated.
        testPublisher.publish(new CollisionMessage(testPlayer, testPuddle));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testPS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision

        assertEquals(1, testPC.speedBonuses.size(),"Puddle should apply one boost");
        assertEquals(-5f, testPC.speedBonuses.get(0).amount, 0.0001f);        
        assertEquals(5f, testPC.speedBonuses.get(0).timeRemaining, 0.0001f);    

    }
    
}
