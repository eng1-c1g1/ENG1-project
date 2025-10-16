package io.github.maze11.assetLoading;

import com.badlogic.gdx.assets.AssetManager;

import java.util.HashMap;
import java.util.Objects;

/**
 * A class to manage all assets using symbolic identifiers instead of hard-coding addresses.
 * This is to prevent the need to duplicate address data across the code and lead to unmaintainable code
 */
public class Assets {
    private AssetManager assetManager;
    private HashMap<AssetKey, String> assetMap;

    public void load(){
        if (assetManager != null){
            throw new RuntimeException("Load was called twice");
        }
        assetManager = new AssetManager();

        for (var asset : assetMap.entrySet()){
            // TODO: resolve unchecked conversion
            assetManager.load(asset.getValue(), asset.getKey().assetType);
        }

        // TODO: add methods to access the assets managed by this class
    }

    private class AssetKey{
        public final AssetId id;
        public final Class assetType;

        public AssetKey(AssetId id, Class assetType){
            this.id = id;
            this.assetType = assetType;
        }

        @Override
        public int hashCode(){
            return Objects.hash(id, assetType);
        }

        @Override
        public boolean equals(Object o){
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            return this.id == ((AssetKey)o).id && this.assetType == ((AssetKey)o).assetType;
        }
    }
    private enum AssetType{
        TEXTURE
    }
}
