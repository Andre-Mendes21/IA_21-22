import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Jug implements Ilayout, Cloneable {
    private final int dim;
    private final int[] jugs;
    private final int[] jugsCapacity;

    public Jug() {
        this.dim = 3;
        this.jugs = new int[dim];
        this.jugsCapacity = new int[dim];
    }

    public Jug(String jugsCapacity, String currJugs) throws IllegalStateException {
        String[] jugsCapacityArr = jugsCapacity.split(" ");
        String[] currJugsArr = currJugs.split(" ");

        if (jugsCapacityArr.length != currJugsArr.length) {
            throw new IllegalStateException("Invalid arg in Jug constructor");
        }
        this.jugsCapacity = Arrays.stream(jugsCapacityArr)
                                    .mapToInt(Integer :: parseInt)
                                    .toArray();
        this.jugs = Arrays.stream(currJugsArr)
                            .mapToInt(Integer :: parseInt)
                            .toArray();
        this.dim = this.jugsCapacity.length;
    }

    public Jug(Jug orig) {
        this.dim = orig.dim;
        this.jugs = new int[this.dim];
        this.jugsCapacity = new int[this.dim];

        System.arraycopy(orig.jugs, 0, this.jugs, 0, this.dim);
        System.arraycopy(orig.jugsCapacity, 0, this.jugsCapacity, 0, this.dim);
    }

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();

        for(int i = 0; i < dim; i++) {
            if (this.jugs[i] == 0) {
                continue;
            }
            for(int j = 1; j < dim; j++) {

                Jug newJug = new Jug(this);
                int nextJug = (i + j) % dim;

                int fillJug = Math.min(newJug.jugs[nextJug] + newJug.jugs[i], newJug.jugsCapacity[nextJug]);
                int emptyJug = Math.max(newJug.jugs[i] - (newJug.jugsCapacity[nextJug] - newJug.jugs[nextJug]), 0);

                if (fillJug == newJug.jugs[nextJug] && emptyJug == newJug.jugs[i]) {
                    continue;
                }
                newJug.jugs[nextJug] = fillJug;
                newJug.jugs[i] = emptyJug;

                children.add(newJug);
            }
        }

        return children;
    }

    public double getH(Ilayout l) {

        Jug objective = (Jug) l;
        double result = 0f;
        
        for(int i = 0; i < dim; i++) {
            result += Math.min(Math.abs(objective.jugs[i] - this.jugs[i]), 1);
        }

        return result;
    }

    @Override
    public boolean isGoal(Ilayout l) {
        return l.equals(this);
    }

    @Override
    public double getG() {
        return 1;
    }

    int[] getJugsCapacity() {
        return this.jugsCapacity;
    }

    int[] getCurrJugs() {
        return this.jugs;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < dim - 1; i++) {
            output.append(this.jugs[i] + " ");
        }
        output.append(this.jugs[dim - 1]);

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

        return Arrays.equals(other.jugs, this.jugs);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
