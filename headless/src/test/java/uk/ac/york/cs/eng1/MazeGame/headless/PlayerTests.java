package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import io.github.maze11.components.TransformComponent;
import io.github.maze11.systems.PlayerSystem;

public class PlayerTests extends AbstractHeadlessGdxTest{
    @Test
    public void playerCollisionTest() {
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        Entity testWall = testEntMaker.makeWall(0, 5, 4, 4);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);

        testPS.setConstDirection(new Vector2(0, 2f));

        simulate(2f);   // Simulate for 2 seconds
                                // Player will have hit wall by this point
        // Saving current position of player
        Vector2 playerPos = testPlayer.getComponent(TransformComponent.class).position.cpy();
        Float prevXPos = playerPos.x;
        Float prevYPos = playerPos.y;

        simulate(0.5f); // Simulating for half a second longer

        // Getting new position of player
        playerPos = testPlayer.getComponent(TransformComponent.class).position;
        
        assertEquals(prevXPos, playerPos.x,
            "Player should not have moved horizontally"
        );

        assertEquals(prevYPos, playerPos.y,
        "Player should not have moved vertically");
        
    }
}
