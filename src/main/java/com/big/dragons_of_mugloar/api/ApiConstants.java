package com.big.dragons_of_mugloar.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {
    private static final String API_URL = "https://dragonsofmugloar.com/api/v2/";
    public static final String API_GAME_START = API_URL + "game/start";
    public static final String API_GET_TASKS = API_URL + "%s/messages";
    public static final String API_SOLVE_TASK = API_URL + "%s/solve/%s";
    public static final String API_GET_SHOP_LIST = API_URL + "%s/shop";
    public static final String API_SHOP_ITEM = API_URL + "%s/shop/buy/%s";
}
