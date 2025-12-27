package io.github.maze11.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Screen;

import io.github.maze11.MazeGame;
import io.github.maze11.screens.PauseScreen;

// CHANGED: Created PauseSystem class
public class PauseSystem extends EntitySystem {

    public static Boolean gamePaused = false;
    static Screen pauseScreen;
    static Screen levelScreen;

    public PauseSystem(MazeGame game, Screen prevScreen) {
        pauseScreen = new PauseScreen(game, prevScreen);
    }

    public static void pauseGame() {
        gamePaused = true;        
    }

    public static void unpauseGame() {
        gamePaused = false;
    }
}
