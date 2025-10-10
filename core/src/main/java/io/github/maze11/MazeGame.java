package io.github.maze11;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MazeGame extends Game {
    private SpriteBatch batch;
    private BitmapFont defaultFont;
    private FitViewport viewport;

    public SpriteBatch getBatch() {return batch;}
    public BitmapFont getDefaultFont() {return defaultFont;}
    public FitViewport getViewport() {return viewport;}

    @Override
    public void create() {
        batch = new SpriteBatch();

        viewport = new FitViewport(16, 12);
        this.setScreen(new LevelScreen(this));

        defaultFont = new BitmapFont();
        defaultFont.setUseIntegerPositions(false);
        //scale the font to the viewport
        defaultFont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        defaultFont.dispose();
    }
}
