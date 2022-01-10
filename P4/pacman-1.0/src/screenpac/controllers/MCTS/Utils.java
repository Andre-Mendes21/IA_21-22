package screenpac.controllers.MCTS;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import screenpac.controllers.PathPlanner;
import screenpac.extract.Constants;
import screenpac.ghosts.GhostTeamController;
import screenpac.model.GameState;
import screenpac.model.GameStateInterface;
import screenpac.model.GhostState;
import screenpac.model.Node;
import screenpac.features.NearestPillDistance;
import screenpac.features.Utilities;

public class Utils implements Constants {

	final static int TREE_DEPTH = 500;
	final static double P = ((double) TREE_DEPTH / 5d) - 1d;
	final static int G = 50;
    public static final int MIN_NODE_VISIT_COUNT = 35;

	public enum DIR {
		UP (0) {
			public DIR opposite() {
				return DIR.DOWN;
			}

		;},
		RIGHT (1) {
			public DIR opposite() {
				return DIR.LEFT;
			}

		;},
		DOWN (2) {
			public DIR opposite() {
				return DIR.UP;
			}

		;},
		LEFT (3) {
			public DIR opposite() {
				return DIR.RIGHT;
			}

		;},
		NEUTRAL (4) {
			public DIR opposite() {
				return DIR.NEUTRAL;
			}

		;};

		private final int dir;

		public abstract DIR opposite();
		public int getDir() {
			return dir;
		}

		private DIR(int dir) {
			this.dir = dir;
		}
	}

	public static int getNumberActivePills(GameStateInterface gameState) {
		return gameState.getPills().cardinality();
	}

	public static int getNumberActivePowerPills(GameStateInterface gameState) {
		return gameState.getPowers().cardinality();
	}

	public static boolean isPillsCleared(GameStateInterface gameState) {
		return gameState.getPills().isEmpty() && gameState.getPowers().isEmpty();
	}

	public static boolean agentDeathSilent(GameStateInterface gameState) {
		for (GhostState g : gameState.getGhosts()) {
			if (!g.edible() && !g.returning() && GameState.overlap(g, gameState.getPacman())) {
				return true;
			}
		}
		return false;
	}

	public static boolean wasPowerEaten(GameStateInterface gameState) {
		boolean powerPillWasEaten = false;
		int ix = gameState.getPacman().current.powerIndex;
		if (ix >= 0) {
			if (gameState.getPowers().get(ix)) {
				powerPillWasEaten = true;
			}
		}
		return powerPillWasEaten;
	}

	public static DIR[] getAllPossibleMoves(Node currentNode) {
		DIR[] allPossiblePosibleMoves = new DIR[currentNode.adj.size()];

		int i = 0;
		for (Node node : currentNode.adj) {
			int dir = Utilities.getDirection(currentNode, node);
			allPossiblePosibleMoves[i++] = DIR.values()[dir];
		}
		return allPossiblePosibleMoves;
	}

	public static ArrayList<DIR> getPacmanMovesWithoutNeutral(GameStateInterface gs) {
		DIR[] possibleMoves = getAllPossibleMoves(gs.getPacman().current);

		ArrayList<DIR> pacmanMoves = new ArrayList<>(Arrays.asList(possibleMoves));
		pacmanMoves.remove(DIR.NEUTRAL);

		return pacmanMoves;
	}

	public static boolean wasAGhostClose(GameStateInterface gameState) {
		Node pacmanNode = gameState.getPacman().current;
		
		for(GhostState ghost : gameState.getGhosts()) {
			Node ghostNode = ghost.current;
			if(gameState.getMaze().dist(pacmanNode, ghostNode) <= G) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasEdibleGhost(GameStateInterface gameState) {
		for(GhostState ghost : gameState.getGhosts()) {
			if(ghost.edible() && !ghost.returning()) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<DIR> getPacmanMovesAtJunctionWithoutReverse(GameStateInterface gameState) {
		ArrayList<DIR> moves = getPacmanMovesWithoutNeutral(gameState);
		moves.remove(gameState.getPacman().getPacmanLastMoveMade().opposite());

		return moves;
	}

	//======================================================================================================//
	
	public static Node gamePreferredNode(GameStateInterface gameState) {
		Node nearestGhostNode = Utils.nearestEdibleGhost(gameState);
		if(nearestGhostNode != null) {
			return nearestGhostNode;
		}

		return Utils.nearestPill(gameState);
	}

	public static Node nearestEdibleGhost(GameStateInterface gameState) {
		Node pacmanNode = gameState.getPacman().current;
		Node closestGhost = null;
		for(GhostState ghost : gameState.getGhosts()) {
			Node ghostNode = ghost.current;
			if((ghost.edible() && !ghost.returning()) && gameState.getMaze().dist(pacmanNode, ghostNode) <= G) {
				closestGhost = ghostNode;
				closestGhost.col = Color.CYAN;
			}
		}
		return closestGhost;
	}

	public static Node nearestPill(GameStateInterface gameState) {
		NearestPillDistance npd = new NearestPillDistance();
		Node pacmanNode = gameState.getPacman().current;
		double pillDist = npd.score(gameState, pacmanNode);
		
		for(GhostState ghost : gameState.getGhosts()) {
			if((!ghost.edible() || ghost.returning()) && pillDist < P) {
				npd.closest.col = Color.green;
				return npd.closest;
			}
		}

		PathPlanner pathPlanner = new PathPlanner(gameState.getMaze());
		ArrayList<Node> path = pathPlanner.getPath(pacmanNode, npd.closest);
		Node nearestNode = path.get(path.size() - 1);
		for(int i = path.size() - 1; i > 0; i--) {
			if(gameState.getMaze().dist(pacmanNode, nearestNode) < P) {
				nearestNode = path.get(i);
				break;
			}
			nearestNode = path.get(i);
		}

		nearestNode.col = Color.ORANGE;
		return nearestNode;
	}
}
