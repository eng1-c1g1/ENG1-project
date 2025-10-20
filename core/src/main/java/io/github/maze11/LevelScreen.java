package io.github.maze11;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.maze11.systems.WorldCameraSystem;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.RenderingSystem;

public class LevelScreen implements Screen {
    private final MazeGame game;
    private final PooledEngine engine;
    private final TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final FitViewport viewport;
    private final BitmapFont defaultFont;

    public LevelScreen(MazeGame game) {
        this.game = game;

        // Create rendering singletons
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(16, 12, camera);
        map = game.getAssets().get(AssetId.Tilemap, TiledMap.class); // Load the map using AssetManager

        //create the font
        defaultFont = new BitmapFont();
        defaultFont.setUseIntegerPositions(false);
        //scale the font to the viewport
        defaultFont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        //create the engine
        engine = new PooledEngine();
        engine.addSystem(new PlayerSystem());
        engine.addSystem(new WorldCameraSystem(camera));
        engine.addSystem(new RenderingSystem(game).startDebugView());

        // Populate the world with objects
        EntityMaker entityMaker = new EntityMaker(engine, game);
        // Temporary debugging code to create objects here
        var debugManager = new DebuggingIndicatorManager(engine, game);
        debugManager.createDebugSquare(1,1);
        debugManager.createDebugSquare(1.5f,1.5f);
        debugManager.createDebugSquare(3f, 3f, 2f, 2f);
        entityMaker.makePlayer(4f, 4f);
    }



    @Override
    public void show() {
        float unitScale = 1f / 32f;
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
        System.out.println("Level screen began displaying");
    }

    @Override
    public void render(float delta) {

        viewport.apply();

        // Render the Tiled map
        mapRenderer.setView((OrthographicCamera) viewport.getCamera());

        var batch = game.getBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        // ######### START RENDER #############
        ScreenUtils.clear(Color.BLACK);

        mapRenderer.render();
        engine.update(delta);
        defaultFont.draw(batch, "Tiled floor level loaded!", 1, 1.5f);

        // ######## END RENDER ###############
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //if size is 0, it is minimised, no need to resize
        if(width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        defaultFont.dispose();
    }
}
