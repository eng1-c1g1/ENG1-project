package io.github.maze11.assetLoading;

public record AssetKey<A>(AssetId id, Class<A> assetType) {

    public Class<A> assetType() {return assetType;}
    }
