package screenpac.controllers.MCTS;

import java.util.*;

import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameState;

public class MCTS {

    private GameState game;
    private GhostTeamController ghosts;
    private Stack<Game> gameStates;
    private MCTNode rootNode;
    public static Random random = new Random();
    private Set<Integer> activePowerPills;
}
