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
 * Main menu screen contains UI logic for buttons when game starts
 * provides options to start gam or quit
 */
public class MenuScreen implements Screen {
    private final MazeGame game;
    private final Stage stage;
    private final Skin skin;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    // creates menu screen
    public MenuScreen(MazeGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // Own skin

        // create custom Roboto fonts for this screen
        generateRobotoFonts();

        buildUI();
        Gdx.input.setInputProcessor(stage); // enables input handling for buttons
        
        System.out.println("Menu screen launched with Roboto font");
    }
    /**
     * generates roboto fonts from ttf (trueType) file
     * uses default skin fonts if loading fails 
     */
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
            
            System.out.println("Menu: Roboto fonts generated successfully");
        } catch (Exception e) {
            System.err.println("Menu: Failed to load Roboto, using default font");
            e.printStackTrace();
            // Fallback to default fonts
            titleFont = skin.getFont("default-font");
            bodyFont = skin.getFont("default-font");
        }
    }
    /**
     * builds the UI layout for the menu scr
     * Layout (top to bottom):
     * - title: "Maze Game"
     * - subtitle: "navigate the maze!"
     * - start game button -> switches to levelscreen
     * - quit button -> exits application
     */
    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title with Roboto
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("Maze Game", titleStyle);

        // Subtitle with Roboto
        Label.LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.LIGHT_GRAY);
        Label subtitle = new Label("Navigate the maze!", bodyStyle);

        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Starting game...");
                game.setScreen(new LevelScreen(game)); // switch to gameplayscreen 
                dispose(); // clean up menyu screen rsources 
            }
        });
        // quit button - exits application 
        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // updates viewport on window resize
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
        System.out.println("Menu screen disposed");
    }
}