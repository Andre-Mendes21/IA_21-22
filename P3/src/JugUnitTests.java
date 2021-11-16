import org.junit.Test;
import java.util.Iterator;
import java.util.*;
import static org.junit.Assert.*;

public class JugUnitTests {
    @Test
    public void testConstructor() {
        String jugsCapacity = "8 5 3";
        String currJugs = "8 0 0";

        String expectedJugCapacity = new String("8 5 3");
        String expectedCurrJugs = new String("8 0 0");
        
        Jug b = new Jug(jugsCapacity, currJugs);

        assertEquals(expectedJugCapacity, Arrays.toString(b.getJugsCapacity()).replaceAll("\\[|\\]|,|", ""));
        assertEquals(expectedCurrJugs, Arrays.toString(b.getCurrJugs()).replaceAll("\\[|\\]|,|", ""));
    }

    @Test
    public void testConstructor2() {
        String jugsCapacity = "21 13 8 5 3";
        String currJugs = "21 0 0 0 0";

        String expectedJugCapacity = new String("21 13 8 5 3");
        String expectedCurrJugs = new String("21 0 0 0 0");
        
        Jug b = new Jug(jugsCapacity, currJugs);

        assertEquals(expectedJugCapacity, Arrays.toString(b.getJugsCapacity()).replaceAll("\\[|\\]|,|", ""));
        assertEquals(expectedCurrJugs, Arrays.toString(b.getCurrJugs()).replaceAll("\\[|\\]|,|", ""));
    }

    @Test
    public void testBestFirstSolve() {
        String jugsCapacity = "8 5 3";
        String currJugs = "8 0 0";
        String out = "0 5 3";
        double expected = 2f;
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            BestFirst.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    @Test (timeout = 2000)
    public void testBestFirstSolve2() {
        String jugsCapacity = "21 13 8 5 3";
        String currJugs = "21 0 0 0 0";
        String out = "6 4 4 4 3";
        double expected = 13f;
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            BestFirst.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    @Test
    public void testBestFirstSolve3() {
        String jugsCapacity = "8 7 6 5 4 3 2 1";
        String currJugs = "0 0 0 0 4 3 2 1";
        String out = "1 1 1 1 1 3 1 1";
        double expected = 8f;
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            BestFirst.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    /*============================== IDS Tests ==============================*/

    @Test (timeout = 2)
    public void testIDSSolve() {
        String jugsCapacity = "8 5 3";
        String currJugs = "8 0 0";
        String out = "0 5 3";
        double expected = 2f;
        IDS s = new IDS();
        Iterator<IDS.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            IDS.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    @Test (timeout = 2000)
    public void testIDSSolve2() {
        String jugsCapacity = "21 13 8 5 3";
        String currJugs = "21 0 0 0 0";
        String out = "6 4 4 4 3";
        double expected = 13f;
        IDS s = new IDS();
        Iterator<IDS.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            IDS.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    @Test (timeout = 2000)
    public void testIDSSolve3() {
        String jugsCapacity = "8 7 6 5 4 3 2 1";
        String currJugs = "0 0 0 0 4 3 2 1";
        String out = "1 1 1 1 1 3 1 1";
        double expected = 8f;
        IDS s = new IDS();
        Iterator<IDS.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            IDS.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    /*============================== A* Tests ==============================*/

    @Test
    public void testAStarSolve() {
        String jugsCapacity = "8 5 3";
        String currJugs = "8 0 0";
        String out = "0 5 3";
        double expected = 2f;
        AStar s = new AStar();
        Iterator<AStar.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            AStar.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    @Test
    public void testAStarSolve2() {
        String jugsCapacity = "21 13 8 5 3";
        String currJugs = "21 0 0 0 0";
        String out = "6 4 4 4 3";
        double expected = 13f;
        AStar s = new AStar();
        Iterator<AStar.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            AStar.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }

    @Test
    public void testAStarSolve2Expanded() {
        String jugsCapacity = "21 13 8 5 3";
        String currJugs = "21 0 0 0 0";
        String out = "6 4 4 4 3";

        String[] expected = {
            "21 0 0 0 0",
            "16 0 0 5 0",
            "16 0 0 2 3",
            "16 0 3 2 0",
            "3 13 3 2 0",
            "3 10 3 2 3",
            "3 10 6 2 0",
            "3 10 6 0 2",
            "3 10 1 5 2",
            "3 10 1 4 3",
            "3 10 4 4 0",
            "3 7 4 4 3",
            "6 7 4 4 0",
            "6 4 4 4 3" };
        

        AStar s = new AStar();
        Iterator<AStar.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        int j = 0;
        while(it.hasNext()) {
            AStar.State i = it.next();
            assertEquals(expected[j++], i.toString());
        }
    }

    @Test 
    public void testAStarSolve3() {
        String jugsCapacity = "8 7 6 5 4 3 2 1";
        String currJugs = "0 0 0 0 4 3 2 1";
        String out = "1 1 1 1 1 3 1 1";
        double expected = 8f;
        AStar s = new AStar();
        Iterator<AStar.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, out));

        while(it.hasNext()) {
            AStar.State i = it.next();
            if(!it.hasNext()) {
                assertEquals(String.format("%.0f", expected), String.format("%.0f", i.getG()));
            }
        }
    }
}