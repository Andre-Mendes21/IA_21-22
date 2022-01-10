package screenpac.controllers.MCTS;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.TimeUnit;

import screenpac.controllers.PathPlanner;
import screenpac.controllers.MCTS.Utils.DIR;
import screenpac.extract.Constants;
import screenpac.features.Utilities;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameStateInterface;
import screenpac.model.Node;
import screenpac.sound.PlaySound;

public class MCTS implements Constants {

    private final MCTNode rootNode;
    private final Random random = new Random();
    private final GhostTeamController ghostsController;

    private final int numOfActivePillsStart;
    private final long timeDue;
    private Node gamePreferredNode;

    public MCTS(GameStateInterface gameState, GhostTeamController ghostsController, long timeDue) {
        this.rootNode = new MCTNode(gameState.copy());
        this.ghostsController = ghostsController;
        this.gamePreferredNode = Utils.gamePreferredNode(rootNode.gameState);

        this.numOfActivePillsStart = Utils.getNumberActivePills(rootNode.gameState);
        this.timeDue = timeDue;
    }

    public GameStateInterface runMCTS() {
        Long timeStamp = System.currentTimeMillis();
        int i = 0;

        while(System.currentTimeMillis() + 2 <= timeStamp + timeDue) {
            MCTNode selectedNode = treePolicy(rootNode);

            double reward = simulateGame(selectedNode);

            backpropagate(selectedNode, reward);
        }

        Optional<MCTNode> bestNode = rootNode.getChildren().stream().max(Comparator.comparingDouble(MCTNode::getReward));
        while(bestNode.isPresent()) {
            return bestNode.get().gameState;
        }

        return null;
    }
    
    private MCTNode treePolicy(MCTNode currentNode) {
        if(currentNode.curTreeDepth > Utils.TREE_DEPTH || currentNode.isGameOver()) {
            return currentNode.parent != null ? currentNode.parent : currentNode;
            // return currentNode;
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
                    .min(Integer::compareTo).get() > Utils.MIN_NODE_VISIT_COUNT;

        if(allChildsVisitsAboveMinVisitCount) {
            return treePolicy(currentNode.getBestChild());
        }
        else {
            return treePolicy(currentNode.getChildren().get(random.nextInt(currentNode.getChildren().size())));
        }
    }

    MCTNode expandNode(MCTNode parentNode) {
        PlaySound.disable();
        ArrayList<DIR> pacmanMoves = parentNode.getPacmanMovesNotExpanded();
        assert !pacmanMoves.isEmpty();
    
        for(DIR move : pacmanMoves) {
            GameStateInterface childGameState = parentNode.gameState.copy();
            childGameState.next(move.ordinal(), ghostsController.getActions(childGameState));

            MCTNode child = new MCTNode(childGameState);
            child.nextDir = move;
            child.parent = parentNode;
            parentNode.getChildren().add(child);
        }
        PlaySound.enable();
        return parentNode;
    }

    // * May need to change in order to optimise Score
    private double simulateGame(MCTNode selectedNode) {
        PlaySound.disable();
        GameStateInterface simGameState = selectedNode.gameState.copy();

        for(int depth = selectedNode.curTreeDepth; depth < Utils.P; depth++) {
            if(Utils.isPillsCleared(simGameState) || simGameState.terminal())  {
                break;
            }
            ArrayList<DIR> legalMoves = Utils.getPacmanMovesAtJunctionWithoutReverse(simGameState);
            DIR randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
            simGameState.next(randomMove.ordinal(), ghostsController.getActions(simGameState));

        }
        PlaySound.enable();

        if(Utils.agentDeathSilent(simGameState)) {
            return -(3000d + simGameState.getScore());
        }

        if(Utils.wasPowerEaten(simGameState) && (Utils.hasEdibleGhost(simGameState) || !Utils.wasAGhostClose(simGameState))) {
            return -(25d + simGameState.getScore());
        }

        double levelComplete = 0.0d;
        if(Utils.isPillsCleared(simGameState)) {
            levelComplete = 5d + simGameState.getLevel();
        }

        double gamePreferredNodeHit;
        if(simGameState.getPacman().current.equals(gamePreferredNode)) {
            gamePreferredNodeHit = 0.8d;
        }
        else {
            gamePreferredNodeHit = 0.6d;
        }

        return (1 + gamePreferredNodeHit + simGameState.getScore() + (levelComplete));
    }

    private void backpropagate(MCTNode selectedNode, double reward) {
        while(selectedNode != null) {
            selectedNode.updateReward(reward);
            selectedNode = selectedNode.parent;
        }
    }
}
