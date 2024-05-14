package com.big.dragons_of_mugloar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TaskProbability {
    PIECE_OF_CAKE(1, "Piece of cake"),
    WALK_IN_THE_PARK(1, "Walk in the park"),
    SURE_THING(2, "Sure thing"),
    QUITE_LIKELY(3, "Quite likely"),
    RISKY(4, "Risky"),
    PLAYING_WITH_FIRE(4, "Playing with fire"),
    HMM(4, "Hmmm...."),
    GAMBLE(5, "Gamble"),
    DETRIMENTAL(6, "Rather detrimental"),
    IMPOSSIBLE(6, "Impossible"),
    SUICIDE_MISSION(7, "Suicide mission"),
    UNKNOWN(Integer.MAX_VALUE, "");

    private final int difficulty;
    private final String probability;

    public static TaskProbability getByProbability(String probability) {
        return Arrays.stream(values())
                .filter(it -> it.getProbability().equalsIgnoreCase(probability))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
