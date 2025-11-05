package io.github.maze11.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Utility class for generating Roboto fonts for UI screens.
 * Reduces code duplication across MenuScreen, GameOverScreen, and WinScreen.
 * Fonts scale proportionally with screen height for responsive UI.
 */
public class FontGenerator {
    
    /**
     * Generates a Roboto font with the specified size and color, scaled to screen height.
     */
    public static BitmapFont generateRobotoFont(int baseSize, Color color, Skin fallbackSkin) {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Roboto-Regular.ttf")
            );
            // Scale font size based on screen height
            float scaleFactor = Gdx.graphics.getHeight() / 1080f; // Reference height of 1080p
            int scaledSize = Math.max(12, (int)(baseSize * scaleFactor)); //12px minimum size
            
            FreeTypeFontParameter param = new FreeTypeFontParameter();
            param.size = scaledSize;
            param.color = color;
            
            BitmapFont font = generator.generateFont(param);
            generator.dispose();
            
            return font;
        } catch (Exception e) {
            System.err.println("Failed to load Roboto font (size " + baseSize + "), using default");
            e.printStackTrace();
            return fallbackSkin.getFont("default-font");
        }
    }
}