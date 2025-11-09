package io.github.maze11.systems.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.maze11.MazeGame;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.components.AnimationComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TimerComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.messages.Message;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.MessageType;
import io.github.maze11.messages.ToastMessage;

public class RenderingSystem extends SortedIteratingSystem {

    private final MazeGame game;

    private final OrthographicCamera worldCamera;
    private final ScreenViewport uiViewport;

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch worldBatch;
    private final SpriteBatch uiBatch;

    private final ShapeRenderer shapeRenderer;

    private boolean isDebugging = false;
    private Box2DDebugRenderer debugRenderer = null;
    private World debugWorld = null;
    private Texture originTexture = null;

    private final BitmapFont timerFont;
    private final BitmapFont toastFont;

    private final ComponentMapper<SpriteComponent> spriteM;
    private final ComponentMapper<TransformComponent> transformM;
    private final ComponentMapper<AnimationComponent> animM;
    private final ComponentMapper<TimerComponent> timerM;

    private final MessageListener messageListener;

    private String toastText = null;
    private float toastTimeRemaining = 0f;
    private float toastOffsetY = 0f;

    public RenderingSystem(MazeGame game, OrthographicCamera worldCamera, TiledMap map, MessagePublisher publisher) {
        super(Family.all(SpriteComponent.class, TransformComponent.class).get(), new RenderOrderComparator());

        this.game = game;
        this.worldCamera = worldCamera;

        this.worldBatch = game.getBatch();
        this.uiBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.uiViewport = new ScreenViewport();

        float unitScale = 1f / 32f;
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale, worldBatch);

        this.timerFont = new BitmapFont();
        timerFont.setUseIntegerPositions(false);
        timerFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        this.toastFont = new BitmapFont();
        toastFont.setUseIntegerPositions(false);
        toastFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        spriteM = ComponentMapper.getFor(SpriteComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);
        animM = ComponentMapper.getFor(AnimationComponent.class);
        timerM = ComponentMapper.getFor(TimerComponent.class);

