package io.github.maze11.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.audio.Sound;
import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.AssetLoader;

public class AudioSystem extends EntitySystem {

    AssetLoader assetLoader;
    public AudioSystem(AssetLoader assetLoader) {
        this.assetLoader = assetLoader;
    }

    @Override
    public void addedToEngine(Engine engine){
        assetLoader.get(AssetId.TEST_SOUND, Sound.class).play(1.0f);
    }
}
