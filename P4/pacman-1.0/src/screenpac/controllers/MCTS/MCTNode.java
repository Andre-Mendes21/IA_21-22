package screenpac.controllers.MCTS;

import java.util.*;

import screenpac.extract.Constants;
import screenpac.model.Agent;
import screenpac.model.GameState;
import screenpac.model.GameStateInterface;
import screenpac.model.Node;

public class MCTNode implements Constants {
	private int move;
	private MCTNode parent;
	private int numOfVisits;
	private int reward;
	private ArrayList<MCTNode> children = new ArrayList<>();
	private GameStateInterface gameState;

	/**
	 * Default Constructor
	 * @param game	 The copied game state
	 */
	public MCTNode(GameStateInterface game) {
		this.numOfVisits = 0;
		this.move = Constants.NEUTRAL;
		this.reward = 0;
		this.parent = null;
		this.gameState = game;
	}

	/**
	 * Constructor for children
	 * 
	 * @param parent The parent of the node
	 * @param move   The move contained in the node
	 * @param game	 The copied game state
	 */
	public MCTNode(MCTNode parent, int move, GameStateInterface game) {
		this.parent = parent;
		this.move = move;
		this.gameState = game;
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

	public void expand(GameState game) {
		
	}


	/**
	 * Indicates whether this node is leaf node
	 * 
	 * @return True if node is leaf, False otherwise
	 */
	public boolean isLeafNode() {
		return this.children == null;
	}
}