package src;

import java.util.List;

public interface Ilayout {
    /**
     * @return the children of the receiver
     */
    List<Ilayout> children();

    /**
     * @return true if the receiver equals the arguments 1; 
     *         return false otherwise
     */
    boolean isGoal(Ilayout l);

    /**
     * @return the cost for moving from the input config to the receiver.
    */
    double getG();
}
