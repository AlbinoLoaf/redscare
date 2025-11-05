import java.util.*;

public class Main {
    static int n;
    static int s;
    static int t;

    private static void println(Object s) { System.out.println(s); }

    public static void main(String[] args) throws Exception {
        if (System.in.available() == 0)
            return;

        Graph graph = readAdjMatrix();

        println("N:" + n + " s:" + s + " t:" + t);
        println(graph.toStringColored());
        // ^^^ Hvis det printede output ser mærkeligt ud, så er det nok fordi din
        //     terminal ikke supporter ANSI escape codes. In that case, bare fjern
        //     kaldet til toStringColored().

        println("[None]: " + None.lengthOfShortestPathWithoutReds(graph, s, t));
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
