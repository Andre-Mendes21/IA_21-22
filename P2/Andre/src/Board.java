import java.util.ArrayList;
import java.util.List;

public class Board implements Ilayout, Cloneable {
    private static final int dim = 3;
    private final int[] jug;
    private final int[] jugCapacity = {8, 5, 3};

    public Board() {
        this.jug = new int[dim];
    }

    public Board(int[] input) throws IllegalStateException {
        if (input.length != dim) {
            throw new IllegalStateException("Invalid arg in Board constructor");
        }
        this.jug = input;
    }

    public Board(Board org) {
        this.jug = new Board().jug;
        System.arraycopy(org.jug, 0, this.jug, 0, dim);
    }

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();
        
        for(int i = 0; i < dim; i++) {
            for(int j = 1; j <= 2; j++) {
                Board newBoard = new Board(this);

                if(newBoard.jug[i] == 0) {
                    continue;
                }
                int nextJug = (i + j) % dim;

                int fillJug = Math.min(newBoard.jug[nextJug] + newBoard.jug[i], newBoard.jugCapacity[nextJug]);
                int emptyJug = Math.max(newBoard.jug[i] - (newBoard.jugCapacity[nextJug] - newBoard.jug[nextJug]), 0);

                if(fillJug == newBoard.jug[nextJug] && emptyJug == newBoard.jug[i]) {
                    continue;
                }
                newBoard.jug[nextJug] = fillJug;
                newBoard.jug[i] = emptyJug;

                children.add(newBoard);
            }
        }

        return children;
    }

    @Override
    public boolean isGoal(Ilayout l) {
        return l.equals(this);
    }

    @Override
    public double getG() {
        return 1;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < dim - 1; i++) {
            output.append(this.jug[i] + " ");
        }
        output.append(this.jug[dim - 1]);

        return output.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }

        if (!(that instanceof Board)) {
            return false;
        }

        Board other = (Board) that;

        for(int i = 0; i < dim; i++) {
            if(other.jug[i] != this.jug[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
