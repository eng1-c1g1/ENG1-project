package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.TiledMap;

import io.github.maze11.assetLoading.AssetId;
import io.github.maze11.assetLoading.AssetPaths;
import io.github.maze11.assetLoading.AssetKey;

public class AssetTests extends AbstractHeadlessGdxTest{
    
    // Testing that all the asset paths are valid
    @Test
    public void testPiAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.PI);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Pi should be accessible");
    }

    @Test
    public void testPlayerAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.PLAYER_SHEET);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite sheet for the Player should be accessible");
    }

    @Test
    public void testGooseAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.GOOSE_SHEET);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite sheet for the Goose should be accessible");
    }

    @Test
    public void testBullyAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.BULLY_SHEET);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite sheet for the Bully should be accessible");
    }

    @Test
    public void testCoffeAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.COFFEE);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the coffee should be accessible");
    }

    @Test
    public void testPuddleAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.PUDDLE);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Puddle should be accessible");
    }

    @Test
    public void testAnkhAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.ANKH);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Ankh should be accessible");
    }

    @Test
    public void testExitAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.EXIT);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Exit should be accessible");
    }

    @Test
    public void testCheckInAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.CHECK_IN);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Check-in code should be accessible");
    }

    @Test
    public void testFalseWallAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.FALSE_WALL);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the False Wall should be accessible");
    }

    @Test
    public void testPressurePlateAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.PRESSURE_PLATE);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Pressure Plate should be accessible");
    }

    @Test
    public void testLongBoiAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.LONGBOI);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Long Boi statue should be accessible");
    }

    @Test
    public void testBribeAssetExists() {
        String filepath = AssetPaths.texturePaths.get(AssetId.BRIBE);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The sprite for the Bribe should be accessible");
    }

    @Test
    public void testTileMapAssetExists() {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        String filepath = AssetPaths.pathsWithTypes.get(new AssetKey(AssetId.TILEMAP, TiledMap.class));
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Tilemap for the game should be accessible");
    }

    @Test
    public void testGameMusicExists() {
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        String filepath = AssetPaths.pathsWithTypes.get(new AssetKey(AssetId.GAME_MUSIC, Music.class));
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The music for the game should be accessible");
    }

    @Test
    public void testCoffeeSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.COFFEE_SLURP);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Coffee collected sound should be accessible");
    }

    @Test
    public void testCheckInSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.COLLECTABLE_SOUND);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Check-in collected sound should be accessible");
    }

    @Test
    public void testGooseHonkSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.GOOSE_HONK);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Goose honk sound should be accessible");
    }

    @Test
    public void testGooseHonkMultiSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.GOOSE_HONK_SEVERAL);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The many Geese honking sound should be accessible");
    }

    @Test
    public void testHiddenRoomSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.HIDDEN_ROOM_SOUND);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Hidden Room opened sound should be accessible");
    }

    @Test
    public void testPressurePlateSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.PRESSURE_PLATE_SOUND);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Pressure Plate activated sound should be accessible");
    }

    @Test
    public void testPlayerWalkingSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.PLAYER_WALKING);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Player walking sound should be accessible");
    }

    @Test
    public void testSingleFootstepSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.FOOTSTEP);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The single footstep sound should be accessible");
    }

    @Test
    public void testButtonPressedSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.SCREEN_BUTTON);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Button pressed sound should be accessible");
    }

    @Test
    public void testWinSoundExists() {
        String filepath = AssetPaths.soundPaths.get(AssetId.WIN_SOUND);
        assertTrue(Gdx.files.internal(filepath).exists(),
        "The Won game sound should be accessible");
    }
}
