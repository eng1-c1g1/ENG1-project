package factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import io.github.maze11.MazeGame;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.AssetLoader;
import io.github.maze11.components.AnimationComponent;
import io.github.maze11.components.AudioListenerComponent;
import io.github.maze11.components.CameraFollowComponent;
import io.github.maze11.components.GooseComponent;
import io.github.maze11.components.HiddenWallComponent;
import io.github.maze11.components.InteractableComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.components.TimerComponent;
import io.github.maze11.messages.CoffeeCollectMessage;
import io.github.maze11.messages.GooseBiteMessage;
import io.github.maze11.messages.InteractableMessage;
import io.github.maze11.messages.MessageType;
import io.github.maze11.messages.PressurePlateTriggerMessage;
import io.github.maze11.messages.SoundMessage;

/**
 * Used to create entities within a scene, within an engine
 */
public class EntityMaker {
    private final PooledEngine engine;
    private final AssetLoader assetLoader;
    private final ComponentMaker cMaker;

    public EntityMaker(PooledEngine engine, MazeGame game) {
        this.engine = engine;
        this.assetLoader = game.getAssetLoader();
        this.cMaker = new ComponentMaker(engine, assetLoader);
    }

    private Entity makeEmptyEntity() {
        Entity entity = engine.createEntity();
        engine.addEntity(entity);
        return entity;
    }

    public Entity makeFalseWall(float x, float y, float width, float height, String triggeredBy) {
        Entity entity = makeVisibleEntity(x + width / 2, y + height / 2, width, height,
                assetLoader.get(AssetId.FALSE_WALL, Texture.class));
        cMaker.addBoxCollider(entity, x, y, width, height, BodyDef.BodyType.StaticBody, false);

        HiddenWallComponent hiddenWall = engine.createComponent(HiddenWallComponent.class);
        hiddenWall.triggeredBy = triggeredBy;
        entity.add(hiddenWall);

        SpriteComponent sprite = entity.getComponent(SpriteComponent.class);
        if (sprite != null) {
            sprite.textureOffset.set(sprite.textureOffset.x, sprite.textureOffset.y - 1f);
        }

        return entity;
    }

