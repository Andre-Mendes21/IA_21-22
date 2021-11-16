import java.util.*;

public class IDS {
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
        
        public String toString() {
            return layout.toString();
        }

        public double getG() {
            return g;
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

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
    
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

    private State DLS(Ilayout s, Ilayout goal, int limit) {
        Stack<State> frontier = new Stack<State>();
        Set<State> visited = new HashSet<State>();
        State result = null;
        List<State> sucs;
        
        frontier.push(new State(s, null));
        while(!frontier.isEmpty()) {
            State actual = frontier.pop();

            if(actual.layout.isGoal(goal)) {
                return actual;
            }

            if(actual.getG() > limit) {
                result = actual;
            }

            else {
                visited.add(actual);
                sucs = this.sucessores(actual);
                
                for (State state : sucs) {
                    if(!visited.contains(state)) {
                        frontier.add(state);
                    }
                }
            }
        }
        return result;
    }

    final public Iterator<State> solve(Ilayout s, Ilayout goal) {
        State result;

        for(int i = 0; ; i++) {
            result = DLS(s, goal, i);

            if(result == null) {
                System.exit(1);
            }

            else if(result.layout.isGoal(goal)) {
                List<State> solutions = new ArrayList<>();
                State temp = result;
                for(; temp.father != null; temp = temp.father) {
                    solutions.add(0, temp);
                }
                solutions.add(0, temp);
                return solutions.iterator();
            }
        }
    }
}

