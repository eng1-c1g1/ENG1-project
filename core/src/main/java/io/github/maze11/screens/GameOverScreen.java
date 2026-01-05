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
import io.github.maze11.systems.gameState.LeaderBoardSystem;
import io.github.maze11.systems.gameState.ScoreCard;
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
    private ScoreCard scoreCard;

    public GameOverScreen(MazeGame game, ScoreCard scoreCard) {
        super(game);
        this.score = scoreCard.totalScore();
        this.scoreCard = scoreCard;
        System.out.println("Game Over screen launched - Score: " + score);
        buildUI();
        
    }

    @Override
    protected void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("The Dean has arrived!", titleStyle);

        // Subtitle
        Label.LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label subtitle = new Label("You have failed to escape in time...", bodyStyle);

        // Score
        Label.LabelStyle scoreStyle = new Label.LabelStyle(bodyFont, Color.YELLOW);
        Label scoreLabel = new Label("Score: " + score, scoreStyle);

        // CHANGED: Score breakdown now appears after a loss
        // Create the breakdown labels from the scorecard

        Label.LabelStyle detailStyle = new Label.LabelStyle(bodyFont, Color.CYAN);

        ArrayList<Label> breakdownLabels = new ArrayList<>();
        for (String str : scoreCard.breakdown()){
            breakdownLabels.add(new Label(str, detailStyle));
        }
   
        BitmapFont buttonFont = FontGenerator.generateRobotoFont(24, Color.WHITE, skin);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = buttonFont;


        TextButton restartButton = new TextButton("Try Again", buttonStyle);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.switchScreen(new LevelScreen(game)); // new LevelScreen i.e. fresh ECS World
                playMenuClick();
            }
        });

        TextButton menuButton = new TextButton("Main Menu", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.switchScreen(new MenuScreen(game));
                playMenuClick();
            }
        });
        // vertical stack layout again between elements
        table.add(title).padBottom(20).row();
        table.add(subtitle).padBottom(20).row();
        // Display all the breakdown labels
        for (var label : breakdownLabels){
            table.add(label).padBottom(5).row();
        }
        table.padBottom(40).row();
        table.add(restartButton).width(200).height(60).padBottom(10).row();
        table.add(menuButton).width(200).height(60);

        stage.addActor(table);

        // Checking whether score is a high score
        LeaderBoardSystem leaderboard = new LeaderBoardSystem(MenuScreen.playerName);
        leaderboard.submitScore(score);
    }

    @Override
    protected float[] getBackgroundColor() {
        return new float[]{0.3f, 0.1f, 0.1f, 1f}; // Dark red
    }
}
