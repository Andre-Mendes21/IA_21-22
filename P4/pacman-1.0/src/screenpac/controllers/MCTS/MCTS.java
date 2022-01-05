package screenpac.controllers.MCTS;

import java.util.*;
import java.util.concurrent.TimeUnit;

import screenpac.extract.Constants;
import screenpac.features.Utilities;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameStateInterface;

public class MCTS implements Constants {

    private final MCTNode rootNode;
    private final Random random = new Random();
    private final GhostTeamController ghostsController;

    private final int numOfActivePillsStart;
    private final long timeDue;

    public MCTS(GameStateInterface gameState, GhostTeamController ghostsController, long timeDue) {
        this.rootNode = new MCTNode(gameState.copy(), 0);
        this.ghostsController = ghostsController;

        this.numOfActivePillsStart = gameState.getNumberActivePills();
        this.timeDue = timeDue;
    }

    public DIR runMCTS() {
        long deltaTimeNS = 0;
        Long lastNS = System.nanoTime();

        while(!terminate(deltaTimeNS)) {
            MCTNode selectedNode = treePolicy(rootNode);

            double reward = simulateGame(selectedNode);
            backpropagate(selectedNode, reward);

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
    
    private boolean terminate(long lastDeltaNS) {
        long lastDeltaMillis = TimeUnit.MILLISECONDS.convert(lastDeltaNS, TimeUnit.NANOSECONDS);
        long returnTimeMS = 2;
        if(System.currentTimeMillis() + lastDeltaMillis + returnTimeMS > timeDue)
        return true;
        return false;
    }
    
    private MCTNode treePolicy(MCTNode currentNode) {
        if(currentNode.isGameOver()) {
            return currentNode.parent != null ? currentNode.parent : currentNode;
        }
        
        if(!currentNode.isFullyExpanded()) {
            return expandNode(currentNode);
        }
        
        if(currentNode.getChildren().isEmpty()) {
            return currentNode;
        }
        
        return treePolicy(currentNode.getBestChild());
    }

    MCTNode expandNode(MCTNode parentNode) {
        // FIXME: Change 140 magic number to Tree_Depth constant
        ArrayList<DIR> pacmanMoves = parentNode.getPacmanMovesNotExpanded(140);
        assert !pacmanMoves.isEmpty();
        DIR pacmanDir = pacmanMoves.get(random.nextInt(pacmanMoves.size()));
    
        SimResult result = Utilities.simulateUntilNextJunction(parentNode.gameState.copy(), ghostsController, pacmanDir);
    
        MCTNode child = new MCTNode(result.gameState, parentNode.pathLengthInSteps + result.steps);
    
        child.parentAction = pacmanDir;
        child.parent.parent = parentNode;
    
        parentNode.getChildren().add(child);
    
        if(result.didedDuringSim || result.powerPillUnncessarilyEaten) {
            child.updateReward(-1);
            child.setCanUpdate(false);
    
            return parentNode;
        }
    
        return child;
    }

    // * May need to change in order to optimise Score
    private double simulateGame(MCTNode selectedNode) {
        GameStateInterface simGameState = selectedNode.gameState.copy();
        // FIXME: Change 140 magic number to Tree_Depth constant
        int totalSteps = 140 - selectedNode.pathLengthInSteps;
        int remainingSteps = totalSteps;
        SimResult lastSimResult = new SimResult();

        while(!Utilities.analyzeGameState(simGameState, lastSimResult, remainingSteps, lastSimResult.powerPillUnncessarilyEaten)) {
            ArrayList<DIR> availableMoves = Utilities.getPacmanMovesAtJunctionWithoutReverse(simGameState);
            DIR pacmanDir = availableMoves.get(random.nextInt(availableMoves.size()));

            lastSimResult = Utilities.simulateToNextJunctionOrLimit(simGameState, ghostsController, pacmanDir, remainingSteps);

            remainingSteps -= lastSimResult.steps;
        }

        if(lastSimResult.levelComplete) {
            return 1;
        }

        if(lastSimResult.didedDuringSim || lastSimResult.powerPillUnncessarilyEaten) {
            return 0;
        }

        if(simGameState.getNumberActivePills() == numOfActivePillsStart) {
            return 0.1d * (1.d / numOfActivePillsStart);
        }

        return 1.0d - (simGameState.getNumberActivePills() / (double) numOfActivePillsStart);
    }

    private void backpropagate(MCTNode selectedNode, double reward) {
        while(selectedNode != null) {
            selectedNode.updateReward(reward);
            selectedNode = selectedNode.parent;
        }
    }
}
