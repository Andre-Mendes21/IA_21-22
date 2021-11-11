import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Jug implements Ilayout, Cloneable {
    private static final int dim = 3;
    private final int[] jug;
    private final int[] jugCapacity = { 8, 5, 3 };

    public Jug() {
        this.jug = new int[dim];
    }

    public Jug(int[] input) throws IllegalStateException {
        if (input.length != dim) {
            throw new IllegalStateException("Invalid arg in Jug constructor");
        }
        this.jug = input;
    }

    public Jug(Jug orig) {
        this.jug = new Jug().jug;
        System.arraycopy(orig.jug, 0, this.jug, 0, dim);
    }

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();

        for(int i = 0; i < dim; i++) {
            if (this.jug[i] == 0) {
                continue;
            }
            for(int j = 1; j < dim; j++) {

                Jug newJug = new Jug(this);
                int nextJug = (i + j) % dim;

                int fillJug = Math.min(newJug.jug[nextJug] + newJug.jug[i], newJug.jugCapacity[nextJug]);
                int emptyJug = Math.max(newJug.jug[i] - (newJug.jugCapacity[nextJug] - newJug.jug[nextJug]), 0);

                if (fillJug == newJug.jug[nextJug] && emptyJug == newJug.jug[i]) {
                    continue;
                }
                newJug.jug[nextJug] = fillJug;
                newJug.jug[i] = emptyJug;

                children.add(newJug);
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

        if (!(that instanceof Jug)) {
            return false;
        }

        Jug other = (Jug) that;

        return Arrays.equals(other.jug, this.jug);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
