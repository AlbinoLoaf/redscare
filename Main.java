import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
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
        if (args.length > 1 && args[1].equals("test")) {
            tests();
            return;
        }

        if (System.in.available() == 0)
            return;

        boolean quiet = args.length > 0 && "-q".equals(args[0]);
        Graph graph = readGraph(System.in);

        if (!quiet) {
            System.out.println("N: " + n + " s:" + s + " t:" + t);
            System.out.println(graph.toStringColored());
        }
        // ^^^ Hvis det printede output ser mærkeligt ud, så er det nok fordi din
        // terminal ikke supporter ANSI escape codes. In that case, bare fjern
        // kaldet til toStringColored().

        println("[None]: " + None.lengthOfShortestPathWithoutReds(graph, s, t));

        println("[Some]: " + Some.doesPathWithRedExist(graph, s, t, graph.isDirected));

        println("[Few]: " + Few.LeastRedPath(graph, s, t));

        println("[Alternate]: " + Alternate.doesAlternatingPathExist(graph, s, t));
        
    }

    private static Graph readGraph(InputStream input) {
        Scanner sc = new Scanner(input);

        // Read graph parameters
        n = sc.nextInt();
        e = sc.nextInt();
        r = sc.nextInt();
        sc.nextLine();
        Boolean stringBasedGraph = false;
        String[] tmp_s = {""};

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
            graph.isDirected = directed;
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

    private static void tests() throws Exception {
        println("Running tests...");
        var file = new File("./testSome.txt");
        var fis = new FileInputStream(file);
        var graph = readGraph(fis);
        System.out.println(graph.toStringColored());

        println("[None]: " + None.lengthOfShortestPathWithoutReds(graph, s, t));

        println("[Some]: " + Some.doesPathWithRedExist(graph, s, t, graph.isDirected));

        println("[Few]: " + Few.LeastRedPath(graph, s, t));

        println("[Alternate]: " + Alternate.doesAlternatingPathExist(graph, s, t));
    }
}
