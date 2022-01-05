package screenpac.features;

import screenpac.model.*;
import screenpac.controllers.MCTS.SimResult;
import screenpac.extract.Constants;
import screenpac.ghosts.GhostTeamController;

import java.util.ArrayList;
import java.util.Arrays;

public class Utilities implements Constants {
    public static Node getMin(ArrayList<Node> nodes, NodeScore f, GameStateInterface gs) {
        double best = Double.MAX_VALUE;
        // selected current
        Node sel = null;
        for (Node n : nodes) {
            if (f.score(gs, n) < best) {
                best = f.score(gs, n);
                sel = n;
            }
        }
        return sel;
    }

    public static int getMinDir(ArrayList<Node> nodes, Node cur, NodeScore f, GameStateInterface gs) {
        Node target = getMin(nodes, f, gs);
        // System.out.println("Targetting: " + target);
        return getWrappedDirection(cur, target, gs.getMaze());
    }

    public static int getClosestDir(ArrayList<Node> nodes, Node cur, NodeScore f, GameState gs) {
        Node target = getMin(nodes, f, gs);
        // System.out.println("Targetting: " + target);
        return getWrappedDirection(cur, target, gs.getMaze());
    }

    public static Node getClosest(ArrayList<Node> nodes, Node target, MazeInterface maze) {
        // if the target current is null then print a warning
        if (target == null) {
            System.out.println("Warning: null target in Utilities.getClosest()");
            return nodes.get(rand.nextInt(nodes.size()));
        }
        double best = Double.MAX_VALUE;
        // selected current
        Node sel = null;
        for (Node n : nodes) {
            int d = maze.dist(n, target);
            if (d < best) {
                best = d;
                sel = n;
            }
        }
        return sel;
    }

    public static int getDirection(Node from, Node to) {
        // may not need the sign function here
        int xDiff = sgn(to.x - from.x);
        int yDiff = sgn(to.y - from.y);
        for (int i = 0; i < dx.length; i++) {
            if (dx[i] == xDiff && dy[i] == yDiff) {
                // System.out.println("Direction: " + from + "\t " + to);
//                System.out.println("diffs: " + xDiff + "\t " + yDiff);
//                System.out.println("returning: " + i );
//                System.out.println("");
                return i;
            }
        }
        System.out.println("Error in Util.getDirection");
        // throw new RuntimeException("Error in getDirection " + from + " : " + to);
        return 0;
    }


    public static int getWrappedDirection(Node a, Node b, MazeInterface maze) {
        int w = maze.getWidth();
        int h = maze.getHeight();
        for (int i = 0; i < dx.length; i++) {
            if (((a.x + dx[i] + w) % w == b.x) && ((a.y + dy[i] + h) % h == b.y)) {
                return i;
            }
        }
        // something's wrong
        System.out.println("Non-adjacent nodes in getWrappedDirection");
        return NEUTRAL;
    }

    public static int sgn(int x) {
        if (x < 0) return -1;
        if (x > 0) return 1;
        return 0;
    }

    public static int manhattan(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public static ArrayList<Constants.DIR> getPacmanMovesWithoutNeutral(GameStateInterface gs) {
        DIR[] possibleMoves = gs.getPacman().current.getAllPossibleMoves();

        ArrayList<Constants.DIR> pacmanMoves = new ArrayList<>(Arrays.asList(possibleMoves));
        pacmanMoves.remove(DIR.NEUTRAL);

        return pacmanMoves;
    }

    public static boolean isJunction(Node node) {
        return node.adj.size() > 2;
    }

    public static SimResult simulateUntilNextJunction(GameStateInterface gameState, GhostTeamController ghostController, DIR dir) {
        SimResult result = simulateToNextJunctionOrLimit(gameState, ghostController, dir, Integer.MAX_VALUE);

        return result;
    }

    public static SimResult simulateToNextJunctionOrLimit(GameStateInterface gameState, GhostTeamController ghostController, DIR dir, int maxSteps) {
        SimResult result = new SimResult();

        DIR currentPacmanDir = dir;
        boolean hadEdibleGhost;
        do {
            currentPacmanDir = getPacmanMoveAlongPath(gameState, dir);

            hadEdibleGhost = hasEdibleGhost(gameState);
            gameState.next(currentPacmanDir.ordinal(), ghostController.getActions(gameState));

            --result.steps;
            --maxSteps;
        } while(!analyzeGameState(gameState, result, maxSteps, hadEdibleGhost) && !isJunction(gameState.getPacman().current));

        result.gameState = gameState;

        return result;
    }

    public static boolean analyzeGameState(GameStateInterface gameState, SimResult result, int remainingSteps, boolean hadEdibleGhost) {
        boolean shouldStop = false;

        if(gameState.getNumberActivePills() == 0) {
            result.levelComplete = true;
            shouldStop = true;
        }

        if(gameState.agentDeathSilent()) {
            result.didedDuringSim = true;
            shouldStop = true;
        }

        if(gameState.wasPowerEaten() && (hadEdibleGhost || !wasAGhostClose(gameState))) {
            result.powerPillUnncessarilyEaten = true;
            shouldStop = true;
        }

        if(remainingSteps <= 0) {
            shouldStop = true;
        }
        return shouldStop;
    }

    public static boolean wasAGhostClose(GameStateInterface gameState) {
        Node pacmanNode = gameState.getPacman().current;
        
        for(GhostState ghost : gameState.getGhosts()) {
            Node ghostNode = ghost.current;
            if(manhattan(pacmanNode, ghostNode) < 20) {
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
}
