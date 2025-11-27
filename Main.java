import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public class Main {
    private static void println(Object s) {
        System.out.println(s);
    }

    public static void main(String[] args) throws IOException {
        boolean quiet = false;
        String folder = null;

        // Parse arguments
        for (String arg : args) {
            if (arg.equals("-q")) {
                quiet = true;
            } else {
                // Anything else is treated as a folder path
                folder = arg;
            }
        }

        run(quiet, folder);
    }

    public static void run(boolean quiet, String folder) throws IOException {
        // If folder is null, read from stdin
        if (folder == null) {
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
            return;
        }

        // Otherwise, process folder
        File folderFile = new File(folder);
        File[] files = folderFile.listFiles();
        if (files == null || files.length == 0) {
            println("No files found in folder: " + folder);
            return;
        }

        Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));

        for (File file : files) {
            if (!file.isFile())
                continue;

            String instanceName = file.getName();

            Input inputFile = Input.parseFrom(new FileInputStream(file));

            if (!quiet) {
                println("N: " + inputFile.graph.nodes.size() + " s:" + inputFile.s + " t:" + inputFile.t);
                println("Directed: " + inputFile.graph.isDirected);
                println("Kind: " + inputFile.graph.kind);
                println(inputFile.graph);
            }

            Result result = Result.checkFor(inputFile);
            String text = result.toString();

            saveResultsToFile("results_table.txt", instanceName, text);

            if (!quiet)
                println(result);
        }
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

                graph.identMap.put(name, i);
                if (isRed)
                    graph.redSet.set(i);
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

            sc.close();
            return new Input(graph, s, t);
        }
    }

    private record Result(Integer graphnodes, Integer none, Boolean some, Integer few, Integer many, Boolean alternate) {
        private static Result checkFor(Input input) {
            if (!input.graph.unionFind.connected(input.s, input.t))
                return new Result(input.graph.nodes.size(), -1, false, -1, -1, false);

            input.graph.checkKindForComponentWith(input.s, input.graph.isDirected);

            return new Result(
                    input.graph.nodes.size(),
                    None.shortestPathWithoutReds(input.graph, input.s, input.t),
                    Some.doesPathWithRedExist(input.graph, input.s, input.t),
                    Few.leastRedPath(input.graph, input.s, input.t),
                    Many.mostRedPath(input.graph, input.s, input.t),
                    Alternate.alternatingPathExist(input.graph, input.s, input.t));
        }

        @Override
        public String toString() {
            Function<Object, String> toStr = (obj) -> obj == null ? "NP-hard" : obj.toString();

            return "%10s & %10s & %10s & %10s & %10s & %5s".formatted(
                toStr.apply(graphnodes),
                toStr.apply(alternate),
                toStr.apply(few),
                toStr.apply(many),
                toStr.apply(none),
                toStr.apply(some));
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

    private static void runFolder(String folderPath, String outFile, boolean quiet) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((f) -> f.isFile() && f.getName().endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No files found in folder: " + folderPath);
            return;
        }

        for (File file : files) {
            if (!quiet)
                System.out.println("Processing: " + file.getName());

            Input input;
            try (FileInputStream fis = new FileInputStream(file)) {
                input = Input.parseFrom(fis);
            }

            Result result = Result.checkFor(input);

            // Format results for table
            String resultText = "%d\t%b & %d & %d & %d & %b".formatted(
                    result.none(),
                    result.alternate(),
                    result.few(),
                    result.many(),
                    result.none(), // adjust if needed
                    result.some());

            saveResultsToFile(outFile, file.getName(), resultText);
        }

        System.out.println("All results written to " + outFile);
    }

    private static void saveResultsToFile(String outFilename, String instanceName, String text) {
        File f = new File(outFilename);

        boolean fileIsEmpty = (!f.exists() || f.length() == 0);

        try (FileWriter fw = new FileWriter(outFilename, true)) {
            if (fileIsEmpty) {
                fw.write("instance name        & "
                        + "       $N$ &  alternate &        few &       many &       none &  some\\\\"
                        + System.lineSeparator()
                        + "\\midrule"
                        + System.lineSeparator());
            }
            fw.write("%-20s & ".formatted(instanceName) + text + "\\\\" + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

}
