package io.github.maze11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.messages.CoffeeCollectMessage;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.systemTypes.FixedStepper;
import io.github.maze11.systems.CollectableSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.TimerRendererSystem;
import io.github.maze11.systems.TimerSystem;
import io.github.maze11.systems.physics.PhysicsSyncSystem;
import io.github.maze11.systems.physics.PhysicsSystem;
import io.github.maze11.systems.physics.PhysicsToTransformSystem;
import io.github.maze11.systems.rendering.RenderingSystem;
import io.github.maze11.systems.rendering.WorldCameraSystem;

public class LevelScreen implements Screen {
    private final MazeGame game;
    private final PooledEngine engine;
    private final TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final FitViewport viewport;
    private final BitmapFont defaultFont;

    // box2d debug renderer
    private final Box2DDebugRenderer debugRenderer;
    private boolean showDebugRenderer = true;
    private final FixedStepper fixedStepper;
    private final MessagePublisher messagePublisher;

    private Entity timerEntity; // entity that holds the timer
    private TimerRendererSystem timerRendererSystem; // system to render the time



    public LevelScreen(MazeGame game) {
        this.game = game;

        // Create rendering singletons
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(16, 12, camera);
        map = game.getAssets().get(AssetId.TILEMAP, TiledMap.class); // Load the map using AssetManager

        //create the font
        defaultFont = new BitmapFont();
        defaultFont.setUseIntegerPositions(false);
        //scale the font to the viewport
        defaultFont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        //create the engine
        engine = new PooledEngine();
        fixedStepper = new FixedStepper();

        messagePublisher = new MessagePublisher();
        EntityMaker entityMaker = new EntityMaker(engine, game);

        // input -> sync -> physics -> render (for no input delay)
        engine.addSystem(new CollectableSystem(messagePublisher, engine, entityMaker));
        engine.addSystem(new PlayerSystem(fixedStepper, messagePublisher)); // player input system
        engine.addSystem(new PhysicsSyncSystem(fixedStepper)); // sync transform to physics bodies
        engine.addSystem(new PhysicsSystem(fixedStepper, messagePublisher)); // run physics simulation
        engine.addSystem(new PhysicsToTransformSystem(fixedStepper)); // sync physics to transform
        engine.addSystem(new WorldCameraSystem(camera, game.getBatch()));
        engine.addSystem(new RenderingSystem(game).startDebugView()); // rendering system
        engine.addSystem(new TimerSystem()); // add Timer System to update timers



        timerRendererSystem = new TimerRendererSystem(game); // initialise timerRenderingSystem
        engine.addSystem(timerRendererSystem); // add to system

        // create walls from tiled layer
        createWallCollisions();

        // Populate the world with objects
        entityMaker.makeCollectable(6f, 10f, new CoffeeCollectMessage(), AssetId.COFFEE);
        entityMaker.makePlayer(4f, 4f);


        // Create 5-minute timer
        timerEntity = entityMaker.makeTimer(300f);

        debugRenderer = new Box2DDebugRenderer();
    }



    @Override
    public void show() {
        float unitScale = 1f / 32f;
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
        System.out.println("Level screen began displaying");
    }

    @Override
    public void render(float deltaTime) {

        viewport.apply();

        // Render the Tiled map
        mapRenderer.setView((OrthographicCamera) viewport.getCamera());

        var batch = game.getBatch();

        batch.begin();
        // ######### START RENDER #############
        ScreenUtils.clear(Color.BLACK);

         mapRenderer.render(new int[] { 0 });
        fixedStepper.advanceSimulation(deltaTime);
        engine.update(deltaTime);

        // ######## END RENDER ###############
        batch.end();

         mapRenderer.render(new int[] { 1 });
         
        // render timer UI after main batch
        timerRendererSystem.renderTimer();

        viewport.apply();

        //  render Box2D debug outlines
        if (showDebugRenderer) {
            var physicsSystem = engine.getSystem(PhysicsSystem.class);
            if (physicsSystem != null) {
                debugRenderer.render(physicsSystem.getWorld(), viewport.getCamera().combined);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        //if size is 0, it is minimised, no need to resize
        if(width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
        timerRendererSystem.resize(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * Creates wall entities from the Tiled "Collisions" object layer.
     * Each wall is now a proper entity in the ECS instead of just a Box2D body.
     */
    private void createWallCollisions() {
        var wallsLayer = map.getLayers().get("Collisions");
        if (wallsLayer != null) {
            System.out.println("Found Collisions object layer, creating wall entities");

            EntityMaker entityMaker = new EntityMaker(engine, game);

            for (MapObject object : wallsLayer.getObjects()) {
                if (object instanceof RectangleMapObject rectangleMapObject) {
                    Rectangle rect = rectangleMapObject.getRectangle();
                    int pixelsToUnit = MazeGame.PIXELS_TO_UNIT;

                    float x = rect.x / pixelsToUnit;
                    float y = rect.y / pixelsToUnit;
                    float width = rect.width / pixelsToUnit;
                    float height = rect.height / pixelsToUnit;

                    // Create a wall entity instead of directly creating Box2D body
                    entityMaker.makeWall(x, y, width, height);
                }
            }
        } else {
            System.out.println("Warning: No 'Collisions' object layer found in map!");
        }
    }



    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        defaultFont.dispose();

        // dispose debug renderer and physics world
        var physicsSystem = engine.getSystem(PhysicsSystem.class);
        if (physicsSystem != null) {
            physicsSystem.getWorld().dispose();
        }

        if (debugRenderer != null) {
            debugRenderer.dispose();
        }
    }
}
