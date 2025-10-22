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
 * renders timer text with a sperate UI viewport
 * this system is responsible for drawing timer countdown on screen
 */
public class TimerRendererSystem extends IteratingSystem{

    private final ComponentMapper<TimerComponent> timerM = ComponentMapper.getFor(TimerComponent.class);
    private final SpriteBatch batch; // Used for drawing 2D graphics
    private final BitmapFont font; // Font for displaying text
    private final ScreenViewport uiViewport; // Viewport for the UI

    // constructor to initialise rendering system
    
    public TimerRendererSystem(MazeGame game) {
        super(Family.all(TimerComponent.class).get());
        this.batch = game.getBatch();
        this.uiViewport = new ScreenViewport();
        
        this.font = new BitmapFont(); // Initialize font
        this.font.getData().setScale(2.5f); // Set font size
        this.font.setColor(Color.WHITE); // Set font color
    }

    @Override
    public void update(float deltaTime) {
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
        
        // Draw Timer Text at top-center of screen
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();
        
        // Calculate text position to center it
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, timeText);
        float textX = (screenWidth - layout.width) / 2f; // Center horizontally
        float textY = screenHeight - 40f; // Position near top
        
        font.draw(batch, timeText, textX, textY); // Draw the timer text
        
        batch.end();
    }

    // method to resize the UI viewpoet
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // not used 
    }

    
}