    public Entity makePressurePlate(float x, float y, String triggers) {
        Entity entity = makeInteractable(x, y, new PressurePlateTriggerMessage(triggers), true, AssetId.PRESSURE_PLATE);
        cMaker.addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    // Creates a wall entity with box collider
    public Entity makeWall(float x, float y, float width, float height) {
        Entity entity = makeEmptyEntity();
        cMaker.addBoxCollider(entity, x + width / 2, y + height / 2, width, height,
                BodyDef.BodyType.StaticBody, false);
        return entity;
    }

    // Creates the player entity with sprite, camera follow, and physics
    public Entity makePlayer(float x, float y) {
        // Create the base entity (TransformComponent included)
        Entity entity = makeEntity(x, y);

        // Sprite (used only for size + offset)
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        sprite.size.set(1f, 2f);
        sprite.textureOffset.set(0f, 0.05f);
        sprite.texture = null; // The animation will provide the frame each render
        entity.add(sprite);

        // Player logic
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        entity.add(player);

        // Camera follows the player
        entity.add(engine.createComponent(CameraFollowComponent.class));

        // Physics
        addBoxCollider(entity, x, y, 0.9f, 0.9f,
                0f, 0.5f,
                BodyDef.BodyType.DynamicBody,
                true);

        AnimationComponent<PlayerComponent.PlayerState> anim = (AnimationComponent<PlayerComponent.PlayerState>) engine
                .createComponent(AnimationComponent.class);

        // Load player spritesheet
        Texture sheet = assetLoader.get(AssetId.PLAYER_SHEET, Texture.class);

        PlayerComponent.PlayerState[] idleStates = {
                PlayerComponent.PlayerState.IDLE_RIGHT,
                PlayerComponent.PlayerState.IDLE_UP,
                PlayerComponent.PlayerState.IDLE_LEFT,
                PlayerComponent.PlayerState.IDLE_DOWN
        };

        PlayerComponent.PlayerState[] walkStates = {
                PlayerComponent.PlayerState.WALK_RIGHT,
                PlayerComponent.PlayerState.WALK_UP,
                PlayerComponent.PlayerState.WALK_LEFT,
                PlayerComponent.PlayerState.WALK_DOWN
        };

        // Starting frame index for each direction: 0, 6, 12, 18
        int[] frameStarts = { 0, 6, 12, 18 };

        for (int i = 0; i < 4; i++) {
            // IDLE animations (row 1)
            anim.animations.put(
                    idleStates[i],
                    loadFrames(sheet, 32, 64,
                            1, frameStarts[i], frameStarts[i] + 5,
                            0.12f));

            // WALK animations (row 2)
            anim.animations.put(
                    walkStates[i],
                    loadFrames(sheet, 32, 64,
                            2, frameStarts[i], frameStarts[i] + 5,
                            0.12f));
        }

        anim.currentState = PlayerComponent.PlayerState.IDLE_DOWN;

        entity.add(anim);

        // Make the player listen for sounds
        var audioListener = new AudioListenerComponent();
        audioListener.offset.set(0f, 1f);
        entity.add(audioListener);

        return entity;
    }

    private Animation<TextureRegion> loadFrames(Texture sheet, int frameW, int frameH, int[][] coords,
            float frameTime) {

        TextureRegion[][] all = TextureRegion.split(sheet, frameW, frameH);

        TextureRegion[] selected = new TextureRegion[coords.length];

        for (int i = 0; i < coords.length; i++) {
            int row = coords[i][0];
            int col = coords[i][1];

            // Defensive: ensure coords don’t explode
            if (row < all.length && col < all[row].length)
                selected[i] = all[row][col];
            else
                selected[i] = all[0][0]; // fallback frame
        }

        return new Animation<>(frameTime, selected);
    }

    /**
     * Convenience: loads frames from a single row, using a start and end column.
     * Delegates to the main loadFrames()
     */
    private Animation<TextureRegion> loadFrames(Texture sheet, int frameW, int frameH, int row, int startCol,
            int endCol, float frameTime) {

        // Safety: cap end column to sheet width
        TextureRegion[][] all = TextureRegion.split(sheet, frameW, frameH);
        int maxCols = all[row].length - 1;
        endCol = Math.min(endCol, maxCols);

        int count = (endCol - startCol) + 1;

        int[][] coords = new int[count][];

        for (int i = 0; i < count; i++) {
            coords[i] = new int[] { row, startCol + i };
        }

        return loadFrames(sheet, frameW, frameH, coords, frameTime);
    }

    // Creates a countdown timer entity
    public Entity makeTimer(float durationSeconds) {
        Entity entity = makeEmptyEntity();

        TimerComponent timer = engine.createComponent(TimerComponent.class);
        timer.timeRemaining = durationSeconds;
        timer.totalTime = durationSeconds;
        timer.isRunning = true;
        timer.hasExpired = false;

        entity.add(timer);
        return entity;
    }

    private Entity makeInteractable(float x, float y, InteractableMessage message, boolean disappearOnInteract,
            AssetId assetId) {
        Entity entity = makeEmptyEntity();
        cMaker.addTransform(entity, x, y);
        cMaker.addSprite(entity, assetId);
        cMaker.addInteractable(entity, message, disappearOnInteract);

        return entity;
    }

    public Entity makeCoffee(float x, float y) {
        Entity entity = makeInteractable(x, y, new CoffeeCollectMessage(), true, AssetId.COFFEE);
        cMaker.addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    public Entity makeCheckInCode(float x, float y) {
        Entity entity = makeInteractable(x, y, new InteractableMessage(MessageType.CHECK_IN_CODE_COLLECT), true,
                AssetId.CHECK_IN);
        cMaker.addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    public Entity makeExit(float x, float y) {
        Entity entity = makeInteractable(x, y, new InteractableMessage(MessageType.EXIT_MAZE), false, AssetId.EXIT);
        cMaker.addBoxCollider(entity, x, y, 1f, 1f, 0f, 0.5f,
            BodyDef.BodyType.StaticBody, true);
        return entity;
    }

    public Entity makeGoose(float x, float y) {
        Entity entity = makeEntity(x, y);

        // Sprite
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        sprite.size.set(2f, 2f);
        sprite.textureOffset.set(0f, -0.3f);
        sprite.texture = null;
        entity.add(sprite);

        // Goose
        GooseComponent goose = engine.createComponent(GooseComponent.class);
        goose.homePosition = new Vector2(x, y);
        goose.currentWanderWaypoint = null;
        entity.add(goose);

        // Interactable
        InteractableComponent interact = engine.createComponent(InteractableComponent.class);
        interact.activationMessage = new GooseBiteMessage();
        interact.additionalMessages.add(new SoundMessage(assetLoader.get(AssetId.TEST_SOUND, Sound.class),
                1f));
        interact.disappearOnInteract = false;
        interact.interactionEnabled = true;
        entity.add(interact);

        // Physics
        addBoxCollider(entity, x, y,
                0.9f, 0.9f,
                0f, 0.4f,
                BodyDef.BodyType.DynamicBody,
                true);

        // Animation
        AnimationComponent<GooseComponent.GooseAnimState> anim = (AnimationComponent<GooseComponent.GooseAnimState>) engine
                .createComponent(AnimationComponent.class);

        Texture sheet = assetLoader.get(AssetId.GOOSE_SHEET, Texture.class);

        // Build animations for goose
        float idleTime = 0.25f;
        float walkTime = 0.12f;
        record GAnim(GooseComponent.GooseAnimState state, int start, int end, float time) {
        }
        GAnim[] table = {
                // IDLE
                new GAnim(GooseComponent.GooseAnimState.IDLE_RIGHT, 0, 5, idleTime),
                new GAnim(GooseComponent.GooseAnimState.IDLE_UP, 6, 11, idleTime),
                new GAnim(GooseComponent.GooseAnimState.IDLE_LEFT, 12, 17, idleTime),
                new GAnim(GooseComponent.GooseAnimState.IDLE_DOWN, 18, 23, idleTime),

                // WALK
                new GAnim(GooseComponent.GooseAnimState.WALK_RIGHT, 0, 5, walkTime),
                new GAnim(GooseComponent.GooseAnimState.WALK_UP, 6, 11, walkTime),
                new GAnim(GooseComponent.GooseAnimState.WALK_LEFT, 12, 17, walkTime),
                new GAnim(GooseComponent.GooseAnimState.WALK_DOWN, 18, 23, walkTime)
        };

        for (GAnim g : table) {
            anim.animations.put(g.state(), loadFrames(sheet, 64, 64, 2, g.start(), g.end(), g.time()));
        }

        // Starting animation
        anim.currentState = GooseComponent.GooseAnimState.IDLE_DOWN;
        anim.elapsed = 0f;

        entity.add(anim);

        return entity;
    }


}