        this.messageListener = new MessageListener(publisher);
    }

    public void enableDebugging(World debugWorld) {
        this.isDebugging = true;
        this.debugRenderer = new Box2DDebugRenderer();
        this.debugWorld = debugWorld;
        this.originTexture = game.getAssetLoader().get(AssetId.ORIGIN_INDICATOR, Texture.class);
    }

    @Override
    public void update(float deltaTime) {
        consumeMessages(deltaTime);

        forceSort();

        worldCamera.update();
        mapRenderer.setView(worldCamera);

        // Background
        mapRenderer.render(new int[] { 0 });

        // World entities
        worldBatch.setProjectionMatrix(worldCamera.combined);
        worldBatch.begin();
        super.update(deltaTime);
        worldBatch.end();

        // Foreground
        mapRenderer.render(new int[] { 1 });

        // Debug
        if (isDebugging) {
            debugRenderer.render(this.debugWorld, worldCamera.combined);
        }

        // UI
        renderUI();
    }

    private void consumeMessages(float deltaTime) {
        while (messageListener.hasNext()) {
            Message m = messageListener.next();
            if (m.type == MessageType.TOAST_SHOW) {
                ToastMessage t = (ToastMessage) m;
                toastText = t.text;
                toastTimeRemaining = t.duration;

                toastOffsetY = 20f;
            }
        }

        if (toastTimeRemaining > 0f) {
            toastTimeRemaining -= deltaTime;

            toastOffsetY *= 0.92f;

            if (toastTimeRemaining < 0f)
                toastTimeRemaining = 0f;
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteComponent sprite = spriteM.get(entity);
        TransformComponent transform = transformM.get(entity);
        if (!sprite.isShown)
            return;

        TextureRegion region;
        AnimationComponent anim = animM.get(entity);
        if (anim != null && anim.currentFrame != null) {
            region = anim.currentFrame;
        } else {
            region = new TextureRegion(sprite.texture);
        }

        float width = sprite.size.x * transform.scale.x;
        float height = sprite.size.y * transform.scale.y;
        float xOffset = -0.5f * width;

        worldBatch.draw(
                region,
                transform.position.x + sprite.textureOffset.x + xOffset,
                transform.position.y + sprite.textureOffset.y,
                width,
                height);

        if (isDebugging) worldBatch.draw(originTexture, transform.position.x - 0.5f, transform.position.y - 0.5f, 1f, 1f);
    }

    private void renderUI() {
        uiViewport.apply();
        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        uiBatch.setProjectionMatrix(uiViewport.getCamera().combined);

        ImmutableArray<Entity> timers = getEngine().getEntitiesFor(Family.all(TimerComponent.class).get());
        if (timers.size() == 0)
            return;

        Entity timerEntity = timers.first();
        TimerComponent timer = timerM.get(timerEntity);

        float w = uiViewport.getWorldWidth();
        float h = uiViewport.getWorldHeight();

        float centerX = w * 0.5f;
        float centerY = h - 60f;
        float radius = 50f;

        float progress = timer.timeRemaining / timer.totalTime;
        Color barColor = getTimerColor(progress);

        // Timer background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.7f);
        shapeRenderer.circle(centerX, centerY, radius);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(barColor);
        float arcAngle = progress * 360f;
        shapeRenderer.arc(centerX, centerY, radius - 5f, 90f, arcAngle, 30);
        shapeRenderer.end();

        // Timer ring
        Gdx.gl.glLineWidth(3);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.circle(centerX, centerY, radius);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);

        drawToastBackground();

        // Scale fonts relative to current viewport
        timerFont.getData().setScale(2f * h / Gdx.graphics.getHeight());
        toastFont.getData().setScale(2f * h / Gdx.graphics.getHeight());

        uiBatch.begin();

        // Timer text
        int minutes = (int) (timer.timeRemaining / 60);
        int seconds = (int) (timer.timeRemaining % 60);
        String timeText = String.format("%02d:%02d", minutes, seconds);
        GlyphLayout timeLayout = new GlyphLayout(timerFont, timeText);
        timerFont.draw(uiBatch, timeLayout, centerX - timeLayout.width * 0.5f, centerY + timeLayout.height * 0.5f);

        drawToastText();

        uiBatch.end();
    }

    // ---------- Toast helpers ----------
    private void drawToastBackground() {
        if (toastTimeRemaining <= 0f || toastText == null)
            return;

        float alpha = (toastTimeRemaining > 0.3f) ? 1f : (toastTimeRemaining / 0.3f);

        float w = uiViewport.getWorldWidth();
        float h = uiViewport.getWorldHeight();

        GlyphLayout layout = new GlyphLayout(toastFont, toastText);

        float x = (w - layout.width) * 0.5f;
        float y = h - 140f - toastOffsetY;

        float padding = 28f;
        float bgWidth = layout.width + padding * 2f;
        float bgHeight = layout.height + padding * 1.5f;

        // Rounded capsule radius
        float radius = bgHeight * 0.5f;

        float bgX = x - padding;
        float bgY = y - layout.height - padding * 0.75f;

        // Background color
        float r = 0.1f, g = 0.1f, b = 0.1f, a = 0.85f * alpha;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(r, g, b, a);

        // Center rectangle
        shapeRenderer.rect(
                bgX + radius,
                bgY,
                bgWidth - 2 * radius,
                bgHeight
        );

        // Left cap
        shapeRenderer.arc(
                bgX + radius,
                bgY + radius,
                radius,
                90,
                180
        );

        // Right cap
        shapeRenderer.arc(
                bgX + bgWidth - radius,
                bgY + radius,
                radius,
                270,
                180);

        shapeRenderer.end();
    }

    private void drawToastText() {
        if (toastTimeRemaining <= 0f || toastText == null)
            return;

        float alpha = (toastTimeRemaining > 0.3f) ? 1f : (toastTimeRemaining / 0.3f);

        float w = uiViewport.getWorldWidth();
        float h = uiViewport.getWorldHeight();

        GlyphLayout layout = new GlyphLayout(toastFont, toastText);

        float x = (w - layout.width) * 0.5f;
        float y = h - 140f - toastOffsetY;

        // Shadow
        toastFont.setColor(0f, 0f, 0f, 0.5f * alpha);
        toastFont.draw(uiBatch, layout, x + 2f, y - 2f);

        // Text
        toastFont.setColor(1f, 1f, 0.9f, alpha);
        toastFont.draw(uiBatch, layout, x, y);
    }

    private Color getTimerColor(float progress) {
        Color green = new Color(0.2f, 0.8f, 0.2f, 1f);
        Color yellow = new Color(1f, 0.8f, 0f, 1f);
        Color red = new Color(1f, 0.2f, 0.2f, 1f);

        if (progress > 0.5f) {
            float t = (progress - 0.5f) / 0.5f;
            return green.cpy().lerp(yellow, 1f - t);
        } else {
            float t = progress / 0.5f;
            return yellow.cpy().lerp(red, 1f - t);
        }
    }

    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    public void dispose() {
        if (isDebugging) {
            debugRenderer.dispose();
        }
    }
}