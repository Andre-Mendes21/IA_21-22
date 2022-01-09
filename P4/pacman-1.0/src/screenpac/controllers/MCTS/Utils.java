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

	final static int TREE_DEPTH = 140;

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
		return gameState.getPills().isEmpty() || gameState.getPowers().isEmpty();
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

	public static boolean isJunction(Node node) {
		return node.adj.size() > 2;
	}

	public static SimResult simulateUntilNextJunction(GameStateInterface gameState, GhostTeamController ghostController, DIR dir, Node gamePreferredNode) {
		SimResult result = simulateToNextJunctionOrLimit(gameState, ghostController, dir, TREE_DEPTH, gamePreferredNode);

		return result;
	}

	public static SimResult simulateToNextJunctionOrLimit(GameStateInterface gameState, GhostTeamController ghostController, DIR dir, int maxSteps, Node gamePreferredNode) {
		SimResult result = new SimResult();
		PathPlanner pathPlanner = new PathPlanner(gameState.getMaze());

		DIR pacmanNextDir = dir;
		boolean hadEdibleGhost;
		
		do {
			Node currentPacmanNode = gameState.getPacman().current; 
			Node nextNode = pathPlanner.getPath(currentPacmanNode, gamePreferredNode).remove(0);
			int nextDir = Utilities.getDirection(currentPacmanNode, nextNode);

			pacmanNextDir = DIR.values()[nextDir];
			// pacmanNextDir = getPacmanMoveAlongPath(gameState, pacmanNextDir);

			hadEdibleGhost = hasEdibleGhost(gameState);
			gameState.next(pacmanNextDir.ordinal(), ghostController.getActions(gameState));

			++result.steps;
			--maxSteps;
		} while(!analyzeGameState(gameState, result, maxSteps, hadEdibleGhost, gamePreferredNode));

		result.gameState = gameState;

		return result;
	}

	public static boolean analyzeGameState(GameStateInterface gameState, SimResult result, int remainingSteps, boolean hadEdibleGhost, Node gamePreferredNode) {
		boolean shouldStop = false;

		if(isPillsCleared(gameState)) {
			result.levelComplete = true;
			shouldStop = true;
		}

		if(agentDeathSilent(gameState)) {
			result.diedDuringSim = true;
			shouldStop = true;
		}

		if(wasPowerEaten(gameState) && (hadEdibleGhost || !wasAGhostClose(gameState))) {
			result.powerPillUnnecessarilyEaten = true;
			shouldStop = true;
		}

		if(gameState.getPacman().current.equals(gamePreferredNode)) {
			result.gamePreferredNodeHit = true;
			shouldStop = true;
		}
		
		if(remainingSteps <= 0) {
			shouldStop = true;
		}
		return shouldStop;
	}

	// FIXME: May not work as intended, needs to find shortest distance to ghost, Manhatten distance may not be suited
	public static boolean wasAGhostClose(GameStateInterface gameState) {
		Node pacmanNode = gameState.getPacman().current;
		
		for(GhostState ghost : gameState.getGhosts()) {
			Node ghostNode = ghost.current;
			if(Utilities.manhattan(pacmanNode, ghostNode) <= 20) {
				return true;
			}
		}
		return false;
	}


	public static DIR getPacmanMoveAlongPath(GameStateInterface gameState, DIR dir) {
		ArrayList<DIR> moves = getPacmanMovesWithoutNeutral(gameState);
		if(moves.contains(dir)) {
			return dir;
		}
		moves.remove(gameState.getPacman().getPacmanLastMoveMade().opposite());

		return moves.get(0);
	}

	public static boolean hasEdibleGhost(GameStateInterface gameState) {
		for(GhostState ghost : gameState.getGhosts()) {
			if(ghost.edible()) {
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
		int G = 20;
		
		for(GhostState ghost : gameState.getGhosts()) {
			Node ghostNode = ghost.current;
			if(ghost.edible() && Utilities.manhattan(pacmanNode, ghostNode) < G) {
				ghostNode.col = Color.CYAN;
				return ghostNode;
			}
		}
		return null;
	}

	public static Node nearestPill(GameStateInterface gameState) {
		double P = ((double) TREE_DEPTH / 5d) - 1d;
		NearestPillDistance npd = new NearestPillDistance();
		Node pacmanNode = gameState.getPacman().current;

		for(GhostState ghost : gameState.getGhosts()) {
			if(!ghost.edible() && npd.score(gameState, pacmanNode) < P) {
				return npd.closest;
			}
		}
		return Utilities.getClosest(pacmanNode.adj, npd.closest, gameState.getMaze());
	}

	public static DIR checkNearGhosts(GameStateInterface gameState) {
        DIR move = DIR.NEUTRAL;
        double maxDistance = Integer.MIN_VALUE;
		Node pacmanNode = gameState.getPacman().current;
		int G = 20;
			
		for(GhostState ghost : gameState.getGhosts()) {
			Node ghostNode = ghost.current;
			if(!ghost.edible() && Utilities.manhattan(pacmanNode, ghostNode) <= G) {
				for (Node a : pacmanNode.adj) {
					double distance = Utilities.manhattan(a, ghostNode);
					
					if (distance > maxDistance) {
						maxDistance = distance;
						move = DIR.values()[Utilities.getDirection(a, ghostNode)].opposite();
					}
				}
				return move;
			}
		}
			
		return DIR.NEUTRAL;
    }
}
