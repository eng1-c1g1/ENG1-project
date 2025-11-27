package io.github.maze11.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.maze11.MazeGame;
import io.github.maze11.systems.PauseSystem;
import io.github.maze11.ui.FontGenerator;

public class PauseScreen extends BaseMenuScreen {
    
    Screen prevScreen;
    public PauseScreen(MazeGame game, Screen prevScreen) {
        super(game);
        buildUI();
        this.prevScreen = prevScreen;
    }
    @Override
    protected void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title with Roboto
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("Game Paused", titleStyle);

        // Subtitle with Roboto
        Label.LabelStyle subtitleStyle = new Label.LabelStyle(bodyFont, Color.LIGHT_GRAY);
        Label subtitle = new Label("Quick! Escape the University.\nCollect coffee to go faster.", subtitleStyle);

        BitmapFont buttonFont = FontGenerator.generateRobotoFont(24, Color.WHITE, skin);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = buttonFont;

        TextButton menuButton = new TextButton("Quit Game", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });
        table.add(title).padBottom(30).row();
        table.add(subtitle).padBottom(10).row();
        table.padBottom(25).row();

        table.add(menuButton).width(200).height(60);

        stage.addActor(table);
    }

    @Override
    protected float[] getBackgroundColor() {
        return new float[]{0.3f, 0.1f, 0.1f, 0.6f}; // Dark red
    }


    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (PauseSystem.gamePaused) {
                PauseSystem.unpauseGame();
                game.switchScreen(prevScreen);
                System.out.println("Unpaused");
            }
        }
        
    }
}
