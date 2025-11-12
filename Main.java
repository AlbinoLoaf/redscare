import java.util.*;

public class Main {
    static int n; // number of nodes
    static int e; // number of edges
    static int r; // number of red nodes
    static int s; // source
    static int t; // sink

    private static void println(Object s) {
        System.out.println(s);
    }

    public static void main(String[] args) throws Exception {
        if (System.in.available() == 0)
            return;

        Graph graph = readAdjMatrix();

        println("N:" + n + " s:" + s + " t:" + t);
        println(graph.toString());
        // ^^^ Hvis det printede output ser mærkeligt ud, så er det nok fordi din
        // terminal ikke supporter ANSI escape codes. In that case, bare fjern
        // kaldet til toStringColored().

        println("[None]: " + None.lengthOfShortestPathWithoutReds(graph, s, t));
    }

    private static Graph readAdjMatrix() {
        Scanner sc = new Scanner(System.in);

        // Read graph parameters
        n = sc.nextInt();
        e = sc.nextInt();
        r = sc.nextInt();
        sc.nextLine();

        s = sc.nextInt();
        t = sc.nextInt();
        sc.nextLine();

        Graph graph = new Graph(new ArrayList<>(n), new HashMap<>(), new HashSet<>());
        for (int i = 0; i < n; i++) {
            String line = sc.nextLine().trim();
            boolean isRed = line.endsWith("*");
            String name = isRed ? line.substring(0, line.length() - 1).trim() : line;

            graph.map().put(name, i);
            graph.reds().add(i);
            graph.nodes().add(new Graph.Node(isRed, new ArrayList<>()));
        }

        for (int i = 0; i < e; i++) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                i--;
                continue;
            }

            boolean directed = line.contains("->");
            String[] parts = line.split(directed ? "->" : "--");
            if (parts.length != 2)
                continue;

            String u_str = parts[0].trim();
            String v_str = parts[1].trim();

            Integer u = graph.map().get(u_str);
            Integer v = graph.map().get(v_str);
            if (u == null || v == null)
                continue;

            graph.nodes().get(u).adjs().add(v);
            if (!directed) {
                graph.nodes().get(v).adjs().add(u);
            }
        }

        sc.close();
        return graph;
    }
}
