package io.github.maze11;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class LevelScreen implements Screen {
    final MazeGame game;

    public LevelScreen(MazeGame game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //placeholder code to show that this screen is displayed
        ScreenUtils.clear(Color.BLACK);

        var viewport = game.getViewport();
        var batch = game.getBatch();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        game.getDefaultFont().draw(batch, "Displaying a placeholder for a level scene", 1, 1.5f);

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

    }
}
