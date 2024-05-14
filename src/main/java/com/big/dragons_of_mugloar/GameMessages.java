package com.big.dragons_of_mugloar;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMessages {
    public static final String GAME_STARTED_MESSAGE = "Game started! Game id: %s. Starting lives: %s. Starting gold: %s";
    public static final String ITEM_USED_MESSAGE = "Item used: %s. Lives: %s. Gold: %s.";
    public static final String CONTINUE_CONFIRMATION_MESSAGE = "You have received over %s points - %s, do you wish to continue? (Y/N)";
    public static final String GAME_FINISHED_MESSAGE = "Finished game %s with score: %s. Turns made: %s. Gold left: %s.";
    public static final String TASK_SOLVE_RESULT_MESSAGE = "%s Your lives are now: %s. Score: %s. Your task was: %s.";
    public static final String NO_ITEMS_AVAILABLE = "Could not buy any items";
    public static final String NO_TASKS_AVAILABLE = "No tasks available!";
    public static final String API_INVALID_RESPONSE_MESSAGE = "Something went wrong receiving the message from API: %s";
    public static final String HTTP_REQUEST_FAILED = "HTTP request failed with status code: %s. %s";
    public static final String GAME_OVER_MESSAGE = "Game over!";
}
