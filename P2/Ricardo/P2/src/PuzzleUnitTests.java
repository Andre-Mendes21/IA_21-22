import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;

public class PuzzleUnitTests {
    final String capacity = "8 5 3";

    @Test
    public void testConstructor() {
        Board b = new Board("800" + capacity);
        String expected = "8 0 0";
        assertEquals(expected, b.toString());
    }

    @Test
    public void testConstructor2() {
        Board b = new Board("41 3" + capacity);
        String expected = "4 1 3";
        assertEquals(expected, b.toString());
    }

    @Test
    public void testSolve() {
        String strIn = "800" + capacity;
        String strOut = "0 5 3" + capacity;
        String[] expected1 = {"8 0 0", "3 5 0", "0 5 3"};
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board(strIn), new Board(strOut));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }

    @Test
    public void testSolve2() {
        String strIn = "8 0 0" + capacity;
        String strOut = "413" + capacity;
        String[] expected1 = {
                "8 0 0",
                "5 0 3",
                "5 3 0",
                "2 3 3",
                "2 5 1",
                "7 0 1",
                "7 1 0",
                "4 1 3"};
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board(strIn), new Board(strOut));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }
}