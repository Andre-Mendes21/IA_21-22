package screenpac.controllers.MCTS;

import java.util.*;

import screenpac.extract.Constants;
import screenpac.model.Agent;
import screenpac.model.GameState;
import screenpac.model.Node;

public class MCTNode implements Constants {
	private int move;
	private MCTNode parent;
	private int numOfVisits;
	private int scoreBonus;
	private Map<Object, MCTNode> children;
	private double mean;
	private boolean moveEatsPowerPill;
	private boolean moveEatsPills;

	/**
	 * Default Constructor
	 */
	public MCTNode() {
		this.numOfVisits = 0;
		this.move = Constants.NEUTRAL;
		this.scoreBonus = 0;
		this.parent = null;
	}

	/**
	 * Constructor for children
	 * 
	 * @param parent The parent of the node
	 * @param move   The move contained in the node
	 */
	public MCTNode(MCTNode parent, int move) {
		this.parent = parent;
		this.move = move;
	}

	/**
	 * Updates the mean score as well as the
	 * number of visits to the node
	 * 
	 * @param score Score in the visit to be updated
	 */
	public void updateScore(int score) {
		this.mean = (double) (this.mean * this.numOfVisits + score) / (this.numOfVisits + 1);
		this.numOfVisits++;
	}

	/**
	 * Adds bonus to the mean score
	 * 
	 * @param bonus Bonus given by the evaluators
	 */
	public void addScoreBonus(int bonus) {
		this.scoreBonus += bonus;
	}

	public void expand(GameState game) {
		ArrayList<Node> currMaze = game.maze.getMap();
		Agent pacman = game.pacMan;
		Node currPacmanPos = pacman.current;
		int[] possibleMoves = currMaze.get(currPacmanPos.nodeIndex).getAllPossibleMoves();

		this.children = new HashMap<>(possibleMoves.length);

		for(int move : possibleMoves) {
			this.children.put(move, new MCTNode(this, move));
		}
	}

	/**
	 * Indicates whether this node is leaf node
	 * 
	 * @return True if node is leaf, False otherwise
	 */
	public boolean isLeafNode() {
		return this.children == null;
	}

	/**
	 * Gets the move contained in this node
	 * 
	 * @return move
	 */
	public int getMove() {
		return this.move;
	}

	/**
	 * Gets the number of visits to this node
	 * 
	 * @return number of visits
	 */
	public int getNumOfVisits() {
		return this.numOfVisits;
	}

	/**
	 * Gets the children of this node
	 * 
	 * @return values of children, if any; null otherwise
	 */
	public Collection<MCTNode> getChildren() {
		if (this.children == null)
			return null;
		return this.children.values();
	}

	/**
	 * Gets the parent of this node
	 * 
	 * @return parent
	 */
	public MCTNode getParent() {
		return this.parent;
	}

	/**
	 * Gets the average score of the node
	 * 
	 * @return average score
	 */
	public double getAverageScore() {
		if (this.numOfVisits > 0)
			return this.mean + this.scoreBonus;
		return this.scoreBonus;
	}

	/**
	 * Indicates whether the current move eats any power pills
	 * 
	 * @return true if current move eats any power pills; false otherwise
	 */
	public boolean isMoveEatsPowerPill() {
		return this.moveEatsPowerPill;
	}

	/**
	 * Sets indicator variable to true if current move eats any power pill, false
	 * otherwise
	 * 
	 * @param moveEatsPowerPill
	 */
	public void setMoveEatsPowerPill(boolean moveEatsPowerPill) {
		this.moveEatsPowerPill = moveEatsPowerPill;
	}

	/**
	 * Indicates whether current move eats any pills
	 * 
	 * @return true if current move eats any pills; false otherwise
	 */
	public boolean isMoveEatsPills() {
		return this.moveEatsPills;
	}

	/**
	 * Sets indicator variable to true if current move eats any pill, false
	 * otherwise
	 * 
	 * @param moveEatsPills
	 */
	public void setMoveEatsPills(boolean moveEatsPills) {
		this.moveEatsPills = moveEatsPills;
	}

	/**
	 * Indicates whether any of the children has moves that eat pills
	 * 
	 * @return true if any of the children has any move that can eat pills, false
	 *         otherwise
	 */
	public boolean isEatPillsInFuture() {
		if(this.children == null)
			return false;
		for(MCTNode child : this.children.values()) {
			if(child.isMoveEatsPills())
				return true;
		}
		return false;
	}
}