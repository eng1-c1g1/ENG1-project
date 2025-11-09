package io.github.maze11.screens;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.maze11.MazeGame;
import io.github.maze11.systems.gameState.ScoreCard;
import io.github.maze11.ui.FontGenerator;

/**
 * Win screen shown when player completes the game.
 * Displays total score and breakdown of how score was calcualted.
 *
 * this scene is triggered when player reaches the exit
 */
public class WinScreen extends BaseMenuScreen {
    private final ScoreCard scoreCard;

    public WinScreen(MazeGame game, ScoreCard scoreCard) {
        super(game);
        this.scoreCard = scoreCard;
        System.out.println("Win screen launched - Total Score: " + scoreCard.totalScore());
        buildUI();
    }

    @Override
    protected void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title with Roboto
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("You Have Escaped University!", titleStyle);

        // Subtitle with Roboto
        Label.LabelStyle subtitleStyle = new Label.LabelStyle(bodyFont, Color.LIGHT_GRAY);
        Label subtitle = new Label("The Dean Has Returned To Lurking The Halls.", subtitleStyle);

        // Score labels with Roboto
        Label.LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label scoreLabel = new Label("Score: " + scoreCard.totalScore(), bodyStyle);

        Label summaryTitle = new Label("Score Breakdown:", subtitleStyle);

        Label.LabelStyle detailStyle = new Label.LabelStyle(bodyFont, Color.CYAN);

        // Create the breakdown labels from the scorecard
        ArrayList<Label> breakdownLabels = new ArrayList<>();
        for (String str : scoreCard.breakdown()){
            breakdownLabels.add(new Label(str, detailStyle));
        }

        BitmapFont buttonFont = FontGenerator.generateRobotoFont(24, Color.WHITE, skin);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = buttonFont;

        TextButton playAgainButton = new TextButton("Play Again", buttonStyle);
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelScreen(game));
                dispose();
            }
        });

        TextButton menuButton = new TextButton("Main Menu", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });

        table.add(title).padBottom(30).row();
        table.add(scoreLabel).padBottom(20).row();
        table.add(summaryTitle).padBottom(10).row();

        // Display all the breakdown labels
        for (var label : breakdownLabels){
            table.add(label).padBottom(5).row();
        }
        table.padBottom(25).row();

        table.add(playAgainButton).width(200).height(60).padBottom(10).row();
        table.add(menuButton).width(200).height(60);

        stage.addActor(table);
    }

    @Override
    protected float[] getBackgroundColor() {
        return new float[]{0.1f, 0.3f, 0.1f}; // Dark green
    }
}
