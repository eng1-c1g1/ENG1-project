package io.github.maze11;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.systems.PhysicsSyncSystem;
import io.github.maze11.systems.PhysicsSystem;
import io.github.maze11.systems.PhysicsToTransformSystem;
import io.github.maze11.systems.PlayerSystem;
import io.github.maze11.systems.RenderingSystem;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public class LevelScreen implements Screen {
    private final MazeGame game;
    private final PooledEngine engine;
    private final TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    // box2d debug renderer
    private Box2DDebugRenderer debugRenderer;
    private boolean showDebugRenderer = true; 

    public LevelScreen(MazeGame game) {
        this.game = game;

        map = game.getAssets().get(AssetId.Tilemap, TiledMap.class); // Load the map using AssetManager

        engine = new PooledEngine();

        // input -> sync -> physics -> render (for no input delay)
        engine.addSystem(new PlayerSystem()); // player input system
        engine.addSystem(new PhysicsSyncSystem()); // sync transform to physics bodies
        engine.addSystem(new PhysicsSystem()); // run physics simulation
        engine.addSystem(new PhysicsToTransformSystem()); // sync physics to transform
        engine.addSystem(new RenderingSystem(game).startDebugView()); // rendering system

        // create walls from tiled layer
        createWallCollisions();

        EntityMaker entityMaker = new EntityMaker(engine, game);
        // Temporary debugging code to create objects here
        var debugManager = new DebuggingIndicatorManager(engine, game);
        debugManager.createDebugSquare(1,1);
        debugManager.createDebugSquare(1.5f,1.5f);
        debugManager.createDebugSquare(3f, 3f, 2f, 2f);
        entityMaker.makePlayer(4f, 4f);

        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void show() {
        float unitScale = 1f / 32f;
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
        System.out.println("Level screen began displaying");
    }

    @Override
    public void render(float delta) {

        var viewport = game.getViewport();
        viewport.apply();

        // Render the Tiled map
        mapRenderer.setView((OrthographicCamera) viewport.getCamera());

        var batch = game.getBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        // ######### START RENDER #############
        ScreenUtils.clear(Color.BLACK);

        mapRenderer.render();
        engine.update(delta);
        game.getDefaultFont().draw(batch, "Tiled floor level loaded!", 1, 1.5f);

        // ######## END RENDER ###############
        batch.end();

        //  render Box2D debug outliness 
        if (showDebugRenderer) {
            var physicsSystem = engine.getSystem(PhysicsSystem.class);
            if (physicsSystem != null) {
                debugRenderer.render(physicsSystem.getWorld(), game.getViewport().getCamera().combined);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        //if size is 0, it is minimised, no need to resize
        if(width <= 0 || height <= 0) return;

        game.getViewport().update(width, height, true);
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
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject)object).getRectangle();
                    // Convert to world units (divide by 32)
                    float x = rect.x / 32f;
                    float y = rect.y / 32f;
                    float width = rect.width / 32f;
                    float height = rect.height / 32f;
                    
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
