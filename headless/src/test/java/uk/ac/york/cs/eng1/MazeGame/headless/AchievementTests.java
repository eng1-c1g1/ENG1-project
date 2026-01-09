package uk.ac.york.cs.eng1.MazeGame.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import io.github.maze11.systems.gameState.LeaderBoardSystem;

public class AchievementTests extends AbstractHeadlessGdxTest {

    static Preferences achievements;
    // Creates Achievements file before any of tests run
    @BeforeAll
    public static void createScoreFile() {
        
        HeadlessLauncher.main(null);
        achievements = Gdx.app.getPreferences("Achievements");
        scores.clear();
        scores.flush();
        LeaderBoardSystem.hiScoreFile = "testScoreFile";
        System.out.println("Score file created");
    }

    // Clears score file before each test runs.
    @BeforeEach
    public void clearScoreFile() {
        scores = Gdx.app.getPreferences("testScoreFile");
        scores.clear();
        scores.flush();
    }

    @Test
    public void emptyFileTest() {

        // Reading file when empty
        assertEquals(null, LeaderBoardSystem.readLeaderboard());

        // Submitting a score when empty
        String name = "bob";
        int score = 123;
        
        List<String> expectedList = new ArrayList<>();
        expectedList.add(String.format("%s - %d", name, score));

        LeaderBoardSystem lb = new LeaderBoardSystem(name);
        lb.submitScore(score);
        
        // Reading the file to check it was submitted correctly
        List<String> leaderboardList = LeaderBoardSystem.readLeaderboard();
        assertEquals(expectedList, leaderboardList, "Score was not submitted properly");

    }
    
}
