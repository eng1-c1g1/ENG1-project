package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.components.GooseComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.components.GooseComponent.GooseState;
import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.messages.Message;
import io.github.maze11.messages.MessageType;
import io.github.maze11.systems.GooseSystem;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;

public class AnkhTests extends AbstractHeadlessGdxTest {

    @Test
    public void ankhActivationTest() {
        Entity testAnkh = testEntMaker.makeAnkh(0, 0);
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        testPublisher.publish(new CollisionMessage(testAnkh, testPlayer));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0);

        // Checking that the ankh sends a message
        testPublisher.listeners.get(0).next();  // Removing the collision message from the queue
        Message msg = testPublisher.listeners.get(0).next();
        assertEquals(MessageType.ANKH_INTERACT, msg.type,
            "Message Type should be Ankh Interact Message"
        );

        assertTrue(testPlayer.getComponent(PlayerComponent.class).isInvulnerable,
    "Player should be Invulnerable");

    }

    @Test
    public void ankhEffectivenessTest() {
        Entity testGoose = testEntMaker.makeGoose(0, 0);
        GooseSystem testGS = new GooseSystem(testStepper, testPublisher);
        testEngine.addSystem(testGS);

        Entity testAnkh = testEntMaker.makeAnkh(0, 0);
        Entity testPlayer = testEntMaker.makePlayer(3, 3); // Create player in range of goose
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        testPublisher.publish(new CollisionMessage(testAnkh, testPlayer));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0);  // Allow ankh to trigger

        TransformComponent playerComp = testPlayer.getComponent(TransformComponent.class);

        float playerX = playerComp.position.x;
        float playerY = playerComp.position.y;
        // Setting goose to target player
        testGS.setTarget(testPlayer);

        simulate(1.5f);

        playerComp = testPlayer.getComponent(TransformComponent.class);

        // Checking the goose has collided with the player
        assertEquals(GooseState.RETREAT, testGoose.getComponent(GooseComponent.class).state,
    "Goose should have collided with the player"
        );
        // Player should not be knocked back by goose
        // Testing X-component
        assertEquals(playerX, playerComp.position.x, 0.2f,
            "Player's X-coordinate should not be changed by the goose"
        );
        // Testing Y-component
        assertEquals(playerY, playerComp.position.y, 0.2f,
            "Player's Y-coordinate should not be changed by the goose"
        );

    }
}
