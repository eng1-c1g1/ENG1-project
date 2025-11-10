package io.github.maze11.systems.gameState;

import java.util.List;

/**
 * Stores the score the player achieved and an element-by-element breakdown of how this was achieved
 * @param totalScore The resultant score of the player
 * @param breakdown Each string is a line explaining a way the player gained or lost score
 */
public record ScoreCard (int totalScore, List<String> breakdown) { }
