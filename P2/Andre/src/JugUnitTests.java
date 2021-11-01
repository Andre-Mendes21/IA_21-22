package src;

import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;

public class JugUnitTests {
    @Test
    public void testConstructor() {
        int[] jugCapacity = {8, 5, 3};
        Board b = new Board("8 0 0", jugCapacity);
        String expected = new String("8 0 0");
        assertEquals(expected, b.toString());
    }

    @Test
    public void testConstructor2() {
        int[] jugCapacity = {8, 5, 3};
        Board b = new Board("4 1 3", jugCapacity);
        String expected = new String("4 1 3");
        assertEquals(expected, b.toString());
    }

    @Test
    public void testSolve() {
        int[] jugCapacity = {8, 5, 3};
        String[] expected1 = {"8 0 0", "3 5 0", "0 5 3"};
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board("8 0 0", jugCapacity), new Board("0 5 3", jugCapacity));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }

    @Test
    public void testSolve2() {
        int[] jugCapacity = {8, 5, 3};
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
        Iterator<BestFirst.State> it = s.solve(new Board("8 0 0", jugCapacity), new Board("4 1 3", jugCapacity));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }
}