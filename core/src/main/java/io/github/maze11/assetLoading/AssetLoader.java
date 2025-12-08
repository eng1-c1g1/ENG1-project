package io.github.maze11.assetLoading;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Manages all assets using symbolic identifiers instead of hard-coding addresses.
 * This is to prevent the need to duplicate address data across the code and lead to unmaintainable code
 */
public class AssetLoader {
    private final AssetManager assetManager;
    private final HashMap<AssetKey<?>, String> assetMap;

    public AssetLoader() {
        assetManager = new AssetManager();

        assetMap = new HashMap<>();
        assetMap.putAll(AssetPaths.pathsWithTypes);

        // Puts all the texture paths into the map
        for (var entry : AssetPaths.texturePaths.entrySet()) {
            assetMap.put(new AssetKey<>(entry.getKey(), Texture.class), entry.getValue());
        }

        // Add audio to our assets
        for (var entry : AssetPaths.soundPaths.entrySet()) {
            assetMap.put(new AssetKey<>(entry.getKey(), Sound.class), entry.getValue());
        }
    }

    /**
     * Loads all the assets into memory. This should be done before the AssetLoader is used
     */
    public void load(){
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        for (var asset : assetMap.entrySet()){
                assetManager.load(asset.getValue(), (Class<?>) asset.getKey().assetType());
        }
        assetManager.finishLoading();
    }

    public <T> T get (AssetId id, Class<T> type) {
        return assetManager.get(assetMap.get(new AssetKey<>(id, type)), type);
    }

    /**
     * Unloads all assets
     */
    public void dispose(){
        assetManager.dispose();
    }
}
