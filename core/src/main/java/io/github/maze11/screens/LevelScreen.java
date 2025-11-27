package io.github.maze11.screens;

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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.maze11.MazeGame;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.components.PhysicsComponent;
import io.github.maze11.factory.EntityMaker;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.ToastMessage;
import io.github.maze11.systems.AudioSystem;
import io.github.maze11.systems.GooseSystem;
import io.github.maze11.systems.HiddenWallSystem;
import io.github.maze11.systems.InteractableSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.TimerSystem;
import io.github.maze11.systems.gameState.GameStateSystem;
import io.github.maze11.systems.physics.PhysicsSyncSystem;
import io.github.maze11.systems.physics.PhysicsSystem;
import io.github.maze11.systems.physics.PhysicsToTransformSystem;
import io.github.maze11.systems.physics.SafeBodyDestroy;
import io.github.maze11.systems.rendering.RenderingSystem;
import io.github.maze11.systems.rendering.WorldCameraSystem;

/**
 * The screen displayed when the game is running.
 * This is where the world and player can be seen, and most the time is spent by the player.
 */
public class LevelScreen implements Screen {
    private final PooledEngine engine;

    private final TiledMap map;
    private final FitViewport viewport;
    private final OrthographicCamera camera;

    private final FixedStepper fixedStepper;
    private final MessagePublisher messagePublisher;

    private final boolean isDebugging = false;

    public LevelScreen(MazeGame game) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(16, 12, camera);

        map = game.getAssetLoader().get(AssetId.TILEMAP, TiledMap.class);

        engine = new PooledEngine();
        fixedStepper = new FixedStepper();
        messagePublisher = new MessagePublisher();

        EntityMaker entityMaker = new EntityMaker(engine, game);

        GooseSystem gooseSystem = new GooseSystem(fixedStepper, messagePublisher);
        RenderingSystem renderingSystem = new RenderingSystem(game, camera, map, messagePublisher);

        // Game conditions (win/lose) -> Input -> Sync & Physics -> render (for no input delay)
        List<EntitySystem> systems = List.of(
                new GameStateSystem(messagePublisher, game, engine),
                new InteractableSystem(messagePublisher, engine, entityMaker),
                gooseSystem,
                new PlayerSystem(fixedStepper, messagePublisher, game),
                new HiddenWallSystem(fixedStepper, messagePublisher),
                new PhysicsSyncSystem(fixedStepper),
                new PhysicsSystem(fixedStepper, messagePublisher),
                new PhysicsToTransformSystem(fixedStepper),
                new AudioSystem(engine, messagePublisher, game),
                new WorldCameraSystem(camera, game.getBatch()),
                new TimerSystem(messagePublisher),
                renderingSystem);

        for (EntitySystem system : systems) {
            engine.addSystem(system);
        }

        PhysicsSystem physicsSystem = engine.getSystem(PhysicsSystem.class);
        if (isDebugging) {
            World debugWorld = physicsSystem.getWorld();
            renderingSystem.enableDebugging(debugWorld);
        }

        registerPhysicsCleanupListener();

        Map<String, List<Entity>> entities = extractEntities(entityMaker);

        // Extract player
        Entity player = entities.getOrDefault("player", List.of()).stream().findFirst().orElse(null);
        gooseSystem.setTarget(player);

        // Create timer (5 minutes = 300 seconds)
        entityMaker.makeTimer(300f);

        this.welcomeToasts(messagePublisher);

