package xyz.haoshoku.ttt.state;

import lombok.Getter;
import lombok.Setter;

public enum GameState {

    LOBBY,
    FORBIDDEN_MOVE,
    GRACE_PERIOD,
    INGAME,
    RESTARTING;


    @Getter @Setter
    private static GameState gameState;

}
