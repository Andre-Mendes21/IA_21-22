package screenpac.controllers.MCTS;

import screenpac.model.GameStateInterface;

public class SimResult {
    public int steps = 0;
    public GameStateInterface gameState;

    public boolean diedDuringSim = false;
    public boolean levelComplete = false;
    public boolean powerPillUnnecessarilyEaten = false;
}
