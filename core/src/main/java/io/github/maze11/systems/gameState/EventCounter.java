package io.github.maze11.systems.gameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import io.github.maze11.messages.MessageType;

/**
 * Keeps count of the number of events and can output a score summary
 */
public class EventCounter {
    /** Stores the event types this records, along with their effect on score and message to show in the win screen*/
    private final Map<MessageType, MessageData> messageMappings = Map.ofEntries(
        entry(MessageType.COLLECT_COFFEE, new MessageData("Coffees Collected", 10)),
        entry(MessageType.PUDDLE_INTERACT, new MessageData("Slipped in puddle", -10)),
        entry(MessageType.CHECK_IN_CODE_COLLECT, new MessageData("Check-in codes collected", 20)),
        entry(MessageType.TIME_LOST, new MessageData("Homeboys Yapped With", -20)),
        entry(MessageType.GOOSE_BITE, new MessageData("Goose Bites", -10)),
        entry(MessageType.PI_ACTIVATED, new MessageData("All Pi's Activated", 75))
    );
    
    private final Map<MessageType, Integer> recordedCounts = new HashMap<>();
    private final int scorePerLeftoverSecond = 1;
    private final int completionBonus = 100;

    /** Records that a new instance of this message was registered in the EventCounter */
    public void receiveMessage(MessageType messageType) {
        // Check if this is a message this class is supposed to track
        if (messageMappings.containsKey(messageType)) {
            // Increment the number of events of this type received by 1
            if (recordedCounts.containsKey(messageType)) {
                recordedCounts.put(messageType, recordedCounts.get(messageType) + 1);
            }
            else {
                recordedCounts.put(messageType, 1);
            }
        }
    }

    /**
     * Generates the player score and a breakdown of how it was obtained
     * @param mazeExited Whether or not the player has reached the exit
     * @param secondsRemaining The seconds remaining on the timer at this point
     * @return A ScoreCard which contains the score and a breakdown of it
     */
    public ScoreCard makeScoreCard(boolean mazeExited, int secondsRemaining) {
        List<String> breakdown = new ArrayList<>();
        int totalScore = 0;

        // Calculate score from each of the tracked events
        for (var term : messageMappings.entrySet()) {
            // Get the number of events of this type collected. If no term, none of this event have been collected
            int occurrences = recordedCounts.getOrDefault(term.getKey(), 0);
            MessageData data = term.getValue();

            int scoreContribution = data.scoreBonus * occurrences;
            totalScore += scoreContribution;

            breakdown.add(occurrences + " " + data.name + ": " + formatBonus(scoreContribution));
        }

        // Time left over and completion bonus is only available if the player has left the maze
        if (mazeExited) {
            // Add score for left over time
            int scoreContribution = scorePerLeftoverSecond * secondsRemaining;
            totalScore += scoreContribution;
            breakdown.add("Time remaining bonus: " + formatBonus(scoreContribution));
            totalScore += completionBonus;
            breakdown.add("Escape bonus: " + formatBonus(completionBonus));
        }

        return new ScoreCard(totalScore, breakdown);
    }

    private String formatBonus(int bonus) {
        // Add a + to the displayed contribution if it is positive or 0
        return bonus >= 0 ? "+" + bonus : bonus + "";
    }

    private record MessageData(String name, int scoreBonus){ }
}
