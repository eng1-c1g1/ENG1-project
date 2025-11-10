package io.github.maze11;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.maze11.assetLoading.AssetLoader;
import io.github.maze11.screens.MenuScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game class that manages screen switching and shared resources.
 * Extends LibGDX Game class which provides screen management via setScreen().
 *
 * Each Screen is independent and owns its own resources (fonts, UI, ECS)
 */
public class MazeGame extends Game {
    private SpriteBatch batch;
    private AssetLoader assetLoader;
    public static final int PIXELS_TO_UNIT = 32;

    /** Returns the spriteBatch to be used for rendering the game */
    public SpriteBatch getBatch() {
        return batch;
    }

    /** Returns a reference to the AssetLoader which can be used to access all assets */
    public AssetLoader getAssetLoader() { return assetLoader; }

    /** The screens queued for disposal */
    private List<Screen> discontinuedScreens = new ArrayList<Screen>();

    @Override
    public void create() {
        System.out.println("Maze game launched");
        batch = new SpriteBatch();

        assetLoader = new AssetLoader();
        assetLoader.load();

        this.setScreen(new MenuScreen(this));
    }


    @Override
    public void render() {
        super.render();
        for (Screen screen : discontinuedScreens) {
            screen.dispose();
        }
        discontinuedScreens.clear();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetLoader.dispose();
    }

    /**
     * Switches the screen to the new one and enqueues the old one for disposal
     * @param newScreen
     */
    public void switchScreen(Screen newScreen){
        discontinuedScreens.add(screen);
        this.setScreen(newScreen);
    }
}
