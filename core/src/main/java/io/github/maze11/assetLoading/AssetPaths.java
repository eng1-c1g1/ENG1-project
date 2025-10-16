package io.github.maze11.assetLoading;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class AssetPaths {
    public static Map<AssetKey, String> anyType = Map.ofEntries(
        entry(new AssetKey(AssetId.DebugTexture, Texture.class), "")
    );
}
