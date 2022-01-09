package screenpac.controllers.MCTS;

import screenpac.controllers.AgentInterface;
import screenpac.controllers.MCTS.Utils.DIR;
import screenpac.extract.Constants;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameStateInterface;

public class MCTSPacman implements AgentInterface, Constants{
    private DIR myDir = DIR.NEUTRAL;
    private GhostTeamController ghostsController;
    private long timeDue;
    private DIR nextDirs;

    public MCTSPacman(GhostTeamController ghostsController, long timeDue) {
        this.ghostsController = ghostsController;
        this.timeDue = timeDue;
    }
    
    @Override
    public int action(GameStateInterface gs) {
        DIR nextMove = Utils.checkNearGhosts(gs);
        if(nextMove != DIR.NEUTRAL) {
            return nextMove.ordinal();
        }
        MCTS mcts = new MCTS(gs, this.ghostsController, timeDue);
        this.nextDirs = mcts.runMCTS();

        if(nextDirs != null) {
            myDir = nextDirs;
        }

        return myDir.ordinal();
    }
    
}
