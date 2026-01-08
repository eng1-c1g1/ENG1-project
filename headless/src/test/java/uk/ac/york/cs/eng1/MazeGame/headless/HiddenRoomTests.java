package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

import io.github.maze11.messages.CollisionMessage;
import io.github.maze11.systems.HiddenWallSystem;
import io.github.maze11.systems.InteractableSystem;

public class HiddenRoomTests extends AbstractHeadlessGdxTest {

    // Testing to make sure pressure plate doesn't crash the game when there is no wall to remove
    @Test
    public void noWallTest() {
        // Creating and adding the hidden wall system
        HiddenWallSystem testHWS = new HiddenWallSystem(testStepper, testPublisher);
        testEngine.addSystem(testHWS);

        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        Entity testPressurePlate = testEntMaker.makePressurePlate(0, 0, "testPlate");
        Entity testPlayer = testEntMaker.makePlayer(0, 0);

        // Activating the pressure plate
        testPublisher.publish(new CollisionMessage(testPressurePlate, testPlayer));
        testInteractSystem.update(0);
        testHWS.fixedUpdate(0);

        assertTrue(true);
    }
    // Testing whether the wall is opened when the pressure plate is stepped on
    @Test
    public void openHiddenWallTest() {
        // Creating and adding the hidden wall system
        HiddenWallSystem testHWS = new HiddenWallSystem(testStepper, testPublisher);
        testEngine.addSystem(testHWS);

        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        Entity testPressurePlate = testEntMaker.makePressurePlate(0, 0, "testPlate");
        Entity testWall = testEntMaker.makeFalseWall(0, 0, 0, 0, "testPlate");
        Entity testPlayer = testEntMaker.makePlayer(0, 0);

        ImmutableArray<Entity> entityList = testEngine.getEntities();

        // Checking the wall has been added to the engine correctly
        assertTrue(entityList.contains(testWall, false), 
    "A wall entity should have been added to the list of entities");

        // Activating the pressure plate
        testPublisher.publish(new CollisionMessage(testPressurePlate, testPlayer));
        testInteractSystem.update(0);
        testHWS.fixedUpdate(0);
        
        entityList = testEngine.getEntities();
        assertFalse(entityList.contains(testWall, false),
    "The wall entity should have been removed from the entity list");

    }

    // Testing how the wall/pressure plate react when there are multiple walls/pressure plates
    @Test
    public void multiWallTests() {
        // Creating and adding the hidden wall system
        HiddenWallSystem testHWS = new HiddenWallSystem(testStepper, testPublisher);
        testEngine.addSystem(testHWS);

        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);

        Entity testPressurePlate = testEntMaker.makePressurePlate(0, 0, "testPlate");
        Entity testWall = testEntMaker.makeFalseWall(0, 0, 0, 0, "testPlate");
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        Entity otherWall = testEntMaker.makeFalseWall(0, 0, 0, 0, "otherPlate");

        ImmutableArray<Entity> entityList = testEngine.getEntities();

        // Checking whether a wall not linked to the activated pressure plate will open
        // Activating the pressure plate
        testPublisher.publish(new CollisionMessage(testPressurePlate, testPlayer));
        testInteractSystem.update(0);
        testHWS.fixedUpdate(0);
        
        entityList = testEngine.getEntities();
        assertTrue(entityList.contains(otherWall, false),
    "The wall not linked to the pressure plate should not have been removed from the entity list");
        assertFalse(entityList.contains(testWall, false),
    "The linked wall should have been removed from the entity list");
        
    }
}
