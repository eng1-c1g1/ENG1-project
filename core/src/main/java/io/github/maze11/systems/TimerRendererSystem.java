package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.maze11.MazeGame;
import io.github.maze11.components.TimerComponent;

/**
 * Renders timer text with a separate UI viewport.
 * This system is responsible for drawing the timer countdown on the screen.
 */
public class TimerRendererSystem extends IteratingSystem {
    private final ComponentMapper<TimerComponent> timerM = ComponentMapper.getFor(TimerComponent.class);
    private final SpriteBatch uiBatch; // Separate batch for UI rendering
    private final BitmapFont font; // Font for displaying text
    private final ScreenViewport uiViewport; // Viewport for the UI

    // Constructor to initialize the rendering system
    public TimerRendererSystem(MazeGame game) {
        super(Family.all(TimerComponent.class).get());
        this.uiBatch = new SpriteBatch(); // Create a new batch just for UI
        this.uiViewport = new ScreenViewport();
        
        this.font = new BitmapFont(); // Initialize font
        this.font.getData().setScale(2.5f); // Set font size
        this.font.setColor(Color.WHITE); // Set font color
    }

    @Override
    public void update(float deltaTime) {
        // Don't render here - render in renderTimer() which is called separately
    }

    /**
     * Call this method to render the timer (after main batch.end())
     */
    public void renderTimer() {
        // Iterate through all entities with a TimerComponent
        for (Entity entity : getEntities()) {
            TimerComponent timer = timerM.get(entity); // Get the TimerComponent
            renderTimerText(timer); // Render the timer text
        }
    }

    // Method to render the timer countdown text
    private void renderTimerText(TimerComponent timer) {
        uiViewport.apply(); // Apply the UI viewport settings
        
        float screenWidth = uiViewport.getWorldWidth();
        float screenHeight = uiViewport.getWorldHeight();
        
        // Format time remaining as MM:SS
        int minutes = (int) (timer.timeRemaining / 60);
        int seconds = (int) (timer.timeRemaining % 60);
        String timeText = String.format("%02d:%02d", minutes, seconds);
        
        // Draw Timer Text at top-center of screen using separate UI batch
        uiBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        uiBatch.begin();
        
        // Calculate text position to center it
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, timeText);
        float textX = (screenWidth - layout.width) / 2f; // Center horizontally
        float textY = screenHeight - 40f; // Position near top
        
        font.draw(uiBatch, timeText, textX, textY); // Draw the timer text
        
        uiBatch.end();
    }
    
    // Method to resize the UI viewport
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Not used
    }
}