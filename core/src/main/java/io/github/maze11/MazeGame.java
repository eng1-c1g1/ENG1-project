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


public class MazeGame extends Game {
    private SpriteBatch batch;
    private BitmapFont defaultFont;
    private FitViewport viewport;

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getDefaultFont() {
        return defaultFont;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    private AssetManager assetManager;

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void create() {
        System.out.println("Maze game launched");
        batch = new SpriteBatch();

        viewport = new FitViewport(16, 12);

        defaultFont = new BitmapFont();
        defaultFont.setUseIntegerPositions(false);
        //scale the font to the viewport
        defaultFont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        // Initialise AssetManager and load assets here 
        assetManager = new AssetManager();
        assetManager.load("Test_Square.png", Texture.class);
        assetManager.load("origin_indicator.png", Texture.class);
        // Register tiledmap loader before loading .tmx
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("floor.tmx", TiledMap.class);
        assetManager.finishLoading(); // Wait until all assets are loaded

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
        assetManager.dispose(); // Dispose of AssetManager and its assets
    }
}
