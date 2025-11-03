import java.util.*;

public class redscare_1 {
    static int n;
    static int s;
    static int t;
    static Set<Integer> r;

    public static void main(String[] args) throws Exception {
        if (System.in.available() == 0)
            return;

        Scanner sc = new Scanner(System.in);

        List<List<Integer>> adj = readAdjMatrix(sc);

        System.out.println("N: " + n + " s:" + s + " t:" + t);
        System.out.println("R: " + r);
        System.out.println("adj: " + adj);
    }

    private static List<List<Integer>> readAdjMatrix(Scanner sc) {
        n = sc.nextInt();
        s = sc.nextInt();
        t = sc.nextInt();
        sc.nextLine();

        String[] redlist = sc.nextLine().split(" ");
        r = new HashSet<>();
        if (!redlist[0].isEmpty()) {
            for (String v : redlist) {
                r.add(Integer.parseInt(v));
            }
        }

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
            String[] edges = sc.nextLine().split(" ");
            for (String e : edges) {
                if (!e.isEmpty())
                    adj.get(i).add(Integer.parseInt(e));
            }
        }

        sc.close();
        return adj;
    }
}
