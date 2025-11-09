package io.github.maze11.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.maze11.MazeGame;
import io.github.maze11.ui.FontGenerator;

/**
 * Game over screen shown when timer runs out.
 * Displays score passed from GameStateSystem.
 * provides options to restart or return to main menu
 *
 * this screen is triggered by GameStateSystem when it recieves a TIMER_EXPIRED message.
 */
public class GameOverScreen extends BaseMenuScreen {
    private final int score;

    public GameOverScreen(MazeGame game, int score) {
        super(game);
        this.score = score;
        System.out.println("Game Over screen launched - Score: " + score);
        buildUI();
    }

    @Override
    protected void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("Game Over!", titleStyle);

        // Subtitle
        Label.LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label subtitle = new Label("Time's up!", bodyStyle);

        // Score
        Label.LabelStyle scoreStyle = new Label.LabelStyle(bodyFont, Color.YELLOW);
        Label scoreLabel = new Label("Score: " + score, scoreStyle);

        BitmapFont buttonFont = FontGenerator.generateRobotoFont(24, Color.WHITE, skin);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = buttonFont;

        TextButton restartButton = new TextButton("Restart", buttonStyle);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.switchScreen(new LevelScreen(game)); // new LevelScreen i.e. fresh ECS World
            }
        });

        TextButton menuButton = new TextButton("Main Menu", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.switchScreen(new MenuScreen(game));
            }
        });
        // vertical stack layout again between elements
        table.add(title).padBottom(20).row();
        table.add(subtitle).padBottom(20).row();
        table.add(scoreLabel).padBottom(40).row();
        table.add(restartButton).width(200).height(60).padBottom(10).row();
        table.add(menuButton).width(200).height(60);

        stage.addActor(table);
    }

    @Override
    protected float[] getBackgroundColor() {
        return new float[]{0.3f, 0.1f, 0.1f}; // Dark red
    }
}
