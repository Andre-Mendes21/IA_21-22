package src;

import java.util.*;

public class Client {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        BestFirst s = new BestFirst();
        int[] jugCapacity = {8, 5, 3};

        Iterator<BestFirst.State> it = s.solve(new Board(sc.nextLine(), jugCapacity), new Board(sc.nextLine(), jugCapacity));

        if(it == null) {
            System.out.println("no solution was found");
        }
        else {
            while(it.hasNext()) {
                BestFirst.State i = it.next();
                System.out.println(i);
                if(!it.hasNext()) {
                    System.out.println(String.format("%.0f", i.getG()));
                }
            }
        }
        sc.close();
    }
}
