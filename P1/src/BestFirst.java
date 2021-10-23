import java.util.*;

public class BestFirst {

    static class State{
        private Ilayout layout;
        private State father;
        private double g;

        public State(Ilayout l, State n)
        {
            this.layout = l;
            this.father = n;

            if(father != null)
            {
                g = father.g + l.getG();
            }

            else
            {
                g = 0.0f;
            }
        }

        public String toString()
        {
            return layout.toString();
        }

        public double getG()
        {
            return g;
        }
    }

    protected Queue<State> opened;
    private List<State> shut;
    private State actual;
    private Ilayout objective;

    final private List<State> sucessores(State n)
    {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.layout.children();
        
        for(Ilayout e: children)
        {
            if(n.father == null || !e.equals(n.father.layout))
            {
                State newState = new State(e, n);
                sucs.add(newState);
            }
        }
        return sucs;
    }

    final public Iterator<State> solve(Ilayout s, Ilayout goal)
    {
        this.objective = goal;
        opened = new PriorityQueue<>(10, (s1, s2) -> (int) Math.signum(s1.getG() - s2.getG()));
        shut = new ArrayList<>();

        opened.add((new State(s, null)));
        List<State> sucs;

        while(true)
        {
            if(opened.isEmpty())
            {
                System.exit(1);
            }

            this.actual = opened.poll();
            opened.remove(actual);
            if(actual.layout.isGoal(objective))
            {
                Queue<State> solutions = new PriorityQueue<>();

                State temp = actual;
                for(; temp.father != null; temp = temp.father)
                {
                    solutions.add(temp);
                }
                solutions.add(temp);

                return solutions.iterator();
            }
            
            else
            {
                sucs = this.sucessores(actual);
                shut.add(actual);

                for (State successor : sucs) 
                {
                    if(!shut.contains(successor))
                    {
                        opened.add(successor);
                    }    
                }
            }
        }
    }
}