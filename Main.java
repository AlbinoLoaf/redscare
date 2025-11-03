import java.util.*;

public class Main {
    static int n;
    static int s;
    static int t;

    public static void main(String[] args) throws Exception {
        if (System.in.available() == 0)
            return;

        Graph graph = readAdjMatrix();

        System.out.println("N: " + n + " s:" + s + " t:" + t);
        System.out.println(graph);

        None.run(graph);
    }

    private static Graph readAdjMatrix() {
        Scanner sc = new Scanner(System.in);

        n = sc.nextInt();
        s = sc.nextInt();
        t = sc.nextInt();
        sc.nextLine();

        Graph graph = new Graph(new ArrayList<>(n), new HashSet<>());

        String[] redlist = sc.nextLine().split(" ");
        if (!redlist[0].isEmpty()) {
            for (String v : redlist)
                graph.reds().add(Integer.parseInt(v));
        }

        for (int i = 0; i < n; i++) {
            Graph.Node node = new Graph.Node(graph.reds().contains(i), new ArrayList<>());
            graph.nodes().add(node);

            String[] edges = sc.nextLine().split(" ");
            for (String e : edges) {
                if (!e.isEmpty())
                    node.adjs().add(Integer.parseInt(e));
            }
        }

        sc.close();
        return graph;
    }

}
