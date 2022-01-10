package screenpac.controllers.MCTS;

import java.util.*;
import screenpac.controllers.MCTS.Utils.DIR;
import screenpac.extract.Constants;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameStateInterface;
import screenpac.model.Node;
import screenpac.sound.PlaySound;

public class MCTS implements Constants {

    private final MCTNode rootNode;
    private final Random random = new Random();
    private final GhostTeamController ghostsController;

    private final long timeDue;
    private Node gamePreferredNode;

    /**
     * 
     * @param gameState         The copied current gameState
     * @param ghostsController  The ghosts controller for simulation
     * @param timeDue           The maximum time allowed for simulation
     */
    public MCTS(GameStateInterface gameState, GhostTeamController ghostsController, long timeDue) {
        this.rootNode = new MCTNode(gameState.copy());
        this.ghostsController = ghostsController;
        this.gamePreferredNode = Utils.gamePreferredNode(rootNode.gameState);

        this.timeDue = timeDue;
    }

    /**
     * This is where the selection, expansion, simulation and backpropagation of the MCTS algorithm is done
     * under the timeDue time limit
     * @return  The gameState from the best MCTNode child given its reward, if none are found returns null
     */
    public GameStateInterface runMCTS() {
        Long timeStamp = System.currentTimeMillis();

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
    
    /**
     * Using the UCT Tuned value selects the bestNode not yet expanded
     * if the minimum visit for a MCTNode is superior to Utils.MIN_NODE_VISIT_COUNT,
     * otherwise select a random child
     * @param currentNode The node to be evaluated for selection
     * @return The selected best MCTNode 
     */
    private MCTNode treePolicy(MCTNode currentNode) {
        if(currentNode.curTreeDepth > Utils.TREE_DEPTH || currentNode.isGameOver()) {
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
                    .min(Integer::compareTo).get() > Utils.MIN_NODE_VISIT_COUNT;

        if(allChildsVisitsAboveMinVisitCount) {
            return treePolicy(currentNode.getBestChild());
        }
        else {
            return treePolicy(currentNode.getChildren().get(random.nextInt(currentNode.getChildren().size())));
        }
    }

    /**
     * This method expands a given MCTNode with children with all possible directions 
     * Ms. Pacman can make
     * @param parentNode    MCTNode to expand 
     * @return The expanded parentNode
     */
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

    /**
     * This method simulates the game from the given MCTNode
     * a number of (TREE_DEPTH / 5) - 1 times. The simulation also stops if
     * either the current level ends or pacMan has zero lives left.
     * 
     * The MCTNode is rewarded given these parameters:
     *      - If the agent has died the reward is: -(3000 + the current score from the simulation)
     *      - If the agent ate a powerpill unnecessarily the reward is: -(25 + the current score from the simulation)
     *      - otherwise the reward is given by this formula: (1 + gamePreferredNodeHit + the current score from the simulation + levelComplete)
     * gamePreferredNode is a small reward (0.8) given to the MCTNode if a target Node is hit, otherwise the reward is 0.6.
     * levelComplete is given by the formula: 50 + the level number the simulation acheived
     * 
     * @param selectedNode MCTNode to be simulated
     * @return  The reward  at the end of the simulation
     */
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

    /**
     * As per the MCTS, this method backpropagates the given reward to the root MCTNode
     * @param selectedNode  The node to add the reward to
     * @param reward        The reward from the simulation
     */
    private void backpropagate(MCTNode selectedNode, double reward) {
        while(selectedNode != null) {
            selectedNode.updateReward(reward);
            selectedNode = selectedNode.parent;
        }
    }
}
