package uk.ac.york.cs.eng1.MazeGame.headless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;


import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

public abstract class AbstractHeadlessGdxTest {
    @BeforeEach
    public void setup() {
        Gdx.gl = Gdx.gl20 = mock(GL20.class);
        HeadlessLauncher.main(new String[0]);
        Gdx.app = mock(Gdx.app);
        System.out.println("Headless backend launched");
    }
}
