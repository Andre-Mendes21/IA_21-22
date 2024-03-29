package games.pacman.controllers;

import games.pacman.core.FullGame;
import games.pacman.core.GameModel;
import games.pacman.core.PacMan;
import games.pacman.maze.OldMaze;
import games.pacman.maze.MazeNode;
import games.pacman.ghost.Ghost;

/**
 *  Smart controller:
 *
 *  Main ideas - make a plan, and execute the plan.
 *
 *  Some key concepts: the distance to each one
 *
 *  So: here's an idea: use an expansion method to search for each node,
 *  but as we do it, consider also how each ghost could move - as a wave-front
 *
 */
public class SmartController implements PacController {


    OldMaze maze;
    Ghost[] ghosts;
    PacMan pacman;

    public SmartController(GameModel model) {
        this(model.maze, model.ghosts, model.pacman);
    }

    public SmartController(FullGame model) {
        this(model.maze, model.ghosts, model.pacman);
    }

    public SmartController(OldMaze maze, Ghost[] ghosts, PacMan pacman) {
        this.maze = maze;
        this.ghosts = ghosts;
        this.pacman = pacman;
    }

    public int getDirection() {
        // System.out.println("Called getDirection");
        // get all the successors of the current node
        try {
            MazeNode[] poss = pacman.current.succ();
            // Node[] poss = pacman.current.getOtherNext()
            MazeNode best = getBest(poss, pacman.previous);
            // Node best = getBest(poss, null);
            // System.out.println("Current: " + pacman.current);
            // System.out.println("Best: " + best);
            // System.out.println(maze.direction(pacman.current,best));
            return maze.direction(pacman.current, best);
        } catch (Exception e) {
            // System.out.println(e);
            return CENTRE;
        }
    }

    /*
    private MazeNode getBest(MazeNode[] poss) {
        // a free choice of nodes
        MazeNode bestNode = null;
        int bestScore = 0;
        // the best node is the one with the maximum score
        // i.e. the one furthest from the nearest ghost
        for (int i = 0; i < poss.length; i++) {
            int score = score(poss[i]);
            // System.out.println(i + "\t " + poss[i] + "\t dist: " + score);
            if (score >= bestScore) {
                bestScore = score;
                bestNode = poss[i];
            }
        }
        return bestNode;
    }
    */
    
    private MazeNode getBest(MazeNode[] poss, MazeNode previous) {
        MazeNode bestNode = null;
        int bestScore = 0;
        // the best node is the one with the maximum score
        // i.e. the one furthest from the nearest ghost
        // set previous to null to allow it to turn back on itself
        previous = null;
        for (int i = 0; i < poss.length; i++) {
            if (poss[i] != previous) {
                int score = score(poss[i]);
                // System.out.println(i + "\t " + poss[i] + "\t dist: " + score);
                if (score >= bestScore) {
                    bestScore = score;
                    bestNode = poss[i];
                }
            }
        }
        return bestNode;
    }

    // for now return score as the shortest path distance to
    // the nearest ghost

    // an improved version would be as follows: it would
    // consider the current direction of each ghost
    //

    public int score(MazeNode node) {
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < ghosts.length; i++) {
            // need the distance between this node and the ghost
            MazeNode gn = ghosts[i].current;
            if (gn != null) {
                int d = maze.dist[node.ix][ghosts[i].current.ix];
                // System.out.println(i + "\t " + d);
                if (d < minDist) {
                    minDist = d;
                }
            }
        }
        // System.out.println(node + " \t " + minDist);
        return minDist;
    }
}