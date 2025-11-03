package io.github.maze11;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.maze11.assetLoading.AssetLoader;

/**
 * main game class that manages screen switching and shared resources.
 * Extends LibGDX Game class which provides screen management via setScreen().
 * 
 * Each Screen is indepedent and owns its own resources (fonts, UI, ECS)
 */
public class MazeGame extends Game {
    private SpriteBatch batch;
    private AssetLoader assetLoader;
    public static final int PIXELS_TO_UNIT = 32;

    public SpriteBatch getBatch() {
        return batch;
    }

    public AssetLoader getAssetLoader() { return assetLoader; }

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
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetLoader.dispose();
    }
}