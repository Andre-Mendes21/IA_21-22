import java.util.*;

public class Client {

    private static int[] getJugs(Scanner sc) {
        int[] jugs = new int[3];

        for(int i = 0; i < 3; i++) {
            jugs[i] = sc.nextInt();
        }
        return jugs;
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        BestFirst s = new BestFirst();

        Iterator<BestFirst.State> it = s.solve(new Jug(getJugs(sc)), new Jug(getJugs(sc)));

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
