package com.big.dragons_of_mugloar.exception;

import static com.big.dragons_of_mugloar.GameMessages.GAME_OVER_MESSAGE;

public class GameOverException extends DragonsOfMugloarException {
    public GameOverException() {
        super(GAME_OVER_MESSAGE);
    }
}
