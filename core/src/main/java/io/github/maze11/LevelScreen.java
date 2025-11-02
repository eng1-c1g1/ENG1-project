package io.github.maze11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.systemTypes.FixedStepper;
import io.github.maze11.systems.GooseSystem;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.TimerRendererSystem;
import io.github.maze11.systems.TimerSystem;
import io.github.maze11.systems.physics.PhysicsSyncSystem;
import io.github.maze11.systems.physics.PhysicsSystem;
import io.github.maze11.systems.physics.PhysicsToTransformSystem;
import io.github.maze11.systems.physics.SafeBodyDestroy;
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
    private final boolean showDebugRenderer = true;

    private final FixedStepper fixedStepper;
    private final MessagePublisher messagePublisher;

    private static final ComponentMapper<PhysicsComponent> physicsMapper =
        ComponentMapper.getFor(PhysicsComponent.class);

    private Entity timerEntity; // entity that holds the timer
    private TimerRendererSystem timerRendererSystem; // system to render the time

    public LevelScreen(MazeGame game) {
        this.game = game;

        // Create rendering singletons
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(16, 12, camera);
        map = game.getAssetLoader().get(AssetId.TILEMAP, TiledMap.class); // Load the map using AssetManager

        // create the font
        defaultFont = new BitmapFont();
        defaultFont.setUseIntegerPositions(false);
        // scale the font to the viewport
        defaultFont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        // create the engine
        engine = new PooledEngine();
        fixedStepper = new FixedStepper();

        messagePublisher = new MessagePublisher();
        EntityMaker entityMaker = new EntityMaker(engine, game);

        // Systems that need to be referenced later
        GooseSystem gooseSystem = new GooseSystem(fixedStepper, messagePublisher);
        timerRendererSystem = new TimerRendererSystem(game);

        // input -> sync -> physics -> render (for no input delay)
        List<EntitySystem> systems = List.of(
            new InteractableSystem(messagePublisher, engine, entityMaker),
            gooseSystem,
            new PlayerSystem(fixedStepper, messagePublisher),
            new PhysicsSyncSystem(fixedStepper),
            new PhysicsSystem(fixedStepper, messagePublisher),
            new PhysicsToTransformSystem(fixedStepper),
            new WorldCameraSystem(camera, game.getBatch()),
            new RenderingSystem(game).startDebugView(),
            new TimerSystem(),
            timerRendererSystem
        );

        // Add them to the engine in order
        for (EntitySystem system : systems) {
            engine.addSystem(system);
        }

        registerPhysicsCleanupListener(); // register listener to destroy physics bodies on entity removal

        // Populate the world with objects
        // create walls and entities from tiled layer
        Map<String, List<Entity>> entities = extractEntities(entityMaker);
        
        // Extract player
        List<Entity> players = entities.get("player");
        Entity player;
        if (players == null || players.isEmpty()) {
            player = null;
        } else {
            player = players.get(0);
        }

        // Create 5-minute timer
        timerEntity = entityMaker.makeTimer(300f);
        
        // Give geese a reference to the player, now that the player has been created
        gooseSystem.setTarget(player);

        debugRenderer = new Box2DDebugRenderer();
    }

    /**
     * Creates wall entities from the Tiled "Collisions" object layer and other
     * entities from the "Entities" layer.
     * Each wall is now a proper entity in the ECS instead of just a Box2D body.
     */
    private Map<String, List<Entity>> extractEntities(EntityMaker entityMaker) {
        int pixelsToUnit = MazeGame.PIXELS_TO_UNIT;

        Map<String, List<Entity>> entityGroups = new HashMap<>();

        // "Collisions" Layer (identifies every object in the "Collisions" layer)
        var wallsLayer = map.getLayers().get("Collisions");
        if (wallsLayer != null) {
            System.out.println("Found 'Collisions' object layer, creating wall entities");

            for (MapObject object : wallsLayer.getObjects()) {
                if (object instanceof RectangleMapObject rectangleMapObject) {
                    Rectangle rect = rectangleMapObject.getRectangle();
                    float x = rect.x / pixelsToUnit;
                    float y = rect.y / pixelsToUnit;
                    float width = rect.width / pixelsToUnit;
                    float height = rect.height / pixelsToUnit;

                    Entity wall = entityMaker.makeWall(x, y, width, height);
                    entityGroups.computeIfAbsent("wall", k -> new ArrayList<>()).add(wall);
                }
            }
        } else {
            System.out.println("Warning: No 'Collisions' object layer found in map!");
        }

        // "Entities" Layer (identifies entities from 'class' in the Tiled map)
        var entitiesLayer = map.getLayers().get("Entities");
        if (entitiesLayer != null) {
            System.out.println("Found 'Entities' object layer, creating game entities");

            for (MapObject object : entitiesLayer.getObjects()) {
                if (!(object instanceof RectangleMapObject rectObj))
                    continue;

                Rectangle rect = rectObj.getRectangle();
                float x = rect.x / pixelsToUnit;
                float y = rect.y / pixelsToUnit;

                MapProperties properties = object.getProperties();

                String className = properties.get("type", String.class);
                if (className == null || className.isEmpty()) {
                    System.out.println("Skipping unnamed object in 'Entities' layer");
                    continue;
                }

                Entity entity = null;
                switch (className.toLowerCase()) {
                    case "player" -> entity = entityMaker.makePlayer(x, y);
                    case "goose" -> entity = entityMaker.makeGoose(x, y);
                    case "coffee" -> entity = entityMaker.makeCoffee(x, y);
                    case "wall" -> {
                        float width = rect.width / pixelsToUnit;
                        float height = rect.height / pixelsToUnit;
                        entity = entityMaker.makeWall(x, y, width, height);
                    }
                    default -> System.out.println("Unknown entity class: " + className);
                }

                if (entity != null) {
                    entityGroups.computeIfAbsent(className.toLowerCase(), k -> new ArrayList<>()).add(entity);
                }
            }
        } else {
            System.out.println("Warning: No 'Entities' object layer found in map!");
        }

        return entityGroups;
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

        // render Box2D debug outlines
        if (showDebugRenderer) {
            var physicsSystem = engine.getSystem(PhysicsSystem.class);
            if (physicsSystem != null) {
                debugRenderer.render(physicsSystem.getWorld(), viewport.getCamera().combined);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // if size is 0, it is minimised, no need to resize
        if (width <= 0 || height <= 0)
            return;

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

    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        defaultFont.dispose();

        // dispose debug renderer and physics world
        var physicsSystem = engine.getSystem(PhysicsSystem.class);
        if (physicsSystem != null) {
            engine.removeSystem(physicsSystem); // triggers removedFromEngine which drains SafeBodyDestroy
        }

        if (debugRenderer != null) {
            debugRenderer.dispose();
        }
    }

    /**
     * Registers a listener that queues Box2d bodies for destruction
     * when their associated entities are removed from the engine.
     */
    private void registerPhysicsCleanupListener() {
        engine.addEntityListener(Family.all(PhysicsComponent.class).get(),
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {
                        // No action needed on addition
                    }

                    @Override
                    public void entityRemoved(Entity entity) {
                        PhysicsComponent physicsComp = physicsMapper.get(entity);
                        if (physicsComp != null && physicsComp.body != null) {
                            SafeBodyDestroy.request(physicsComp.body); // Queue body for destruction
                            physicsComp.body = null; // Clear reference in component
                        }
                    }
                });
    }
}
