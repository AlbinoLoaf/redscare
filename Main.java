import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Main {
    private static void println(Object s) {
        System.out.println(s);
    }

    public static void main(String[] args) throws IOException {
        boolean test = false;
        boolean quiet = false;

        for (int argI = 1; argI < args.length; argI++) {
            String arg = args[argI];

            if (argI == 1 && !arg.startsWith("-")) {
                String cmd = arg;
                if (cmd.equals("test")) {
                    test = true;
                } else {
                    println("Unknown command: " + cmd);
                    return;
                }
                continue;
            }

            if (arg.startsWith("-"))
                quiet = true;
            else
                println("Unknown argument: " + arg);
        }

        if (test)
            test(quiet);
        else
            run(quiet);
    }

    public static void run(boolean quiet) throws IOException {
        if (System.in.available() == 0) {
            println("No content from stdin.");
            return;
        }

        if (System.in.available() == 0)
            return;

        // vvv To whoever wrote this, the first command line argument is args[1]. arg[0]
        // is always the program name/path.
        boolean quiet = args.length > 0 && "-q".equals(args[0]);
        Input input = readInput(System.in);

        if (!quiet) {
            println("N: " + input.graph.nodes.size() + " s:" + input.s + " t:" + input.t);
            println("Directed: " + input.graph.isDirected);
            println("Cyclic: " + input.graph.isCyclic);
            println("Tree: " + input.graph.isTree);
            println(input.graph.toStringColored());
        }

        println("[None]: " + None.lengthOfShortestPathWithoutReds(input.graph, input.s, input.t));

        println("[Some]: " + Some.doesPathWithRedExist(input.graph, input.s, input.t));

        println("[Few]: " + Few.LeastRedPath(input.graph, input.s, input.t));

        println("[Alternate]: " + Alternate.doesAlternatingPathExist(input.graph, input.s, input.t) + "\n");

    }

    private static void test(boolean quiet) {
        println("Running tests...");

        Input input = readInputFromFile("./testSome.txt");
        println(input.graph.toStringColored());

        println("[None]: " + None.lengthOfShortestPathWithoutReds(input.graph, input.s, input.t));

        println("[Some]: " + Some.doesPathWithRedExist(input.graph, input.s, input.t));

        println("[Few]: " + Few.LeastRedPath(input.graph, input.s, input.t));

        println("[Alternate]: " + Alternate.doesAlternatingPathExist(input.graph, input.s, input.t));
    }

    private static record Input(Graph graph, int s, int t) {
    }

    private static Input readInput(InputStream stream) {
        Scanner sc = new Scanner(stream);

        // Read graph parameters
        int n = sc.nextInt();
        int e = sc.nextInt();
        @SuppressWarnings("unused")
        int r = sc.nextInt();
        sc.nextLine();

        String s_str = sc.next();
        String t_str = sc.next();
        sc.nextLine();

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

        for (int i = 0; i < e; i++) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                i--;
                continue;
            }

            graph.isDirected = line.contains("->");
            String[] parts = line.split(graph.isDirected ? "->" : "--");
            if (parts.length != 2)
                continue;

            String u_str = parts[0].trim();
            String v_str = parts[1].trim();

            Integer u = graph.map.get(u_str);
            Integer v = graph.map.get(v_str);
            if (u == null || v == null)
                continue;

            if (graph.isDirected)
                graph.addEdgeDirected(u, v);
            else
                graph.addEdgeUndirected(v, u);
        }

        int s = graph.map.get(s_str);
        int t = graph.map.get(t_str);

        if (!graph.unionFind.connected(s, t)) {
            println("Abort: s and t are in different connected components.");
            System.exit(0);
        }

        graph.checkIfCyclic(s);

        sc.close();
        return new Input(graph, s, t);
    }

    private static Input readInputFromFile(String path) {
        File file = new File(path);
        try {
            return readInput(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            println("File not found: " + path);
            System.exit(1);
            return null; // Never happens
        }
    }

}
