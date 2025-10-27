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
    public static Map<AssetId, String> texturePaths = Map.ofEntries(
        entry(AssetId.PlayerTexture, "player.png")
    );

    // This map can be used to declare paths to loaded files of any type
    public static Map<AssetKey<?>, String> pathsWithTypes = Map.ofEntries(
        entry(new AssetKey<>(AssetId.Tilemap, TiledMap.class), "map/default.tmx")

    );
}
