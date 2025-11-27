package io.github.maze11.factory;

import java.util.ArrayList;
import java.util.List;

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
import io.github.maze11.components.CameraFollowComponent;
import io.github.maze11.components.GooseComponent;
import io.github.maze11.components.PlayerComponent;
import io.github.maze11.components.SpriteComponent;
import io.github.maze11.messages.CoffeeCollectMessage;
import io.github.maze11.messages.GooseBiteMessage;
import io.github.maze11.messages.InteractableMessage;
import io.github.maze11.messages.Message;
import io.github.maze11.messages.MessageType;
import io.github.maze11.messages.PressurePlateTriggerMessage;
import io.github.maze11.messages.PuddleInteractMessage;
import io.github.maze11.messages.SoundMessage;
import io.github.maze11.messages.ToastMessage;

/**
 * Used to create entities. Has methods for all the entity instances that need to be created.
 * Constructs an entity by adding components in sequence.
 * Delegates much of component creation to ComponentMaker.
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
        Entity entity = makeEmptyEntity();

        cMaker.addTransform(entity, x + width / 2, y + height / 2, width, height);
        cMaker.addSprite(entity, AssetId.FALSE_WALL);
        cMaker.addBoxCollider(entity, x + width / 2, y + height / 2, width, height, 0f, 0f, BodyDef.BodyType.StaticBody, false);
        cMaker.addHiddenWall(entity,  triggeredBy);

        SpriteComponent sprite = entity.getComponent(SpriteComponent.class);
        sprite.textureOffset.set(sprite.textureOffset.x, sprite.textureOffset.y - 1f);

        return entity;
    }

    public Entity makePressurePlate(float x, float y, String triggers) {
        List<Message> additionalMessages = new ArrayList<>();
        additionalMessages.add(new SoundMessage(assetLoader.get(AssetId.PRESSURE_PLATE_SOUND, Sound.class), 1f));
        additionalMessages.add(new ToastMessage("You hear a click... A secret door has opened!", 2.5f));

        Entity entity = makeInteractable(x, y, new PressurePlateTriggerMessage(triggers), true, AssetId.PRESSURE_PLATE, additionalMessages);
        cMaker.addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    // Creates a wall entity with box collider
    public Entity makeWall(float x, float y, float width, float height) {
        Entity entity = makeEmptyEntity();
        cMaker.addBoxCollider(entity, x + width / 2, y + height / 2, width, height, 0f, 0f,
                BodyDef.BodyType.StaticBody, false);
        return entity;
    }

    /** Creates the player entity with sprite, camera follow, and physics */
    public Entity makePlayer(float x, float y) {
        Entity entity = makeEmptyEntity();

        cMaker.addTransform(entity, x, y);
        cMaker.addSprite(entity, null, 1f, 2f, 0f, 0.05f);
        entity.add(engine.createComponent(PlayerComponent.class));

        // Camera follows the player
        entity.add(engine.createComponent(CameraFollowComponent.class));

        // Physics
        cMaker.addBoxCollider(entity, x, y, 0.9f, 0.9f,
                0f, 0.5f,
                BodyDef.BodyType.DynamicBody,
                true);
        cMaker.addAudioListener(entity, new Vector2(0f, 1f));

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
        cMaker.addTimer(entity, durationSeconds);
        return entity;
    }

    private Entity makeInteractable(float x, float y, InteractableMessage message, boolean disappearOnInteract,
            AssetId assetId, List<Message> additionalMessages) {
        Entity entity = makeEmptyEntity();
        cMaker.addTransform(entity, x, y);
        cMaker.addSprite(entity, assetId);
        cMaker.addInteractable(entity, message, disappearOnInteract, additionalMessages);

        return entity;
    }

    public Entity makeCoffee(float x, float y) {
        List<Message> additionalMessages = new ArrayList<>();
        additionalMessages.add(new SoundMessage(assetLoader.get(AssetId.COFFEE_SLURP, Sound.class), 1f));
        additionalMessages.add(new ToastMessage("Coffee drank! +Speed", 2f));

        Entity entity = makeInteractable(x, y, new CoffeeCollectMessage(), true, AssetId.COFFEE, additionalMessages);
        cMaker.addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    public Entity makePuddle(float x, float y) {
        List<Message> additionalMessages = new ArrayList<>();
        additionalMessages.add(new ToastMessage("Slipped in puddle! -Speed", 2f));

        Entity entity = makeInteractable(x, y, new PuddleInteractMessage(), false, AssetId.PUDDLE, additionalMessages);
        cMaker.addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    public Entity makeCheckInCode(float x, float y) {
        List<Message> additionalMessages = new ArrayList<>();
        additionalMessages.add(new SoundMessage(assetLoader.get(AssetId.COLLECTABLE_SOUND, Sound.class), 1f));
        additionalMessages.add(new ToastMessage("Check-in code collected! +20 Points", 2f));

        Entity entity = makeInteractable(x, y, new InteractableMessage(MessageType.CHECK_IN_CODE_COLLECT), true,
                AssetId.CHECK_IN, additionalMessages);
        cMaker.addCircleCollider(entity, x, y, 0.75f, 0f, 0.5f, BodyDef.BodyType.StaticBody);
        return entity;
    }

    public Entity makeExit(float x, float y) {
        Entity entity = makeInteractable(x, y, new InteractableMessage(MessageType.EXIT_MAZE), false, AssetId.EXIT, null);
        cMaker.addBoxCollider(entity, x, y, 1.6f, 2.2f, 0f, 0.5f,
                BodyDef.BodyType.StaticBody, true);

        SpriteComponent sprite = entity.getComponent(SpriteComponent.class);
        if (sprite != null) {
            sprite.size.set(3f, 3f);
            sprite.textureOffset.set(sprite.textureOffset.x, sprite.textureOffset.y - 1f);
        }

        return entity;
    }

    public Entity makeGoose(float x, float y) {
        Entity entity = makeEmptyEntity();

        List<Message> messages = new ArrayList<>();
        messages.add(new SoundMessage(assetLoader.get(AssetId.GOOSE_HONK, Sound.class),
            1f));

        cMaker.addTransform(entity, x, y);
        cMaker.addSprite(entity, null, 2f, 2f, 0f, -0.3f);
        cMaker.addGoose(entity, x, y);
        cMaker.addInteractable(entity, new GooseBiteMessage(), false, messages);
        cMaker.addBoxCollider(entity, x, y,
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
