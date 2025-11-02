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
 * Game over screen shown when timer runs out.
 * Displays score passed from GameStateSystem.
 * provides options to restart or return to main menu 
 * 
 * this screen is triggered by GameStateSystem when it recieves a TIMER_EXPIRED message.
 */
public class GameOverScreen implements Screen {
    private final MazeGame game;
    private final Stage stage;
    private final Skin skin;
    private final int score;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    // creates the game over screen
    public GameOverScreen(MazeGame game, int score) {
        this.game = game;
        this.score = score;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // Own skin

        // Generate Roboto fonts for this screen
        generateRobotoFonts();

        buildUI();
        Gdx.input.setInputProcessor(stage);
        
        System.out.println("Game Over screen launched - Score: " + score);
    }

    private void generateRobotoFonts() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Roboto-Regular.ttf")
            );

            // Title font
            FreeTypeFontParameter titleParam = new FreeTypeFontParameter();
            titleParam.size = 72;
            titleParam.color = Color.WHITE;
            titleFont = generator.generateFont(titleParam);

            // Body font
            FreeTypeFontParameter bodyParam = new FreeTypeFontParameter();
            bodyParam.size = 32;
            bodyParam.color = Color.WHITE;
            bodyFont = generator.generateFont(bodyParam);

            generator.dispose();
            
            System.out.println("GameOver: Roboto fonts generated successfully");
        } catch (Exception e) {
            System.err.println("GameOver: Failed to load Roboto, using default font");
            e.printStackTrace();
            // Fallback to default fonts
            titleFont = skin.getFont("default-font");
            bodyFont = skin.getFont("default-font");
        }
    }

    /**
     * builds the UI layout using scene2D table same as MenuScreen
     */
    private void buildUI() {
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

        TextButton restartButton = new TextButton("Restart", skin);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Restarting game...");
                game.setScreen(new LevelScreen(game)); // new LevelScreen i.e. fresh ECS World
                dispose(); // clean up game over screen resources 
            }
        });
        // main menu button to return to menuScreeen
        TextButton menuButton = new TextButton("Main Menu", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Returning to menu...");
                game.setScreen(new MenuScreen(game));
                dispose();
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.1f, 0.1f, 1f);
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

    /**
     * cleans up resources when screen is disposed
     * each Screen uses its own resource so must dispose them there. 
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        System.out.println("Game Over screen disposed");
    }
}