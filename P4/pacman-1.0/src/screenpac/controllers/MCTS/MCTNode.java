package screenpac.controllers.MCTS;

import java.util.*;
import java.util.stream.Collectors;

import screenpac.controllers.MCTS.Utils.DIR;
import screenpac.extract.Constants;
import screenpac.model.GameStateInterface;

public class MCTNode implements Constants {
	MCTNode parent;
	DIR parentAction;

	private int numOfVisits;
	private double reward;

	private ArrayList<MCTNode> children;
	GameStateInterface gameState;

	private boolean canUpdate;

	int pathLengthInSteps;

	/**
	 * Default Constructor
	 * @param game	 The copied game state
	 */
	public MCTNode(GameStateInterface game, int pathLengthInSteps) {
		this.parent = null;
		this.parentAction = null;

		this.numOfVisits = 0;
		this.reward = 0;

		this.children = new ArrayList<MCTNode>();
		this.gameState = game;

		this.canUpdate = true;
		this.pathLengthInSteps = pathLengthInSteps;
	}

	public double getReward() {
		double childrenMax = children.stream().map(MCTNode::getReward).max(Double::compareTo).orElse(0d);

		return Math.max(childrenMax * 0.5, reward);
	}
	
	public double getUCB_Tuned() {
		double reward = this.getReward();
		double exploit = reward / numOfVisits;

		double rewardSqr = reward * reward;
		double rewardSqrExploit = rewardSqr / numOfVisits;
		
		double exploitSqr = exploit * exploit;
		double explor = Math.sqrt( (Math.log(parent.numOfVisits) / this.numOfVisits) 
										* Math.min(0.25, rewardSqrExploit - exploitSqr 
										+ Math.sqrt((2d * Math.log(parent.numOfVisits) / numOfVisits))) );
		return exploit + explor;
	}

	public boolean isFullyExpanded() {
		// FIXME: Change 140 magic number to Tree_Depth constant
		return getPacmanMovesNotExpanded(140).isEmpty();
	}

	public ArrayList<DIR> getPacmanMovesWithoutReverse() {
		ArrayList<DIR> moves = Utils.getPacmanMovesWithoutNeutral(this.gameState);

		if(this.parent != null) {
			moves.remove(parentAction.opposite());
		} 

		return moves;
	}

	public ArrayList<DIR> getPacmanMovesNotExpanded(final int MAX_PATH_LENGTH) {
		if(this.pathLengthInSteps > 0.8f * MAX_PATH_LENGTH) {
			return new ArrayList<>();
		}
		else {
			ArrayList<DIR> moves = this.getPacmanMovesWithoutReverse();

			moves.removeAll(children.parallelStream().map(child -> child.parentAction).collect(Collectors.toList()));

			return moves;
		}
	}

	public MCTNode getBestChild() {
		MCTNode bestChild = null;
		double bestUCBValue = Double.MIN_VALUE;

		for(MCTNode child : children) {
			double childValue = child.getUCB_Tuned();
			if(childValue > bestUCBValue) {
				bestChild = child;
				bestUCBValue = childValue;
			}
		}

		if(bestChild == null) {
			return children.get(0);
		}

		return bestChild;
	}

	public int getTimesVisited() {
		return this.numOfVisits;
	}

	public boolean isGameOver() {
		return Utils.agentDeathSilent(gameState) || Utils.getNumberActivePills(gameState) == 0;
	}

	public void updateReward(double deltaReward) {
		if(canUpdate) {
			this.reward += deltaReward;
		}
		++this.numOfVisits;
	}

	public void setCanUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}

	public String path() {
        Stack<MCTNode> pathStack = new Stack<>();
        MCTNode currentNode = this;

        while (currentNode.parent != null) {
            pathStack.push(currentNode);
            currentNode = currentNode.parent;
        }

        StringBuilder sb = new StringBuilder();
        while (!pathStack.empty()) {
            MCTNode node = pathStack.pop();
            sb.append('/');
            sb.append(node.parentAction);
        }

        return sb.toString();
    }


	/**
	 * Indicates whether this node is leaf node
	 * 
	 * @return True if node is leaf, False otherwise
	 */
	public boolean isLeafNode() {
		return this.children == null;
	}

	public ArrayList<MCTNode> getChildren() {
		return this.children;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(path() + " distance: " + pathLengthInSteps);
        sb.append(" - Children ");
        for(MCTNode child : children) {
            sb.append(" || [" + child.parentAction + "] ");
            sb.append("r/s: " + child.reward + "/" + child.numOfVisits);
        }

        return sb.toString();
    }
}