package io.github.maze11.systems.gameState;

import io.github.maze11.messages.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Keeps count of the number of events and can output a score summary
 */
public class EventCounter {
    /** Stores the event types this records, along with their effect on score and message to show in the win screen*/
    private final Map<MessageType, MessageData> messageMappings = Map.ofEntries(
        entry(MessageType.COLLECT_COFFEE, new MessageData("Coffees Collected", 10)),
        entry(MessageType.GOOSE_BITE, new MessageData("Goose Bites", -20))
    );


    private final Map<MessageType, Integer> recordedCounts = new HashMap<>();

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
            // Add a + to the displayed contribution if it is positive or 0
            String scoreContributionFormatted = scoreContribution >= 0 ? "+" + scoreContribution : scoreContribution + "";

            breakdown.add(occurrences + " " + data.name + ": " + scoreContributionFormatted);
        }

        return new ScoreCard(totalScore, breakdown);
    }

    private record MessageData(String name, int scoreBonus){ }
}
