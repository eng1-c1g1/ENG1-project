package io.github.maze11.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.maze11.MazeGame;
import io.github.maze11.ui.FontGenerator;

/**
 * Main menu screen contains UI logic for buttons when game starts
 * provides options to start gam or quit
 */
public class MenuScreen extends BaseMenuScreen {

    public MenuScreen(MazeGame game) {
        super(game);
        System.out.println("Menu screen launched");
        buildUI();
    }

    @Override
    protected void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("Maze Game", titleStyle);

        Label.LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.LIGHT_GRAY);
        Label subtitle = new Label("Navigate the maze!", bodyStyle);

        BitmapFont buttonFont = FontGenerator.generateRobotoFont(24, Color.WHITE, skin);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = buttonFont;

        TextButton startButton = new TextButton("Start Game", buttonStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.switchScreen(new LevelScreen(game));
            }
        });

        TextButton quitButton = new TextButton("Quit", buttonStyle);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });
        // vertical stack layout for elements in menu screen
        table.add(title).padBottom(20).row(); // e.g. title with 20px space below
        table.add(subtitle).padBottom(40).row();
        table.add(startButton).width(200).height(60).padBottom(10).row();
        table.add(quitButton).width(200).height(60);

        stage.addActor(table);
    }

    @Override
    protected float[] getBackgroundColor() {
        return new float[]{0.1f, 0.1f, 0.2f}; // Dark blue
    }
}
