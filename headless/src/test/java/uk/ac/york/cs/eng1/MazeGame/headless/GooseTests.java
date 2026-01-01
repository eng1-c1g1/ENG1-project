package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.utils.BaseAnimationController.Transform;
import com.badlogic.gdx.math.Vector2;

import io.github.maze11.components.GooseComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.systems.GooseSystem;

public class GooseTests extends AbstractHeadlessGdxTest{


    // Checking that the goose starts in WANDER state
    @Test
    public void intitialStateTest() {
        GooseSystem testGS = new GooseSystem(testStepper, testPublisher);
        testEngine.addSystem(testGS);

        Entity testGoose = testEntMaker.makeGoose(0, 0);
        GooseComponent gooseComponent = testGoose.getComponent(GooseComponent.class);
        assertEquals(GooseComponent.GooseState.WANDER, gooseComponent.state,
            "Goose should start in WANDER state"
        );

    }

    // Checking whether the goose will chase and forget the player correctly
    @Test
    public void chaseTests() {
        GooseSystem testGS = new GooseSystem(testStepper, testPublisher);
        testEngine.addSystem(testGS);

        Entity testGoose = testEntMaker.makeGoose(0, 0);
        Entity testPlayer = testEntMaker.makePlayer(8, 8);  // Placing player outside of goose detection radius
        GooseComponent gooseComponent = testGoose.getComponent(GooseComponent.class);

        // Setting the player as the target, then simulating the game for 1 second
        testGS.setTarget(testPlayer);
        simulate(1f);

        assertEquals(GooseComponent.GooseState.WANDER, gooseComponent.state, 
            "Goose should not be chasing player"
        );

        Vector2 goosePos = testGoose.getComponent(TransformComponent.class).position;

        // Move player inside of goose's detection radius
        testPlayer.getComponent(TransformComponent.class).position = goosePos.cpy().sub(new Vector2(4f, 4f));

        simulate(0.2f);

        // Goose should now be chasing player
        assertEquals(GooseComponent.GooseState.CHASE, gooseComponent.state, 
            "Goose should be chasing player"
        );

        // Moving player outside of goose's detection radius, but inside it's forget target radius
        // Goose should still chase the player
        testPlayer.getComponent(TransformComponent.class).position = goosePos.cpy().add(new Vector2(8, 8));

        simulate(0.1f);

        // Goose should now be chasing player
        assertEquals(GooseComponent.GooseState.CHASE, gooseComponent.state, 
            "Goose should still be chasing player"
        );

        // Moving player outside of goose forget radius
        testPlayer.getComponent(TransformComponent.class).position = goosePos.cpy().add(new Vector2(13, 13));

        simulate(0.1f);

        // Goose should now be chasing player
        assertEquals(GooseComponent.GooseState.WANDER, gooseComponent.state, 
            "Goose should be in WANDER state"
        );

    }   

    // Checking that the goose retreats after biting a player
    @Test
    public void retreatTest() {
        GooseSystem testGS = new GooseSystem(testStepper, testPublisher);
        testEngine.addSystem(testGS);

        Entity testGoose = testEntMaker.makeGoose(0, 0);
        Entity testPlayer = testEntMaker.makePlayer(1, 1);
        GooseComponent gooseComponent = testGoose.getComponent(GooseComponent.class);

        // Setting the player as the target, then simulating the game for 1.5 seconds
        // During this time goose should bite the player
        testGS.setTarget(testPlayer);
        simulate(1.5f);

        assertEquals(GooseComponent.GooseState.RETREAT, gooseComponent.state, 
            "Goose should be retreating"
        );

        simulate(1.5f);

        // Goose should now have exited retreating state
        assertNotEquals(GooseComponent.GooseState.RETREAT, gooseComponent.state, 
            "Goose should be retreating"
        );
    }   
}
