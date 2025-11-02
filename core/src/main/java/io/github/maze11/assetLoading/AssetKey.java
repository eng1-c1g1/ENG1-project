package io.github.maze11.assetLoading;

record AssetKey<A>(AssetId id, Class<A> assetType) {

    public Class<A> assetType() {return assetType;}
    }
