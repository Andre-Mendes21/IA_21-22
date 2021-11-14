import java.util.*;

public class BestFirst {
    static class State {
        private final Ilayout layout;
        private final State father;
        private final double g;

        public State(Ilayout l, State n) {
            this.layout = l;
            this.father = n;

            if(father != null) {
                g = father.g + l.getG();
            }
            else {
                g = 0.0f;
            }
        }

        @Override
        public boolean equals(Object that) {
            if (that == null) {
                return false;
            }
    
            if (!(that instanceof State)) {
                return false;
            }
    
            State other = (State) that;
    
            return other.layout.equals(this.layout);
        }

        public String toString() {
            return layout.toString();
        }

        public double getG() {
            return g;
        }
    }

    protected Queue<State> opened;

    private List<State> sucessores(State n) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.layout.children();
        
        for(Ilayout e: children) {
            if(n.father == null || !e.equals(n.father.layout)) {
                State newState = new State(e, n);
                sucs.add(newState);
            }
        }
        return sucs;
    }

    final public Iterator<State> solve(Ilayout s, Ilayout goal) {
        opened = new PriorityQueue<>(10, (s1, s2) -> (int) Math.signum(s1.getG() - s2.getG()));
        List<State> shut = new ArrayList<>();
        opened.add((new State(s, null)));
        List<State> sucs;

        while(true) {
            if(opened.isEmpty()) {
                System.exit(1);
            }

            State actual = opened.poll();
            
            if(actual.layout.isGoal(goal)) {
                List<State> solutions = new ArrayList<>();
                State temp = actual;
                for(; temp.father != null; temp = temp.father) {
                    solutions.add(0, temp);
                }
                solutions.add(0, temp);
                return solutions.iterator();
            }
            else {
                sucs = this.sucessores(actual);
                shut.add(actual);
                for(State successor : sucs) {
                    if(!shut.contains(successor) && !opened.contains(successor)) {
                        opened.add(successor);
                    }    
                }
            }
        }
    }
}
