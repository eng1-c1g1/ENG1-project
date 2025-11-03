package io.github.maze11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.maze11.ui.FontGenerator;

/**
 * Abstract base class for menu screens (MenuScreen, GameOverScreen, WinScreen).
 * Handles common functionality: font generation, stage setup, viewport scaling.
 * Reduces code duplication across menu screens.
 */
public abstract class BaseMenuScreen implements Screen {
    protected final MazeGame game;
    protected final Stage stage;
    protected final Skin skin;
    protected BitmapFont titleFont;
    protected BitmapFont bodyFont;

    /**
     * Creates a base menu screen with scaled fonts and viewport.
     */
    public BaseMenuScreen(MazeGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Generate scaled fonts
        this.titleFont = FontGenerator.generateRobotoFont(72, Color.WHITE, skin);
        this.bodyFont = FontGenerator.generateRobotoFont(28, Color.WHITE, skin);

        buildUI();
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Builds the UI elements for this screen.
     */
    protected abstract void buildUI();

    /**
     * Gets the background color for this screen.
     */
    protected abstract float[] getBackgroundColor();

    @Override
    public void render(float delta) {
        float[] bgColor = getBackgroundColor();
        Gdx.gl.glClearColor(bgColor[0], bgColor[1], bgColor[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        
        // Regenerate fonts for new screen size
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        
        titleFont = FontGenerator.generateRobotoFont(72, Color.WHITE, skin);
        bodyFont = FontGenerator.generateRobotoFont(28, Color.WHITE, skin);
        
        // Rebuild UI with new fonts
        stage.clear();
        buildUI();
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        System.out.println(this.getClass().getSimpleName() + " disposed");
    }
}