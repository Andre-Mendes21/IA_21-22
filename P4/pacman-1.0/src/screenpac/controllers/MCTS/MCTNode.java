package screenpac.controllers.MCTS;

import java.util.*;
import java.util.stream.Collectors;

import screenpac.controllers.MCTS.Utils.DIR;
import screenpac.extract.Constants;
import screenpac.model.GameStateInterface;

public class MCTNode implements Constants {
	MCTNode parent;
	DIR nextDir;

	private int numOfVisits;
	private double reward;

	private ArrayList<MCTNode> children;
	GameStateInterface gameState;
	int curTreeDepth;
	private ArrayList<DIR> legalMoves;

	private boolean canUpdate;


	/**
	 * Root node Constructor
	 * @param game	 The copied game state
	 */
	public MCTNode(GameStateInterface game) {
		this.parent = null;
		this.nextDir = null;

		this.numOfVisits = 0;
		this.reward = 0;

		this.gameState = game;
		this.curTreeDepth = 0;
		this.legalMoves = Utils.getPacmanMovesWithoutNeutral(gameState);
		this.children = new ArrayList<MCTNode>(legalMoves.size());

		this.canUpdate = true;
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
										+ Math.sqrt(((2d * Math.log(parent.numOfVisits)) / numOfVisits))) );
		return exploit + explor;
	}

	public boolean isFullyExpanded() {
		return !children.isEmpty();
	}

	public ArrayList<DIR> getPacmanMovesWithoutReverse() {
		ArrayList<DIR> moves = Utils.getPacmanMovesWithoutNeutral(this.gameState);

		if(this.parent != null) {
			moves.remove(nextDir.opposite());
		} 

		return moves;
	}

	public ArrayList<DIR> getPacmanMovesNotExpanded() {
		ArrayList<DIR> moves = this.getPacmanMovesWithoutReverse();

		moves.removeAll(children.parallelStream().map(child -> child.nextDir).collect(Collectors.toList()));

		return moves;
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
		return Utils.agentDeathSilent(gameState) || Utils.getNumberActivePills(gameState) + Utils.getNumberActivePowerPills(gameState) == 0;
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
}