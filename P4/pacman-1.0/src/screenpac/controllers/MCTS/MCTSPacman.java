package screenpac.controllers.MCTS;

import screenpac.controllers.AgentInterface;
import screenpac.controllers.MCTS.Utils.DIR;
import screenpac.extract.Constants;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameStateInterface;

public class MCTSPacman implements AgentInterface, Constants{
    private DIR myDir = DIR.NEUTRAL;
    private GhostTeamController ghostsController;
    private int timeDue;

    public MCTSPacman(GhostTeamController ghostsController, int timeDue) {
        this.ghostsController = ghostsController;
        this.timeDue = timeDue;
    }

    @Override
    public int action(GameStateInterface gs) {
        MCTS mcts = new MCTS(gs, this.ghostsController, timeDue);
        DIR nextDir = mcts.runMCTS();

        if(nextDir != null) {
            myDir = nextDir;
        }

        return myDir.ordinal();
    }
    
}
