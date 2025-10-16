package io.github.maze11.assetLoading;

import com.badlogic.gdx.assets.AssetManager;

import java.util.HashMap;

/**
 * A class to manage all assets using symbolic identifiers instead of hard-coding addresses.
 * This is to prevent the need to duplicate address data across the code and lead to unmaintainable code
 */
public class Assets {
    private AssetManager assetManager;
    private HashMap<AssetKey, String> assetMap;

    public Assets() {
        assetManager = new AssetManager();

        assetMap.putAll(AssetPaths.anyType);
    }

    public void load(){
        if (assetManager != null){
            throw new RuntimeException("Load was called twice");
        }
        assetManager = new AssetManager();

        for (var asset : assetMap.entrySet()){
            // TODO: resolve unchecked conversion
            assetManager.load(asset.getValue(), asset.getKey().assetType());
        }
        assetManager.finishLoading();
    }

    public <T> T get (AssetId id, Class<T> type) {
        return assetManager.get(assetMap.get(new AssetKey(id, type)), type);
    }

}
