package io.github.maze11;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.maze11.systems.CameraSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.RenderingSystem;

public class LevelScreen implements Screen {
    private final MazeGame game;
    private final PooledEngine engine;
    private final TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public LevelScreen(MazeGame game) {
        this.game = game;

        map = game.getAssetManager().get("floor.tmx", TiledMap.class); // Load the map using AssetManager

        engine = new PooledEngine();
        engine.addSystem(new PlayerSystem(game));
        engine.addSystem(new CameraSystem());
        engine.addSystem(new RenderingSystem(game).startDebugView());


        // Temporary debugging code to create objects here
        var debugManager = new DebuggingIndicatorManager(engine, game);
        debugManager.createDebugSquare(1,1);
        debugManager.createDebugSquare(1.5f,1.5f);
        debugManager.createDebugSquare(3f, 3f, 2f, 2f);
        debugManager.createDebugPlayer(4f, 4f);
    }

    @Override
    public void show() {
        float unitScale = 1f / 32f;
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
        System.out.println("Level screen began displaying");
    }

    @Override
    public void render(float delta) {

        var viewport = game.getViewport();
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
        game.getDefaultFont().draw(batch, "Tiled floor level loaded!", 1, 1.5f);

        // ######## END RENDER ###############
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //if size is 0, it is minimised, no need to resize
        if(width <= 0 || height <= 0) return;

        game.getViewport().update(width, height, true);
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

    }
}
