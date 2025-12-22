package io.github.maze11.assetLoading;

/**
 * Used to define an asset in AssetPaths
 * @param id
 * @param assetType
 * @param <A>
 */
record AssetKey<A>(AssetId id, Class<A> assetType) {

    public Class<A> assetType() {return assetType;}
    }
