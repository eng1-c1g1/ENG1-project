package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.math.Vector2;
import io.github.maze11.components.TransformComponent;
import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;

import io.github.maze11.components.PlayerComponent;
import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;

public class TeleporterTests extends AbstractHeadlessGdxTest{
    @Test
    public void testTeleporter(){
        Entity testTeleporter = testEntMaker.makeTeleportation(0f, 0f, new Vector2(1f,1f));
        Entity testPlayer = testEntMaker.makePlayer(0f, 0f);

        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        PlayerComponent testPC = testPlayer.getComponent(PlayerComponent.class);

        // Testing whether the puddle sends a message when activated.
        testPublisher.publish(new CollisionMessage(testPlayer, testTeleporter));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testPS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision

        assertEquals(new Vector2(1f,1f), testPlayer.getComponent(TransformComponent.class).position, "Position should be set");

    }
}
