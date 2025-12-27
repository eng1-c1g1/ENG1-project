package uk.ac.york.cs.eng1.MazeGame.headless;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import io.github.maze11.MazeGame;
import io.github.maze11.factory.EntityMaker;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.gameState.GameStateSystem;
import io.github.maze11.systems.physics.PhysicsSyncSystem;
import io.github.maze11.systems.physics.PhysicsSystem;
import io.github.maze11.systems.physics.PhysicsToTransformSystem;

import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

import java.util.List;

public abstract class AbstractHeadlessGdxTest {
    public MazeGame testMazeGame = new MazeGame();
    public PooledEngine testEngine;
    public EntityMaker testEntMaker;
    public final MessagePublisher testPublisher = new MessagePublisher();
    public static MessageListener testListener;
    public FixedStepper testStepper;

    // Runs before every single function starting with @Test
    @BeforeEach
    public void setup() {
        Gdx.gl = Gdx.gl20 = mock(GL20.class);
        HeadlessLauncher.main(new String[0]);
        
        // Setting up systems and components to allow the game to function
        testEngine = new PooledEngine();
        testStepper = new FixedStepper();
        testEntMaker = new EntityMaker(testEngine, testMazeGame);
        testListener = new MessageListener(testPublisher);
        
        // Basic systems that the vast majority of tests will need. To add missing systems, 
        // create a new instance of that system, then add the line:
        // "testEngine.addSystem(yourSystemName.class);"
        // See HiddenPiTests for (hopefully) working examples.
        List<EntitySystem> systems = List.of(
                new GameStateSystem(testPublisher, testMazeGame, testEngine),
                new InteractableSystem(testPublisher, testEngine, testEntMaker),
                new PlayerSystem(testStepper, testPublisher, testMazeGame),
                new PhysicsSyncSystem(testStepper),
                new PhysicsSystem(testStepper, testPublisher),
                new PhysicsToTransformSystem(testStepper)
                );

        for (EntitySystem system : systems) {
            testEngine.addSystem(system);
        }

        System.out.println("Headless backend successfully launched");
    }
}
