package io.github.maze11.systems.gameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import io.github.maze11.messages.MessageType;



/**
 * Keeps count of the number of events and can output a score summary
 */
public class EventCounter {
    // CHANGED: Added messages for new events
    /** Stores the event types this records, along with their effect on score and message to show in the win screen*/
    private final Map<MessageType, MessageData> messageMappings = Map.ofEntries(
        entry(MessageType.COLLECT_COFFEE, new MessageData("Coffees Collected", 10, Type.POSITIVE)),
        entry(MessageType.PUDDLE_INTERACT, new MessageData("Slipped in puddle", -10, Type.NEGATIVE)),
        entry(MessageType.CHECK_IN_CODE_COLLECT, new MessageData("Check-in codes collected", 20, Type.POSITIVE)),
        entry(MessageType.TIME_LOST, new MessageData("Homeboys Yapped With", -20, Type.NEGATIVE)),
        entry(MessageType.GOOSE_BITE, new MessageData("Goose Bites", -10, Type.NEGATIVE)),
        entry(MessageType.PI_ACTIVATED, new MessageData("All Pi's Activated", 100, Type.HIDDEN)),
        entry(MessageType.ANKH_INTERACT, new MessageData("Ankh Activated", 0, Type.POSITIVE)),
        entry(MessageType.BULLY_BRIBED, new MessageData("Bully bribed", 0, Type.NEGATIVE)),
        entry(MessageType.LONGBOI_INTERACT, new MessageData("Long Boi found", 50, Type.HIDDEN)),
        entry(MessageType.PRESSURE_PLATE_TRIGGER, new MessageData("Hidden Room Unlocked", 0, Type.HIDDEN)),
        entry(MessageType.TELEPORTATION, new MessageData("Teleported backwards", -10, Type.NEGATIVE))
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

        // CHANGED: Changed the score breakdown to tell you how many positive, negative and hidden events were triggered.
        // Calculate score from each of the tracked events
        int positiveEventCount = 0;
        int negativeEventCount = 0;
        int hiddenEventCount = 0;
        int posScoreContribution = 0;
        int negScoreContribution = 0;
        int hiddenScoreContribution = 0;
        for (var term : messageMappings.entrySet()) {
            // Get the number of events of this type collected. If no term, none of this event have been collected
            int occurrences = recordedCounts.getOrDefault(term.getKey(), 0);
            MessageData data = term.getValue();

            int scoreContribution = data.scoreBonus * occurrences;

            if (data.eventType == Type.POSITIVE) {
                posScoreContribution += scoreContribution;
                positiveEventCount += occurrences;
            } else if (data.eventType == Type.NEGATIVE) {
                negScoreContribution += scoreContribution;
                negativeEventCount += occurrences;
            } else {
                hiddenScoreContribution += scoreContribution;
                hiddenEventCount += occurrences;
            }

            totalScore += scoreContribution;

        }
        breakdown.add(positiveEventCount + " Positive Events: " + formatBonus(posScoreContribution));
        breakdown.add(negativeEventCount + " Negative Events: " + formatBonus(negScoreContribution));
        breakdown.add(hiddenEventCount + " Hidden Events: " + formatBonus(hiddenScoreContribution));

        // Time left over and completion bonus is only available if the player has left the maze
        if (mazeExited) {
            // Add score for left over time
            int scoreContribution = scorePerLeftoverSecond * secondsRemaining;
            totalScore += scoreContribution;
            breakdown.add("Time remaining bonus: " + formatBonus(scoreContribution));
            totalScore += completionBonus;
            breakdown.add("Escape bonus: " + formatBonus(completionBonus));

	    // end of the game - now have how many events happened total - update achievements using it:
	    updateAchievements();
        }

        return new ScoreCard(totalScore, breakdown);
    }

    // CHANGED - added Achievement class to encapsulate achievement functionality
    public class Achievement {
            // maps events : number of times they need to occur to get the achievement
            private Map<MessageType, Integer> eventRequirements;
            private String name;

            public Achievement(String givenName, Map<MessageType, Integer> givenMapping) {
                // set Map based on mapping passed:
                name = givenName;
                eventRequirements = givenMapping;
            }
            
            public boolean checkEventsAchieve(Map<MessageType, Integer> EventCounts) {
                // check if given EventCounts qualifies for this achievement:
                for (var event : eventRequirements.keySet()) {
                    // debug:
                    String out = String.format("%s has count: %d", event, EventCounts.get(event));
                    System.out.println(out);
                    // check if invalid
                    if (EventCounts.get(event) == null) {
                        return false;

                    } else if (EventCounts.get(event) < eventRequirements.get(event)) {
                        return false;
                    }
                }
                // no issues found, true by default:
                return true;
            }

            public String getName() {
                return name;
            }
    }


    // CHANGED - added method to update achievements, called at end of game
    public void updateAchievements() {
            // -- ACHIEVEMENTS INITIALISED HERE --:
            List<Achievement> allAchievements = new ArrayList<Achievement>();
            // define achievements here
            // All hidden Pis
            allAchievements.add(new Achievement("Got All 3 Hidden Pis in 1 run", Map.ofEntries(entry(MessageType.PI_ACTIVATED, 1))));
            // All hidden events:
            allAchievements.add(new Achievement("Got All Hidden Events in 1 run", Map.ofEntries(
                entry(MessageType.PI_ACTIVATED, 1),
                entry(MessageType.LONGBOI_INTERACT, 1),
                entry(MessageType.PRESSURE_PLATE_TRIGGER, 1))));

            // All Events:
            Map<MessageType, Integer> reqMapping = new HashMap();
            // add each event to our requiremens mapping:
            
            for (MessageType msgType : messageMappings.keySet()) {
                reqMapping.put(msgType, 1);
            }
            
            allAchievements.add(new Achievement("Got All Events in 1 run", reqMapping));

            // load preferences:
            Preferences prefs = Gdx.app.getPreferences("Achievements");

            // iterate through each achievement, check which we achieved this run:
            for (var achieve : allAchievements){
                // check if achieved them in this run:
                if (achieve.checkEventsAchieve(recordedCounts)) {
                    // if achieved, just print to console that we did for now:
                    System.out.println("achieved " + achieve.getName());
                    
                    // save to preferences:
                    prefs.putBoolean(achieve.getName(), true);
                } else {
                    System.out.println("did not achieve " + achieve.getName());
                }
            }
            // save preferences so they persist:
            prefs.flush();
    }

    public List<String> getAllAchievements() {
        // get all keys from prefs file w/ value that isn't 0:
        List<String> achievementsGot = new ArrayList<>();
        // load preferences:
        Preferences prefs = Gdx.app.getPreferences("Achievements");
        // get all achievements w/ value = true
        for (var achvName: prefs.get().keySet()) {
            if (prefs.getBoolean(achvName) == true) {
                achievementsGot.add(achvName);
            }
        }
    
        return achievementsGot;
    }

    private String formatBonus(int bonus) {
        // Add a + to the displayed contribution if it is positive or 0
        return bonus > 0 ? "+" + bonus : bonus + "";
    }

    private record MessageData(String name, int scoreBonus, Type eventType){ }

    enum Type {
        POSITIVE,
        NEGATIVE,
        HIDDEN
    }
}

 
