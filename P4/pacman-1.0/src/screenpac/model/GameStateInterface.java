package screenpac.model;

import java.util.BitSet;

public interface GameStateInterface {
    GameStateInterface copy();
    void next(int pacDir, int[] ghostDirs);
    Agent getPacman();
    MazeInterface getMaze();
    int getLevel(); // the maze should be recoverable from this
    BitSet getPills();
    int getNumberActivePills();
    int getNumberActivePowerPills();
    BitSet getPowers();
    GhostState[] getGhosts();
    int getScore();
    int getGameTick();
    int getEdibleGhostScore();
    boolean wasPowerEaten();
    int getLivesRemaining();
    boolean agentDeath();
    boolean agentDeathSilent();
    boolean terminal();
    void reset();
}
