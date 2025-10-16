package io.github.maze11;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
// assetmanager
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import io.github.maze11.assetLoading.Assets;


public class MazeGame extends Game {
    private SpriteBatch batch;
    private BitmapFont defaultFont;
    private FitViewport viewport;
    private Assets assets;

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getDefaultFont() {
        return defaultFont;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public Assets getAssets() { return assets; }

    @Override
    public void create() {
        System.out.println("Maze game launched");
        batch = new SpriteBatch();

        viewport = new FitViewport(16, 12);

        defaultFont = new BitmapFont();
        defaultFont.setUseIntegerPositions(false);
        //scale the font to the viewport
        defaultFont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

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
        defaultFont.dispose();
        assets.dispose(); // Dispose of AssetManager and its assets
    }
}
