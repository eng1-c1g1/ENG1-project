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
        entry(AssetId.EXIT, "debug.png"),
        entry(AssetId.CHECK_IN, "debug.png")
    );

    // This map can be used to declare paths to loaded files of any type
    static Map<AssetKey<?>, String> pathsWithTypes = Map.ofEntries(
        entry(new AssetKey<>(AssetId.TILEMAP, TiledMap.class), "map/default.tmx")

    );
}
