package src;

import java.util.ArrayList;
import java.util.List;

public class Board implements Ilayout, Cloneable {
    // Up, Right, Down, Left
    private static final int dim = 3;
    private final int[] jug;
    private final int[] jugCapacity;

    public Board() {
        this.jug = new int[dim];
        this.jugCapacity = new int[dim];
    }

    public Board(String str, int[] capacity) throws IllegalStateException {
        if (str.length() != dim && capacity.length != dim) {
            throw new IllegalStateException("Invalid arg in Board constructor");
        }

        this.jug = new int[dim];
        this.jugCapacity = capacity;

        String[] jugs = str.split(" ");

        for(int i = 0; i < dim; i++) {
            this.jug[i] = Integer.parseInt(jugs[i]);
        }
    }

    public Board(Board org) {
        this.jug = new Board().jug;
        this.jugCapacity = new Board().jugCapacity;

        System.arraycopy(org.jug, 0, this.jug, 0, dim);
        System.arraycopy(org.jugCapacity, 0, this.jugCapacity, 0, dim);
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

                int fillJug = (newBoard.jug[nextJug] + newBoard.jug[i]) > newBoard.jugCapacity[nextJug] ? newBoard.jugCapacity[nextJug] 
                                                                                                        : (newBoard.jug[nextJug] + newBoard.jug[i]); 
                int emptyJug = (newBoard.jug[i] - (newBoard.jugCapacity[nextJug] - newBoard.jug[nextJug])) < 0 ? 0 
                                                                                                                : (newBoard.jug[i] - (newBoard.jugCapacity[nextJug] - newBoard.jug[nextJug]));

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
