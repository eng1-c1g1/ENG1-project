package io.github.maze11.assetLoading;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class AssetPaths {
    public static Map<AssetKey, String> anyType = Map.ofEntries(
        entry(new AssetKey(AssetId.DebugTexture, Texture.class), "Test_Square.png"),
        entry(new AssetKey(AssetId.PlayerTexture, Texture.class), "Test_Square.png"),
        entry(new AssetKey(AssetId.OriginIndicator, Texture.class), "origin_indicator.png"),
        entry(new AssetKey(AssetId.Tilemap, TiledMap.class), "floor.tmx")

    );
}
