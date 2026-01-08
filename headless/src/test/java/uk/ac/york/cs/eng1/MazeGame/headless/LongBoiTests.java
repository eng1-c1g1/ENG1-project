package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.messages.Message;
import io.github.maze11.messages.MessageType;
import io.github.maze11.systems.InteractableSystem;

public class LongBoiTests extends AbstractHeadlessGdxTest {

    @Test
    public void LongBoiInteractTest() {
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        Entity testLongBoi = testEntMaker.makeLongBoi(0, 0);
        Entity testPlayer = testEntMaker.makePlayer(0, 0);

        testPublisher.publish(new CollisionMessage(testLongBoi, testPlayer));
        testInteractSystem.update(0);

        // Checking the correct message type is sent
        testPublisher.listeners.get(0).next();  // Removing the collision message from the queue
        Message msg = testPublisher.listeners.get(0).next();
        assertEquals(MessageType.LONGBOI_INTERACT, msg.type,
            "Message Type should be LongBoiMessage"
        );

        // Checking to make sure you can't activate the statue twice
        testPublisher.listeners.get(0).next(); // Clearing queue
        testPublisher.publish(new CollisionMessage(testLongBoi, testPlayer));
        testInteractSystem.update(0);

        // Checking that another message isn't sent
        testPublisher.listeners.get(0).next();  // Removing the collision message from the queue
        assertFalse(testPublisher.listeners.get(0).hasNext(),
    "There should not be any additional messages in the queue");  // The collision message should not have been processed
    }
}