        System.out.println("Level Screen created.");
    }

    private void welcomeToasts(MessagePublisher messagePublisher) {
        ToastMessage[] toasts = {
                new ToastMessage("Welcome to the maze!\nUse Arrow Keys or WASD to move.", 10f),
                new ToastMessage("Escape the maze as fast as possible\nand avoid the geese.", 10f),
                new ToastMessage("Collect coffee for extra speed and\ncheck-in codes for extra points, good luck!", 5f),
        };

        float startDelay = 2f;
        float gap = 4f;

        for (int i = 0; i < toasts.length; i++) {
            final ToastMessage msg = toasts[i];
            float delay = startDelay + i * gap;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    messagePublisher.publish(msg);
                }
            }, delay);
        }
    }

    private Map<String, List<Entity>> extractEntities(EntityMaker entityMaker) {
        int pixelsToUnit = MazeGame.PIXELS_TO_UNIT;

        Map<String, List<Entity>> groups = new HashMap<>();

        // Walls (Collisions layer)
        var wallsLayer = map.getLayers().get("Collisions");
        if (wallsLayer != null) {
            for (MapObject obj : wallsLayer.getObjects()) {
                if (obj instanceof RectangleMapObject rectObj) {
                    var rect = rectObj.getRectangle();

                    float x = rect.x / pixelsToUnit;
                    float y = rect.y / pixelsToUnit;
                    float w = rect.width / pixelsToUnit;
                    float h = rect.height / pixelsToUnit;

                    Entity wall = entityMaker.makeWall(x, y, w, h);
                    groups.computeIfAbsent("wall", k -> new ArrayList<>()).add(wall);
                }
            }
        }

        // Game Entities (Entities layer)
        var entitiesLayer = map.getLayers().get("Entities");
        if (entitiesLayer != null) {
            for (MapObject obj : entitiesLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject rectObj))
                    continue;

                var rect = rectObj.getRectangle();
                float x = rect.x / pixelsToUnit;
                float y = rect.y / pixelsToUnit;

                String type = obj.getProperties().get("type", String.class);
                if (type == null)
                    continue;

                if (isDebugging) {
                    System.out.println("=== Entity Properties ===");
                    var props = obj.getProperties();

                    var keys = props.getKeys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        Object value = props.get(key);
                        System.out.printf(" • %-20s : %s%n", key, value);
                    }
                }

                Entity e = switch (type.toLowerCase()) {
                    case "player" -> entityMaker.makePlayer(x, y);
                    case "goose" -> entityMaker.makeGoose(x, y);
                    case "coffee" -> entityMaker.makeCoffee(x, y);
                    case "puddle" -> entityMaker.makePuddle(x, y);
                    case "check-in" -> entityMaker.makeCheckInCode(x, y);
                    case "exit" -> entityMaker.makeExit(x, y);
                    case "pressure-plate" -> {
                        String triggers = obj.getProperties().get("triggers", String.class);
                        yield entityMaker.makePressurePlate(x, y, triggers);
                    }
                    case "false-wall" -> {
                        float w = rect.width / pixelsToUnit;
                        float h = rect.height / pixelsToUnit;
                        String triggeredBy = obj.getProperties().get("triggeredBy", String.class);
                        yield entityMaker.makeFalseWall(x, y, w, h, triggeredBy);
                    }
                    case "wall" -> {
                        float w = rect.width / pixelsToUnit;
                        float h = rect.height / pixelsToUnit;
                        yield entityMaker.makeWall(x, y, w, h);
                    }
                    default -> null;
                };

                if (e != null) {
                    groups.computeIfAbsent(type, k -> new ArrayList<>()).add(e);
                }
            }
        }

        return groups;
    }

    @Override
    public void render(float deltaTime) {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();

        fixedStepper.advanceSimulation(deltaTime);
        engine.update(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0)
            return;

        viewport.update(width, height, true);

        RenderingSystem renderSys = engine.getSystem(RenderingSystem.class);
        if (renderSys != null) {
            renderSys.resize(width, height);
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        engine.removeAllEntities(); // Clean up entities first to trigger physics body removal
        var phys = engine.getSystem(PhysicsSystem.class);
        var audio = engine.getSystem(AudioSystem.class);

        if (phys != null) {
            engine.removeSystem(phys);
        }
        if (audio != null) {
            audio.dispose();
        }

        engine.removeAllEntities();
        System.out.println("Level screen disposed");
    }

    private void registerPhysicsCleanupListener() {
        ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        engine.addEntityListener(
                Family.all(PhysicsComponent.class).get(),
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity e) {
                    }

                    @Override
                    public void entityRemoved(Entity e) {
                        PhysicsComponent phys = physicsMapper.get(e);
                        if (phys != null && phys.body != null) {
                            SafeBodyDestroy.request(phys.body);
                            phys.body = null;
                        }
                    }
                });
    }
}
