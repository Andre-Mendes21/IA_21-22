package screenpac.controllers.MCTS;

import java.util.*;
import java.util.concurrent.TimeUnit;

import screenpac.extract.Constants;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameState;
import screenpac.model.GameStateInterface;

public class MCTS implements Constants {

    private final MCTNode rootNode;
    private final Random random = new Random();
    private final GhostTeamController ghostsController;

    private final int numOfActivePillsStart;
    private final long timeDue;

    public MCTS(GameStateInterface gameState, long timeDue) {
        this.rootNode = new MCTNode(gameState.copy(), 0);
        this.numOfActivePillsStart = gameState.getNumberActivePills();
        this.timeDue = timeDue;
    }

    public DIR runMCTS() {
        long deltaTimeNS = 0;
        Long lastNS = System.nanoTime();

        while(!Terminate(deltaTimeNS)) {
            MCTNode selectedNode = TreePolicy(rootNode);

            double reward = SimulateGame(selectedNode);
            Backpropagate(selectedNode, reward);

            long currentNS = System.nanoTime();
            deltaTimeNS = currentNS - lastNS;
            lastNS = currentNS;
        }

        Optional<MCTNode> bestNode = rootNode.getChildren().stream().max(Comparator.comparingInt(MCTNode::getTimesVisited));
        if(bestNode.isPresent()) {
            return bestNode.get().parentAction;
        }

        return null;
    }

    private boolean Terminate(long lastDeltaNS) {
        long lastDeltaMillis = TimeUnit.MILLISECONDS.convert(lastDeltaNS, TimeUnit.NANOSECONDS);
        long returnTimeMS = 2;
        if(System.currentTimeMillis() + lastDeltaMillis + returnTimeMS > timeDue)
            return true;
        return false;
    }

    private MCTNode TreePolicy(MCTNode currentNode) {
        if(currentNode.isGameOver()) {
            return currentNode.parent != null ? currentNode.parent : currentNode;
        }

        if(!currentNode.isFullyExpanded()) {
            return expandNode(currentNode);
        }

        if(currentNode.getChildren().isEmpty()) {
            return currentNode;
        }
        
        boolean allChildVisits

        return TreePolicy(currentNode.getBestChild());
    }
}
