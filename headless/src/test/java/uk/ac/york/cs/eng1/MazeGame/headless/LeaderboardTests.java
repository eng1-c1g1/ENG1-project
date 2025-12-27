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

public class LeaderboardTests extends AbstractHeadlessGdxTest {

    static Preferences scores;
    // Creates score file before any of tests run
    @BeforeAll
    public static void createScoreFile() {
        
        HeadlessLauncher.main(null);
        scores = Gdx.app.getPreferences("testScoreFile");
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
    
    @Test
    public void duplicateScoreTests() {
        // Submitting a score
        String name = "bob";
        int score = 123;
        int newScore = 200;
        
        // Simulating a higher score being earnt by the player in the same play session
        LeaderBoardSystem lb = new LeaderBoardSystem(name);
        lb.submitScore(score);
        lb.submitScore(newScore);

        List<String> expectedList = new ArrayList<>();
        expectedList.add(String.format("%s - %d", name, newScore));
        
        // Reading the file to check the correct score has been saved
        List<String> leaderboardList = LeaderBoardSystem.readLeaderboard();
        assertEquals(expectedList, leaderboardList, "New score was not submitted properly");

        // Simulating a higher score being earnt by the player after closing and reopening the game
        // but still using the same name.
        newScore = 300;
        LeaderBoardSystem lb2 = new LeaderBoardSystem(name);
        lb2.submitScore(newScore);

        expectedList.clear();
        expectedList.add(String.format("%s - %d", name, newScore));

        leaderboardList = LeaderBoardSystem.readLeaderboard();
        assertEquals(expectedList, leaderboardList, "New score was not submitted properly");

        // Ensuring the score is only overwritten if it is higher than the previous one
        lb2.submitScore(score);
        assertEquals(expectedList, leaderboardList, "Old score should not be overwritten");
    }

    @Test
    public void sortScoresTest() {
        
        // Submitting scores in the wrong order
        LeaderBoardSystem lb = new LeaderBoardSystem("tom");
        lb.submitScore(400);
        LeaderBoardSystem lb2 = new LeaderBoardSystem("jim");
        lb2.submitScore(500);
        LeaderBoardSystem lb3 = new LeaderBoardSystem("bob");
        lb3.submitScore(600);

        // List of scores in correct order
        List<String> expectedList = new ArrayList<>();
        expectedList.add(String.format("%s - %d", "bob", 600));
        expectedList.add(String.format("%s - %d", "jim", 500));
        expectedList.add(String.format("%s - %d", "tom", 400));

        // Reading the file
        List<String> leaderboardList = LeaderBoardSystem.readLeaderboard();
        assertEquals(expectedList, leaderboardList, "Scores should be returned in the correct order");

    }

    @Test
    public void replaceHiScoreTests() {
        // Submitting 5 scores
        LeaderBoardSystem lb = new LeaderBoardSystem("tom");
        lb.submitScore(400);
        LeaderBoardSystem lb2 = new LeaderBoardSystem("jim");
        lb2.submitScore(500);
        LeaderBoardSystem lb3 = new LeaderBoardSystem("bob");
        lb3.submitScore(600);
        LeaderBoardSystem lb4 = new LeaderBoardSystem("tim");
        lb4.submitScore(300);
        LeaderBoardSystem lb5 = new LeaderBoardSystem("rob");
        lb5.submitScore(200);

        // 6th Score shouldn't fit onto the scoreboard
        // As it is lower, it shouldn't replace any scores
        LeaderBoardSystem lbLow = new LeaderBoardSystem("dave");
        lbLow.submitScore(100);

        // List of expected scores
        List<String> expectedList = new ArrayList<>();
        expectedList.add(String.format("%s - %d", "bob", 600));
        expectedList.add(String.format("%s - %d", "jim", 500));
        expectedList.add(String.format("%s - %d", "tom", 400));
        expectedList.add(String.format("%s - %d", "tim", 300));
        expectedList.add(String.format("%s - %d", "rob", 200));

        // Reading the file
        List<String> leaderboardList = LeaderBoardSystem.readLeaderboard();
        assertEquals(expectedList, leaderboardList, "High scores shouldn't have been overwritten by lower score");

        // Testing whether a higher score can replace a lower one
        LeaderBoardSystem lbHigh = new LeaderBoardSystem("john");
        lbHigh.submitScore(350);

        // Altering list of expected scores
        expectedList.remove(4);
        expectedList.add(3, String.format("%s - %d", "john", 350));

        leaderboardList = LeaderBoardSystem.readLeaderboard();
        assertEquals(expectedList, leaderboardList, 
            "New High score should have been inserted in position 4 and shifted tim's score to 5th");
    }
}
