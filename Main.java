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

        for (int argI = 0; argI < args.length; argI++) {
            String arg = args[argI];

            if (argI == 0 && !arg.startsWith("-")) {
                String cmd = arg;
                if (cmd.equals("test")) {
                    test = true;
                } else {
                    println("Unknown command: " + cmd);
                    return;
                }
                continue;
            }

            if (arg.equals("-q"))
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

        Input input = Input.parseFrom(System.in);
        if (!quiet) {
            println("N: " + input.graph.nodes.size() + " s:" + input.s + " t:" + input.t);
            println("Directed: " + input.graph.isDirected);
            println("Kind: " + input.graph.kind);
            println(input.graph);
        }

        Result result = Result.checkFor(input);
        println(result);
    }

    private static void test(boolean quiet) {
        println("Running tests...");

        Input input = readInputFromFile("./testSome.txt");
        println(input.graph);

        Result result = Result.checkFor(input);
        println(result);
    }

    private static record Input(Graph graph, int s, int t) {
        private static Input parseFrom(InputStream stream) {
            Scanner sc = new Scanner(stream);

            // Read graph parameters
            int n = sc.nextInt();
            int e = sc.nextInt();
            @SuppressWarnings("unused") int r = sc.nextInt();
            sc.nextLine();

            String s_str = sc.next();
            String t_str = sc.next();
            sc.nextLine();

            Graph graph = new Graph(n);
            for (int i = 0; i < n; i++) {
                String line = sc.nextLine().trim();
                boolean isRed = line.endsWith("*");
                String name = isRed ? line.substring(0, line.length() - 1).trim() : line;

                graph.identMap.put(name, i);
                if (isRed) graph.redSet.set(i);
                graph.nodes.add(graph.new Node(i));
                assert graph.nodes.size() == i + 1;
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

                Integer u = graph.identMap.get(u_str);
                Integer v = graph.identMap.get(v_str);
                if (u == null || v == null)
                    continue;

                graph.addEdge(u, v);
            }

            int s = graph.identMap.get(s_str);
            int t = graph.identMap.get(t_str);

            if (!graph.unionFind.connected(s, t)) {
                println("Abort: s and t are in different connected components.");
                System.exit(0);
            }

            graph.checkKindForComponentWith(s, graph.isDirected);

            sc.close();
            return new Input(graph, s, t);
        }
    }

    private record Result(int none, boolean some, int few, int many, boolean alternate) {
        private static Result checkFor(Input input) {
            return new Result(
                None.shortestPathWithoutReds(input.graph, input.s, input.t),
                Some.doesPathWithRedExist(input.graph, input.s, input.t),
                Few.leastRedPath(input.graph, input.s, input.t),
                Many.mostRedPath(input.graph, input.s, input.t),
                Alternate.alternatingPathExist(input.graph, input.s, input.t)
            );
        }

        @Override
        public String toString() {
            return "[None: %-10d Some: %-10b Few: %-10d Many: %-10d Alternate: %-5b]"
                .formatted(none, some, few, many, alternate);
        }
    }

    private static Input readInputFromFile(String path) {
        File file = new File(path);
        try {
            return Input.parseFrom(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            println("File not found: " + path);
            System.exit(1);
            return null; // Never happens
        }
    }

}
