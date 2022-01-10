package screenpac.controllers.MCTS;

import screenpac.controllers.AgentInterface;
import screenpac.extract.Constants;
import screenpac.features.Utilities;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameStateInterface;

public class MCTSPacman implements AgentInterface, Constants{
    // private DIR myDir = DIR.NEUTRAL;
    private GhostTeamController ghostsController;
    private long timeDue;
    // private DIR nextDirs;
    private GameStateInterface it;

    public MCTSPacman(GhostTeamController ghostsController, long timeDue) {
        this.ghostsController = ghostsController;
        this.timeDue = timeDue;
    }
    
    @Override
    public int action(GameStateInterface gs) {
        // DIR nextMove = Utils.checkNearGhosts(gs);
        // if(nextMove != DIR.NEUTRAL) {
        //     return nextMove.ordinal();
        // }
        int myDir = NEUTRAL;
        MCTS mcts = new MCTS(gs.copy(), this.ghostsController, timeDue);
        this.it = mcts.runMCTS();

        if(it != null) {
            return myDir = Utilities.getWrappedDirection(gs.getPacman().current, it.getPacman().current, gs.getMaze());
        }

        return myDir;
    }
    
}
