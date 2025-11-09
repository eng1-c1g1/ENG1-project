package io.github.maze11.assetLoading;

import java.util.Map;
import static java.util.Map.entry;

import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Static storage for all the file paths of the assets loaded in the project
 */
public class AssetPaths {

    // Texture paths is a special case of the more general paths, created for convenience of assignment
    // This prevents needing to constantly repeat that all the entries are textures
    static Map<AssetId, String> texturePaths = Map.ofEntries(
        entry(AssetId.PLAYER_SHEET, "sprites/Premade_Character_32x32_17.png"),
        entry(AssetId.GOOSE_SHEET, "sprites/Duck_White_32x32.png"),

        entry(AssetId.ORIGIN_INDICATOR, "origin_indicator.png"),
        entry(AssetId.COFFEE, "items/coffee.png"),
        entry(AssetId.EXIT, "sprites/exit.png"),
        entry(AssetId.CHECK_IN, "items/check-in.png"),

        entry(AssetId.FALSE_WALL, "sprites/false-wall.png"),
        entry(AssetId.PRESSURE_PLATE, "debug.png")
    );

    static Map<AssetId, String> soundPaths = Map.ofEntries(
        entry(AssetId.TEST_SOUND, "audio/test_sound.mp3"),
        entry(AssetId.COFFEE_SLURP, "audio/CoffeeSlurlp.mp3"),
        entry(AssetId.COLLECTABLE_SOUND, "audio/CollectableSound.mp3"),
        entry(AssetId.GOOSE_HONK, "audio/GooseHonk.mp3"),
        entry(AssetId.GOOSE_HONK_SEVERAL, "audio/GooseHonkingMany.mp3"),
        entry(AssetId.HIDDEN_ROOM_SOUND, "audio/HiddenRoomSound.mp3"),
        entry(AssetId.PLAYER_WALKING, "audio/PlayerWalking.mp3"),
        entry(AssetId.PRESSURE_PLATE_SOUND, "audio/PressurePlateSound.mp3"),
        entry(AssetId.SCREEN_BUTTON, "audio/ScreenButtons.mp3"),
        entry(AssetId.WIN_SOUND, "audio/WinSound.mp3")
    );

    // This map can be used to declare paths to loaded files of any type
    static Map<AssetKey<?>, String> pathsWithTypes = Map.ofEntries(
        entry(new AssetKey<>(AssetId.TILEMAP, TiledMap.class), "map/default.tmx")

    );
}
