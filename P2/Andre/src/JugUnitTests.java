import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;

public class JugUnitTests {
    @Test
    public void testConstructor() {
        int[] in = {8, 0, 0};
        Board b = new Board(in);
        String expected = new String("8 0 0");
        assertEquals(expected, b.toString());
    }

    @Test
    public void testConstructor2() {
        int[] in = {4, 1, 3};
        Board b = new Board(in);
        String expected = new String("4 1 3");
        assertEquals(expected, b.toString());
    }

    @Test
    public void testSolve() {
        int[] in = {8, 0, 0};
        int[] out = {0, 5, 3};
        String[] expected1 = {"8 0 0", "3 5 0", "0 5 3"};
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board(in), new Board(out));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }

    @Test
    public void testSolve2() {
        int[] in = {8, 0, 0};
        int[] out = {4, 1, 3};
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
        Iterator<BestFirst.State> it = s.solve(new Board(in), new Board(out));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }
}