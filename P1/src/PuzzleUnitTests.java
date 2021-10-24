import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;

public class PuzzleUnitTests {
    @Test
    public void testConstructor() {
        Board b = new Board("023145678");
        String expected = new String(" 23\n145\n678\n");
        assertEquals(expected, b.toString());
    }

    @Test
    public void testConstructor2() {
        Board b = new Board("123485670");
        String expected = new String("123\n485\n67 \n");
        assertEquals(expected, b.toString());
    }

    @Test
    public void testSolve() {
        String[] expected1 = {" 23\n145\n678\n", "123\n 45\n678\n", "123\n4 5\n678\n"};
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board("023145678"), new Board("123405678"));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }

    @Test
    public void testSolve2() {
        String[] expected1 = {
                            "123\n456\n78 \n", 
                            "123\n456\n7 8\n", 
                            "123\n4 6\n758\n", 
                            "1 3\n426\n758\n", 
                            " 13\n426\n758\n", 
                            "413\n 26\n758\n", 
                            "413\n726\n 58\n", 
                            "413\n726\n5 8\n", 
                            "413\n7 6\n528\n", 
                            "4 3\n716\n528\n", 
                            "43 \n716\n528\n", 
                            "436\n71 \n528\n", 
                            "436\n718\n52 \n"};
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board("123456780"), new Board("436718520"));

        int j = 0;
        while(it.hasNext()) {
            BestFirst.State i = it.next();
            assertEquals(expected1[j++], i.toString());
        }
    }
}