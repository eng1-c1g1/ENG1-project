package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.ashley.core.Entity;

import io.github.maze11.messages.*;
import io.github.maze11.systems.gameState.GameStateSystem;
import io.github.maze11.systems.GooseSystem;
import io.github.maze11.systems.HiddenWallSystem;
import io.github.maze11.systems.gameState.EventCounter;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.BullySystem;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.components.GooseComponent;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.BullyComponent;


public class AchievementTests extends AbstractHeadlessGdxTest {

    static Preferences achievements;
    // Creates Achievements file before any of tests run
    @BeforeAll
    public static void createScoreFile() {
        
        HeadlessLauncher.main(null);
        achievements = Gdx.app.getPreferences("Achievements");
        achievements.clear();
        achievements.flush();
        System.out.println("Achievements file created");
    }

    // Clears score file before each test runs.
    @BeforeEach
    public void clearScoreFile() {
        achievements = Gdx.app.getPreferences("Achievements");
        achievements.clear();
        achievements.flush();
    }

    @Test
    public void noAchievementsTest() {
        // create test EventCounter:
        EventCounter testCounter = new EventCounter();
        // Reading file when empty
        int numAchievements = testCounter.getAllAchievements().size();
        assertEquals(0, numAchievements, "getAchievements returning achievements when prefs should be empty");
    }


    // normal tests:
    @Test
    public void hiddenPisAchievementTest() {
        GameStateSystem testGSSystem = testEngine.getSystem(GameStateSystem.class);
        // get the eventCounter it will be broadcast to:
        EventCounter testCounter = testGSSystem.eventCounter;
        
        // trigger our event:
        Entity testPi = testEntMaker.makePi(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        
        // final collision
        PiCollectMessage.numPis = 2;
        CollisionMessage msg = new CollisionMessage(testPi, testPlayer);

        // publish message and update all our components:
        testPublisher.publish(msg);
        testInteractSystem.update(0f);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // update achievements, assert that we have achieved it:
        testCounter.updateAchievements();
        achievements = Gdx.app.getPreferences("Achievements");
        boolean piAchieved = achievements.getBoolean("Got All 3 Hidden Pis in 1 run");
        assertTrue(piAchieved, "didn't achieve all 3 hidden Pis even though it should have");

    }

    @Test
    public void allHiddenEventsAchievementTest() {
        GameStateSystem testGSSystem = testEngine.getSystem(GameStateSystem.class);
        // get the eventCounter it will be broadcast to:
        EventCounter testCounter = testGSSystem.eventCounter;

        // trigger our event:
        Entity testPi = testEntMaker.makePi(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        
        // final collision
        PiCollectMessage.numPis = 2;

        // publish message and update all our components:
        testPublisher.publish(new CollisionMessage(testPi, testPlayer));
        testInteractSystem.update(0f);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // Do LongBoi collision:
        Entity testLongBoi = testEntMaker.makeLongBoi(0f, 0f);
        testPublisher.publish(new CollisionMessage(testLongBoi, testPlayer));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // Do Hidden Room activation:
        HiddenWallSystem testHWS = new HiddenWallSystem(testStepper, testPublisher);
        testEngine.addSystem(testHWS);
    
        Entity testPressurePlate = testEntMaker.makePressurePlate(0, 0, "testPlate");
        Entity testWall = testEntMaker.makeFalseWall(0, 0, 0, 0, "testPlate");
        
        // publish our message:
        testPublisher.publish(new CollisionMessage(testPressurePlate, testPlayer));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);
        testHWS.fixedUpdate(0);

        testCounter.updateAchievements();
        achievements = Gdx.app.getPreferences("Achievements");
        boolean achieved = achievements.getBoolean("Got All Hidden Events in 1 run");
        assertTrue(achieved, "didn't achieve all  hidden events even though it should have");
    }

    @Test
    public void allEventsAchievementTest() {
        /*
        horrifically inelegant but they're all slightly different so can't do w/ generic function or loop
        rely on common resources so would just be more of a headache to use a generic function for most while passing those resources to the func
        therefore use this solution
        */
        GameStateSystem testGSSystem = testEngine.getSystem(GameStateSystem.class);
        // get the eventCounter it will be broadcast to:
        EventCounter testCounter = testGSSystem.eventCounter;

        // trigger our event:
        Entity testPi = testEntMaker.makePi(0f, 0f);
        Entity testPlayer = testEntMaker.makePlayer(0, 0);
        PlayerSystem testPS = testEngine.getSystem(PlayerSystem.class);
        InteractableSystem testInteractSystem = testEngine.getSystem(InteractableSystem.class);
        // final collision
        PiCollectMessage.numPis = 2;
        // publish message and update all our components:
        testPublisher.publish(new CollisionMessage(testPi, testPlayer));
        testInteractSystem.update(0f);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // Do LongBoi collision:
        Entity testLongBoi = testEntMaker.makeLongBoi(0f, 0f);
        testPublisher.publish(new CollisionMessage(testLongBoi, testPlayer));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // Do Hidden Room activation:
        HiddenWallSystem testHWS = new HiddenWallSystem(testStepper, testPublisher);
        testEngine.addSystem(testHWS);
        // make actual pressure plate and wall
        Entity testPressurePlate = testEntMaker.makePressurePlate(0, 0, "testPlate");
        Entity testWall = testEntMaker.makeFalseWall(0, 0, 0, 0, "testPlate");
        // publish our message:
        testPublisher.publish(new CollisionMessage(testPressurePlate, testPlayer));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);
        testHWS.fixedUpdate(0);

    
        // coffee:
        Entity testCoffee = testEntMaker.makeCoffee(0f, 0f);
        testPublisher.publish(new CollisionMessage(testPlayer, testCoffee));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // ankh:
        Entity testAnkh = testEntMaker.makeAnkh(0f, 0f);
        testPublisher.publish(new CollisionMessage(testPlayer, testAnkh));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);
        
