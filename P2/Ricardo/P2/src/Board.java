import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board implements Ilayout {
    private static final int dim = 3;
    private final int[][] jug;

    public Board() {
        this.jug = new int[2][dim];
    }

    public Board(String str) throws IllegalStateException {
        str = str.replaceAll(" ","");
        if(str.length() != dim * 2) {
            throw new IllegalStateException("Invalid arg in Board constructor");
        }

        this.jug = new int[2][dim];
        int si = 0;

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < dim; j++) {
                this.jug[i][j] = Character.getNumericValue(str.charAt(si++));
            }
        }
    }


    public Board(Board org) {
        this.jug = new Board().jug;

        for(int i = 0; i < 2; i++) {
                System.arraycopy(org.jug[i], 0, this.jug[i], 0, dim);
        }
    }

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();
        Board newBoard;

        for(int i = 0; i < dim; i++) {
            for(int j = 1; j < dim; j++) {
                int currentJug = this.jug[0][i];
                int nextJug = this.jug[0][(i + j) % dim];
                int nextJugCapacity = this.jug[1][(i + j) % dim];

                if(this.jug[0][i] == 0 || nextJug == nextJugCapacity) {
                    continue;
                }

                newBoard = new Board(this);

                newBoard.jug[0][i] = Math.max(currentJug - (nextJugCapacity - nextJug), 0);
                newBoard.jug[0][(i + j) % dim] = Math.min((currentJug + nextJug), nextJugCapacity);

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

        for(int i = 0; i < dim; i++) {
            output.append(String.format("%d ", this.jug[0][i]));
        }
        return output.toString().trim();
    }

    @Override
    public boolean equals(Object that) {
        if(that == null) {
            return false;
        }

        if(!(that instanceof Board)) {
            return false;
        }

        Board other = (Board) that;

        return Arrays.equals(other.jug[0], this.jug[0]);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}
