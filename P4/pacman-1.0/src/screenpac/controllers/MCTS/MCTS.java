package screenpac.controllers.MCTS;

import java.util.*;
import java.util.concurrent.TimeUnit;

import screenpac.controllers.MCTS.Utils.DIR;
import screenpac.extract.Constants;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameStateInterface;
import screenpac.model.Node;

public class MCTS implements Constants {

    private final MCTNode rootNode;
    private final Random random = new Random();
    private final GhostTeamController ghostsController;

    private final int numOfActivePillsStart;
    private final long timeDue;

    public MCTS(GameStateInterface gameState, GhostTeamController ghostsController, long timeDue) {
        this.rootNode = new MCTNode(gameState.copy(), 0);
        this.ghostsController = ghostsController;

        this.numOfActivePillsStart = Utils.getNumberActivePills(gameState);
        this.timeDue = timeDue;
    }

    public DIR runMCTS() {
        Long timeStamp = System.currentTimeMillis();

        while(System.currentTimeMillis() <= timeStamp + timeDue + 2) {
            MCTNode selectedNode = treePolicy(rootNode);

            double reward = simulateGame(selectedNode);

            backpropagate(selectedNode, reward);
        }

        Optional<MCTNode> bestNode = rootNode.getChildren().stream().max(Comparator.comparingInt(MCTNode::getTimesVisited));
        while(bestNode.isPresent()) {
            return bestNode.get().nextDir;
        }

        return null;
    }
    
    private MCTNode treePolicy(MCTNode currentNode) {
        if(currentNode.isGameOver()) {
            return currentNode.parent != null ? currentNode.parent : currentNode;
        }
        
        if(!currentNode.isFullyExpanded()) {
            expandNode(currentNode);
            return currentNode.getBestChild();
        }
        
        if(currentNode.getChildren().isEmpty()) {
            return currentNode;
        }

        boolean allChildsVisitsAboveMinVisitCount = 
                currentNode.getChildren().parallelStream()
                    .map(MCTNode::getTimesVisited)
                    .min(Integer::compareTo).get() > 20;

        if(allChildsVisitsAboveMinVisitCount) {
            return treePolicy(currentNode.getBestChild());
        }
        else {
            return treePolicy(currentNode.getChildren().get(random.nextInt(currentNode.getChildren().size())));
        }
    }

    MCTNode expandNode(MCTNode parentNode) {
        ArrayList<DIR> pacmanMoves = parentNode.getPacmanMovesNotExpanded(Utils.TREE_DEPTH);
        assert !pacmanMoves.isEmpty();
    
        for(DIR move : pacmanMoves) {
            GameStateInterface childGameState = parentNode.gameState.copy();
            childGameState.next(move.ordinal(), ghostsController.getActions(childGameState));

            MCTNode child = new MCTNode(childGameState, parentNode.pathLengthInSteps);
            child.nextDir = move;
            child.parent = parentNode;
            parentNode.getChildren().add(child);
        }
    
        return parentNode;
    }

    // * May need to change in order to optimise Score
    private double simulateGame(MCTNode selectedNode) {
        GameStateInterface simGameState = selectedNode.gameState.copy();
        int totalSteps = Utils.TREE_DEPTH - selectedNode.pathLengthInSteps;
        int remainingSteps = totalSteps;
        SimResult lastSimResult = new SimResult();
        Node gamePreferredNode = Utils.gamePreferredNode(simGameState);

        while(!Utils.analyzeGameState(simGameState, lastSimResult, remainingSteps, lastSimResult.powerPillUnnecessarilyEaten, gamePreferredNode)) {
            ArrayList<DIR> availableMoves = Utils.getPacmanMovesAtJunctionWithoutReverse(simGameState);
            DIR pacmanDir = availableMoves.get(random.nextInt(availableMoves.size()));

            lastSimResult = Utils.simulateToNextJunctionOrLimit(simGameState, ghostsController, pacmanDir, remainingSteps, gamePreferredNode);

            remainingSteps -= lastSimResult.steps;
        }

        if(lastSimResult.levelComplete) {
            return 1d;
        }

        if(lastSimResult.diedDuringSim) {
            return 0d;
        }

        if(lastSimResult.powerPillUnnecessarilyEaten) {
            return 0.1d;
        }

        // if(Utils.getNumberActivePills(simGameState) == numOfActivePillsStart) {
        //     return 0.1d * (1.d / numOfActivePillsStart);
        // }

        if(lastSimResult.gamePreferredNodeHit) {
            return 0.8d;
        }

        // return 1.0d - (Utils.getNumberActivePills(simGameState) / (double) numOfActivePillsStart);
        return 0.6d;
    }

    private void backpropagate(MCTNode selectedNode, double reward) {
        while(selectedNode != null) {
            selectedNode.updateReward(reward);
            selectedNode = selectedNode.parent;
        }
    }
}