        // puddle:
        Entity testPuddle = testEntMaker.makePuddle(0f, 0f);
        testPublisher.publish(new CollisionMessage(testPlayer, testPuddle));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);
 
        // checkin code:
        Entity testCheckin = testEntMaker.makeCheckInCode(0f, 0f);
        testPublisher.publish(new CollisionMessage(testPlayer, testCheckin));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // time loss:
        Entity testTimeLoss = testEntMaker.makeTimeLoss(0f, 0f);
        testPublisher.publish(new CollisionMessage(testPlayer, testTimeLoss));
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // goose bite:
        GooseSystem testGS = new GooseSystem(testStepper, testPublisher);
        testEngine.addSystem(testGS);
        Entity testGoose = testEntMaker.makeGoose(0, 0);
        GooseComponent gooseComponent = testGoose.getComponent(GooseComponent.class);
        // Setting the player as the target, then simulating the game for 1.5 seconds
        // During this time goose should bite the player
        testGS.setTarget(testPlayer);
        simulate(1.5f);
        //
        testInteractSystem.update(0);
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);

        // bully system:
        BullySystem testBullySystem = new BullySystem(testStepper, testPublisher, testEngine);
        testEngine.addSystem(testBullySystem);
        // make bully and bribe
        Entity testBully = testEntMaker.makeBully(0f, 0f);
        Entity testBribe = testEntMaker.makeBribe(0f, 0f);
        BullySystem testBS = testEngine.getSystem(BullySystem.class);
        // get our components
        PlayerComponent testPC = testPlayer.getComponent(PlayerComponent.class);
        BullyComponent testBC = testBully.getComponent(BullyComponent.class);
        InteractableComponent testIC = testBully.getComponent(InteractableComponent.class);
        // player gets the bribe:
        testPublisher.publish(new CollisionMessage(testPlayer, testBribe));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testPS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision
        // player collides w/ bully w/ bribe, sends message:
        testPublisher.publish(new CollisionMessage(testPlayer, testBully));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testBS.fixedUpdate(0f);  // Actually processes the event that occurred from the collision
        testPS.fixedUpdate(0f);
        testGSSystem.update(0f);
        
        // teleport:
        Entity testTeleporter = testEntMaker.makeTeleportation(0f, 0f, new Vector2(1f,1f));
        testPS.fixedUpdate(0f); // Actually processes the event that occurred from the collision
        testPublisher.publish(new CollisionMessage(testPlayer, testTeleporter));    // Tells the game the player has collided with an entity
        testInteractSystem.update(0f);   // Sends message confirming collision has occurred
        testGSSystem.update(0f);
        
        // actually test now that we've done every event:
        testCounter.updateAchievements();
        achievements = Gdx.app.getPreferences("Achievements");
        boolean achieved = achievements.getBoolean("Got All Events in 1 run");
        assertTrue(achieved, "didn't achieve all events even though it should have");

    }
   
}
