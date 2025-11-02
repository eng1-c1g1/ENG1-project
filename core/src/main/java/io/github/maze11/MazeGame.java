package io.github.maze11;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.maze11.assetLoading.AssetLoader;

public class MazeGame extends Game {
    private SpriteBatch batch;
    private AssetLoader assetLoader;
    private Skin uiSkin;
    public static final int PIXELS_TO_UNIT = 32;

    public SpriteBatch getBatch() {
        return batch;
    }

    public AssetLoader getAssetLoader() { return assetLoader; }

    public Skin getUiSkin() {
        return uiSkin;
    }

    @Override
    public void create() {
        System.out.println("Maze game launched");
        batch = new SpriteBatch();

        assetLoader = new AssetLoader();
        assetLoader.load();

        // Create UI skin with Roboto font 
        createUiSkin();

        this.setScreen(new MenuScreen(this));
    }

    private void createUiSkin() {
        // Load default skin
        uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        try {
            // Check if font file exists
            if (!Gdx.files.internal("fonts/Roboto-Regular.ttf").exists()) {
                System.err.println("ERROR: fonts/Roboto-Regular.ttf not found! Using default font.");
                return; // Use default skin fonts
            }

            System.out.println("Loading Roboto font from: " + Gdx.files.internal("fonts/Roboto-Regular.ttf").path());

            // Generate Roboto fonts using FreeTypeFontGenerator
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Roboto-Regular.ttf")
            );

            // Title font (large)
            FreeTypeFontParameter titleParam = new FreeTypeFontParameter();
            titleParam.size = 48;
            BitmapFont titleFont = generator.generateFont(titleParam);

            // Body font (medium)
            FreeTypeFontParameter bodyParam = new FreeTypeFontParameter();
            bodyParam.size = 24;
            BitmapFont bodyFont = generator.generateFont(bodyParam);

            // Button font (medium)
            FreeTypeFontParameter buttonParam = new FreeTypeFontParameter();
            buttonParam.size = 20;
            BitmapFont buttonFont = generator.generateFont(buttonParam);

            generator.dispose();

            // Add fonts to skin
            uiSkin.add("title-font", titleFont, BitmapFont.class);
            uiSkin.add("body-font", bodyFont, BitmapFont.class);
            uiSkin.add("button-font", buttonFont, BitmapFont.class);
            uiSkin.add("default-font", bodyFont, BitmapFont.class);

            // Update default styles to use Roboto
            uiSkin.get(com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle.class).font = bodyFont;
            uiSkin.get(com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle.class).font = buttonFont;

            System.out.println("UI skin created with Roboto fonts successfully!");

        } catch (Exception e) {
            System.err.println("ERROR loading Roboto font: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Falling back to default skin fonts");
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetLoader.dispose();
        if (uiSkin != null) {
            uiSkin.dispose(); // Disposes all fonts too
        }
    }
}