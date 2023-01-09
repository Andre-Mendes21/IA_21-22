package games.pacman.ghost;

import games.pacman.features.NodeEvaluator;
import games.pacman.maze.MazeNode;
import games.pacman.core.PacMan;
import games.pacman.maze.OldMaze;
// import games.pacman.ghost.Ghost;

public class EuclideanScore implements NodeEvaluator {
    public double score(MazeNode node, Ghost me, PacMan pacman, Ghost[] ghosts, OldMaze maze) {
        return sqr(node.x - pacman.current.x) +
                sqr(node.y - pacman.current.y);
    }

    private static double sqr(double x) {
        return x * x;
    }
}
