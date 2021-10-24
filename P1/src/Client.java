import java.util.*;

public class Client {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board(sc.next()), new Board(sc.next()));

        if(it == null) {
            System.out.println("no solution was found");
        }
        else {
            while(it.hasNext()) {
                BestFirst.State i = it.next();
                System.out.println(i);
                if(!it.hasNext()) {
                    System.out.println(i.getG());
                }
            }
        }
        sc.close();
    }
}
