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
        println(graph.toStringColored());
        // ^^^ Hvis det printede output ser mærkeligt ud, så er det nok fordi din
        // terminal ikke supporter ANSI escape codes. In that case, bare fjern
        // kaldet til toStringColored().

        println("[None]: " + None.lengthOfShortestPathWithoutReds(graph, s, t));

        println("[Few]: " + Few.LeastRedPath(graph, s, t));

        println("[Alternate]: " + Alternate.doesAlternatingPathExist(graph, s, t));
    }

    private static Graph readAdjMatrix() {
        Scanner sc = new Scanner(System.in);

        // Read graph parameters
        n = sc.nextInt();
        e = sc.nextInt();
        r = sc.nextInt();
        sc.nextLine();
        Boolean stringBasedGraph = false;
        String[] tmp_s = "".split(" ");
        println(tmp_s);
        try {
            s = sc.nextInt();
            t = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            tmp_s = sc.nextLine().split(" ");
            stringBasedGraph = true;

        }

        Graph graph = new Graph(n);
        for (int i = 0; i < n; i++) {
            String line = sc.nextLine().trim();
            boolean isRed = line.endsWith("*");
            String name = isRed ? line.substring(0, line.length() - 1).trim() : line;

            graph.map.put(name, i);
            if (isRed)
                graph.reds.add(i);
            graph.nodes.add(graph.new Node(isRed));
        }
        if (stringBasedGraph) {
            s = graph.map.get(tmp_s[0]);
            t = graph.map.get(tmp_s[1]);

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

            Integer u = graph.map.get(u_str);
            Integer v = graph.map.get(v_str);
            if (u == null || v == null)
                continue;

            if (directed)
                graph.addEdgeDirected(u, v);
            else
                graph.addEdgeUndirected(v, u);
        }

        sc.close();
        return graph;
    }
}
