package io.github.maze11;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.maze11.assetLoading.Assets;


public class MazeGame extends Game {
    private SpriteBatch batch;
    private Assets assets;
    public static final int PIXELS_TO_UNIT = 32;

    public SpriteBatch getBatch() {
        return batch;
    }

    public Assets getAssets() { return assets; }

    @Override
    public void create() {
        System.out.println("Maze game launched");
        batch = new SpriteBatch();

        assets = new Assets();
        assets.load();

        this.setScreen(new LevelScreen(this)); // moved here to ensure assets are loaded first

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose(); // Dispose of AssetManager and its assets
    }
}
