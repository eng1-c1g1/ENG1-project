package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.maze11.MazeGame;
import io.github.maze11.components.TimerComponent;

/**
 * Renders timer text with circular progress bar centered at top of screen.
 * Features smooth color transitions from green → yellow → red.
 */
public class TimerRendererSystem extends IteratingSystem {
    private final ComponentMapper<TimerComponent> timerM = ComponentMapper.getFor(TimerComponent.class);
    private final SpriteBatch uiBatch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final ScreenViewport uiViewport;

    public TimerRendererSystem(MazeGame game) {
        super(Family.all(TimerComponent.class).get());
        this.uiBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.uiViewport = new ScreenViewport();
        this.font = createHDFont();
    }
    
    private BitmapFont createHDFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/Roboto-Regular.ttf")
        );
        
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.5f;
        parameter.borderColor = new Color(0, 0, 0, 0.5f);
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = new Color(0, 0, 0, 0.5f);
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    @Override
    public void update(float deltaTime) {
        // Rendering happens in renderTimer()
    }

    public void renderTimer() {
        for (Entity entity : getEntities()) {
            TimerComponent timer = timerM.get(entity);
            renderTimerUI(timer);
        }
    }

    private void renderTimerUI(TimerComponent timer) {
        uiViewport.apply();
        
        float screenWidth = uiViewport.getWorldWidth();
        float screenHeight = uiViewport.getWorldHeight();
        
        // Centered at top of screen
        float centerX = screenWidth / 2f;
        float centerY = screenHeight - 80f;
        float radius = 50f;
        
        // Format time
        int minutes = (int) (timer.timeRemaining / 60);
        int seconds = (int) (timer.timeRemaining % 60);
        String timeText = String.format("%02d:%02d", minutes, seconds);
        
        // Calculate progress
        float progress = timer.timeRemaining / timer.totalTime;
        Color barColor = getTimerColor(progress);
        
        // === Draw Circular Progress Bar ===
        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        
        // Background circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.7f);
        shapeRenderer.circle(centerX, centerY, radius);
        shapeRenderer.end();
        
        // Progress arc
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(barColor);
        float arcAngle = progress * 360f;
        shapeRenderer.arc(centerX, centerY, radius - 5f, 90f, arcAngle, 30);
        shapeRenderer.end();
        
        // Border circle
        Gdx.gl.glLineWidth(3);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.circle(centerX, centerY, radius);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
        
        // === Draw Timer Text ===
        uiBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        uiBatch.begin();
        
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, timeText);
        float textX = centerX - layout.width / 2f;
        float textY = centerY + layout.height / 2f;
        
        font.draw(uiBatch, timeText, textX, textY);
        
        uiBatch.end();
    }

    /**
     * SMOOTH color transitions: green → yellow → red
     */
    private Color getTimerColor(float progress) {
        Color green = new Color(0.2f, 0.8f, 0.2f, 1f);
        Color yellow = new Color(1f, 0.8f, 0f, 1f);
        Color red = new Color(1f, 0.2f, 0.2f, 1f);
        
        Color result = new Color();
        
        if (progress > 0.5f) {
            // Green → Yellow (100% to 50%)
            float t = (progress - 0.5f) / 0.5f;
            result.r = green.r + (yellow.r - green.r) * (1 - t);
            result.g = green.g + (yellow.g - green.g) * (1 - t);
            result.b = green.b + (yellow.b - green.b) * (1 - t);
            result.a = 1f;
        } else {
            // Yellow → Red (50% to 0%)
            float t = progress / 0.5f;
            result.r = red.r + (yellow.r - red.r) * t;
            result.g = red.g + (yellow.g - red.g) * t;
            result.b = red.b + (yellow.b - red.b) * t;
            result.a = 1f;
        }
        
        return result;
    }
    
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Not used
    }
}