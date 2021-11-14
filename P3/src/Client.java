import java.util.*;

public class Client {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        BestFirst s = new BestFirst();

        String jugsCapacity = sc.nextLine().trim();
        String currJugs = sc.nextLine().trim();
        String objective = sc.nextLine().trim();

        Iterator<BestFirst.State> it = s.solve(new Jug(jugsCapacity, currJugs), new Jug(jugsCapacity, objective));

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
