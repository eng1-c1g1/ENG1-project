package io.github.maze11;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class LevelScreen implements Screen {
    final MazeGame game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public LevelScreen(MazeGame game) {
        this.game = game;

        map = new TmxMapLoader().load("floor.tmx");
        
    }

    @Override
    public void show() {    
        float unitScale = 1f / 32f;  
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
        
    }

    @Override
    public void render(float delta) {
        //placeholder code to show that this screen is displayed
        ScreenUtils.clear(Color.BLACK);

        var viewport = game.getViewport();
        viewport.apply();

        // Render the Tiled map
        mapRenderer.setView((OrthographicCamera) viewport.getCamera());
        mapRenderer.render();

        // Optional overlay text with SpriteBatch
        var batch = game.getBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        game.getDefaultFont().draw(batch, "Tiled floor level loaded!", 1, 1.5f);
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
