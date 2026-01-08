package io.github.maze11.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.maze11.MazeGame;
import io.github.maze11.systems.gameState.LeaderBoardSystem;
import io.github.maze11.ui.FontGenerator;

/**
 * Main menu screen contains UI logic for buttons when game starts
 * provides options to start gam or quit
 */
public class MenuScreen extends BaseMenuScreen {

    static String playerName;
    boolean nameEntered = false;
    
    public MenuScreen(MazeGame game) {
        super(game);
        System.out.println("Menu screen launched");
        buildUI();
    }

    @Override
    protected void buildUI() {
        Table outerTable = new Table();
        Table innerTable = new Table();
        //outerTable.add().expandX();
        outerTable.setFillParent(true);
        
        // Building Main Menu
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("Maze Game", titleStyle);

        Label.LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.LIGHT_GRAY);
        Label subtitle = new Label("Navigate the maze!", bodyStyle);

        BitmapFont buttonFont = FontGenerator.generateRobotoFont(24, Color.WHITE, skin);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = buttonFont;

        TextField nameEntry = new TextField("Enter your name", skin);
        nameEntry.hasKeyboardFocus();
        nameEntry.addListener(new ChangeListener() {
            
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String enteredName = nameEntry.getText();
                // Ensuring the player enters a name
                if (!enteredName.equals("") && !enteredName.equals("Enter your name")) {
                    if (enteredName.length() >= 20) {
                        enteredName = enteredName.substring(0, 20);
                    }
                    MenuScreen.playerName = enteredName;
                    nameEntered = true;
                }
                // If the player doesn't enter their name, it is set for them
            }
        });

        TextButton startButton = new TextButton("Start Game", buttonStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                
                if (nameEntered) {
                    game.switchScreen(new LevelScreen(game));
                    playMenuClick();
                } else {
                    MenuScreen.playerName = "default";
                    game.switchScreen(new LevelScreen(game));
                    playMenuClick();
                }

            }
        });

        TextButton quitButton = new TextButton("Quit", buttonStyle);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });

	// CHANGED - build achievements:
	// temporary fix to orient menu properly:
	String achvData = "Achievements:                            \n";

	if (LeaderBoardSystem.readAchievements() != null) {
		for (String entry : LeaderBoardSystem.readAchievements()) {
			achvData += entry + "\n";
		}
	}

	Label achievements = new Label(achvData, bodyStyle);
	outerTable.add(achievements).padRight(325).padLeft(200);
        
        // vertical stack layout for elements in menu screen
        innerTable.add(title).padBottom(20).row(); // e.g. title with 20px space below
        innerTable.add(subtitle).padBottom(40).row();
        innerTable.add(nameEntry).width(200).height(40).padBottom(10).row();
        innerTable.add(startButton).width(200).height(60).padBottom(10).row();
        innerTable.add(quitButton).width(200).height(60);
        innerTable.center();

        outerTable.add(innerTable);

        // Building Leaderboard

        // Fetching data from file
        String lbData = "Top Scores:\n";

        if (LeaderBoardSystem.readLeaderboard() != null) {
            for (String entry : LeaderBoardSystem.readLeaderboard()) {
                lbData += entry +"\n";
            }
        }
        
        Label leaderboard = new Label(lbData, bodyStyle);
        outerTable.add(leaderboard).expandX();


	// do @ end, regardless of other features
	stage.addActor(outerTable);
	stage.setDebugAll(false);
    }

    @Override
    protected float[] getBackgroundColor() {
        return new float[]{0.1f, 0.1f, 0.2f, 1f}; // Dark blue
    }
}
