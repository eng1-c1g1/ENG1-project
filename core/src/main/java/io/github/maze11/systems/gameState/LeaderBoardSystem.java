package io.github.maze11.systems.gameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Preferences;

import com.badlogic.gdx.Gdx;

//CHANGED: Added LeaderBoardSystem class
public class LeaderBoardSystem {

    final int leaderboardLen = 5;   // Max number of entries to store/display in the leaderboard
    public static String hiScoreFile = "hiScores";
    String name;
    int score;

    public LeaderBoardSystem(String name) {
        this.name = name;
    }

     /**
      * Checks if the current score is a new high-score, then saves it in the
      * leaderboard file if it is.
      * @param score
      */
    public void submitScore(int score) {
        
        Preferences hiScores = Gdx.app.getPreferences(hiScoreFile);
        // If a score has already been submitted for this player, check whether the
        // new score is larger or not, then replace if necessary.
        if (hiScores.contains(name)) {
            if (hiScores.getInteger(name) < score) {
                hiScores.putInteger(name, score);
                hiScores.flush();
            }
            return;
        }
        // Only allows 5 entries in score file at once
        if (hiScores.get().size() == leaderboardLen) {
            
            // Checking whether new score is higher than the scores in the file.
            String lowestScoreKey = compareScore(score);

            if (lowestScoreKey == null) {  
                // score isn't a new high score
                return;
            }
            hiScores.remove(lowestScoreKey);
        }
        
        hiScores.putInteger(name,score);
        hiScores.flush();
        return;
        
    }

    /**
     * Compares the score of the current game to the scores in the leaderboard file
     * and returns which entry (if any) in the file the score should replace.
     * @param score
     * @returns a key from the preferences file, or null if the score is not a new high score
     * */
    private String compareScore(int score) {
        
        Preferences hiScores = Gdx.app.getPreferences(hiScoreFile);

        for (String key : hiScores.get().keySet()) {
            int hiScore = hiScores.getInteger(key);
            if (hiScore < score) {
                return key;
            }
        }
        return null;
    }

    /**
     * 
     * @returns a string of of all the lines in the leaderboard file seperated by newlines
     */
    public static List<String> readLeaderboard() {
        
        // Formatting Leaderboard
        List<String> formattedLb = new ArrayList<>();

        Preferences hiScores = Gdx.app.getPreferences(hiScoreFile);

        if (hiScores.get().size() == 0) {
            return null;
        }
        for (String key : hiScores.get().keySet()) {
            int score = hiScores.getInteger(key);
            String entry = String.format("%s - %d", key, score);
            formattedLb.add(entry);
        }
        formattedLb.sort(new leaderBoardSort());
        return formattedLb;
    }
}

// Compares leaderboard entry strings based on their score component.
class leaderBoardSort implements Comparator {
    public int compare(Object obj1, Object obj2) {
        String str1 = (String)obj1;
        String str2 = (String)obj2;

        int score1 = -9999;
        int score2 = -9999;

        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) == '-') {
                score1 = Integer.parseInt(str1.substring(i + 2));
                break;
            }
        }

        for (int i = 0; i < str2.length(); i++) {
            if (str2.charAt(i) == '-') {
                score2 = Integer.parseInt(str2.substring(i + 2));
                break;
            }
        }

        if (score1 < score2) return 1;
        if (score1 > score2) return -1;
        return 0;
    }
}
