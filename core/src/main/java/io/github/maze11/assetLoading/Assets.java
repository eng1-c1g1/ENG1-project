package io.github.maze11.assetLoading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.util.HashMap;

/**
 * A class to manage all assets using symbolic identifiers instead of hard-coding addresses.
 * This is to prevent the need to duplicate address data across the code and lead to unmaintainable code
 */
public class Assets {
    private AssetManager assetManager;
    private final HashMap<AssetKey, String> assetMap;

    public Assets() {
        assetManager = new AssetManager();

        assetMap = new HashMap<>();
        assetMap.putAll(AssetPaths.anyType);
    }

    public void load(){
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        for (var asset : assetMap.entrySet()){
            // TODO: resolve unchecked conversion
            assetManager.load(asset.getValue(), asset.getKey().assetType());
        }
        assetManager.finishLoading();
    }

    public <T> T get (AssetId id, Class<T> type) {
        return assetManager.get(assetMap.get(new AssetKey(id, type)), type);
    }

    public void dispose(){
        assetManager.dispose();
    }
}
