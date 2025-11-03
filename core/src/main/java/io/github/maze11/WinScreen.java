package io.github.maze11;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.maze11.ui.FontGenerator;

/**
 * Win screen shown when player completes the game.
 * Displays total score and breakdown of how score was calcualted.
 * 
 * this scene is triggered when player reaches the exit (//TODO: Implement win condition)
 */
public class WinScreen extends BaseMenuScreen {
    private final int totalScore;
    private final int coffeeScore;
    private final int timeBonus;
    private final int completionBonus;

    public WinScreen(MazeGame game, int totalScore, int coffeeScore, int timeBonus, int completionBonus) {
        super(game);
        this.totalScore = totalScore;
        this.coffeeScore = coffeeScore;
        this.timeBonus = timeBonus;
        this.completionBonus = completionBonus;
        System.out.println("Win screen launched - Total Score: " + totalScore);
    }

    @Override
    protected void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title with Roboto
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("You Win!", titleStyle);

        // Score labels with Roboto
        Label.LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label scoreLabel = new Label("Score: " + totalScore, bodyStyle);
        
        Label.LabelStyle subtitleStyle = new Label.LabelStyle(bodyFont, Color.LIGHT_GRAY);
        Label summaryTitle = new Label("Score Breakdown:", subtitleStyle);

        Label.LabelStyle detailStyle = new Label.LabelStyle(bodyFont, Color.CYAN);
        Label coffeeLabel = new Label("Coffee collected: +" + coffeeScore, detailStyle);
        Label timeBonusLabel = new Label("Time bonus: +" + timeBonus, detailStyle);
        Label completionLabel = new Label("Completion bonus: +" + completionBonus, detailStyle);

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
        table.add(coffeeLabel).padBottom(5).row();
        table.add(timeBonusLabel).padBottom(5).row();
        table.add(completionLabel).padBottom(30).row();
        table.add(playAgainButton).width(200).height(60).padBottom(10).row();
        table.add(menuButton).width(200).height(60);

        stage.addActor(table);
    }

    @Override
    protected float[] getBackgroundColor() {
        return new float[]{0.1f, 0.3f, 0.1f}; // Dark green
    }
}