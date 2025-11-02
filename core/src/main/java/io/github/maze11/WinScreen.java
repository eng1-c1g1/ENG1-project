package io.github.maze11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Win screen shown when player completes the game.
 * Displays total score and breakdown of how score was calcualted.
 * 
 * this scene is triggered when player reaches the exit (//TODO: Implement win condition)
 */
public class WinScreen implements Screen {
    private final MazeGame game;
    private final Stage stage;
    private final Skin skin;
    private final int totalScore;
    private final int coffeeScore;
    private final int timeBonus;
    private final int completionBonus;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    // 
    public WinScreen(MazeGame game, int totalScore, int coffeeScore, int timeBonus, int completionBonus) {
        this.game = game;
        this.totalScore = totalScore;
        this.coffeeScore = coffeeScore;
        this.timeBonus = timeBonus;
        this.completionBonus = completionBonus;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // Own skin

        // Generate Roboto fonts for this screen
        generateRobotoFonts();

        buildUI();
        Gdx.input.setInputProcessor(stage);
        
        System.out.println("Win screen launched - Total Score: " + totalScore);
    }

    private void generateRobotoFonts() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Roboto-Regular.ttf")
            );

            // Title font (large)
            FreeTypeFontParameter titleParam = new FreeTypeFontParameter();
            titleParam.size = 72;
            titleParam.color = Color.WHITE;
            titleFont = generator.generateFont(titleParam);

            // Body font (medium)
            FreeTypeFontParameter bodyParam = new FreeTypeFontParameter();
            bodyParam.size = 28;
            bodyParam.color = Color.WHITE;
            bodyFont = generator.generateFont(bodyParam);

            generator.dispose();
            
            System.out.println("Win: Roboto fonts generated successfully");
        } catch (Exception e) {
            System.err.println("Win: Failed to load Roboto, using default font");
            e.printStackTrace();
            // Fallback to default fonts
            titleFont = skin.getFont("default-font");
            bodyFont = skin.getFont("default-font");
        }
    }

    private void buildUI() {
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

        TextButton playAgainButton = new TextButton("Play Again", skin);
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Restarting game...");
                game.setScreen(new LevelScreen(game));
                dispose();
            }
        });

        TextButton menuButton = new TextButton("Main Menu", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Returning to menu...");
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.3f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        System.out.println("Win screen disposed");
    }
}