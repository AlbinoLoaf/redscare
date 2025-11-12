import java.util.*;

public class Graph {
    public final List<Node> nodes;
    public final HashMap<String, Integer> map = new HashMap<>();
    public final Set<Integer> reds = new HashSet<>();

    private final UnionFind unionFind;

    public Graph(int n) {
        nodes = new ArrayList<>(n);
        unionFind = new UnionFind(n);
    }

    public Node get(int i) {
        return nodes.get(i);
    }

    public int size() {
        return nodes.size();
    }

    @Override
    public String toString() {
        String string = "Nodes:\n";
        for (int i = 0; i < nodes.size(); i++) {
            string += i + ": ";
            string += nodes.get(i) + "\n";
        }

        return string;
    }

    public String toStringColored() {
        String string = "";

        string += "Reds: [";

        for (int red : reds) {
            string += RED + red + RESET + ", ";
        }

        if (reds.isEmpty())
            string += "]\n";
        else
            string = string.substring(0, string.length() - 2) + "]\n";

        string += "Nodes:\n";
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);

            if (node.isRed)
                string += RED;
            string += BOLD + i + RESET + ": ";
            string += node.toStringColored(this) + "\n";
        }
        return string;
    }

    private static final String BOLD = "\033[1m";
    private static final String RED = "\033[31m";
    private static final String RESET = "\033[0m";

    public class Node {
        public final boolean isRed;
        private final List<Integer> adjs = new ArrayList<>();

        public Node(boolean isRed) {
            this.isRed = isRed;
        }

        public void addAdj(int i) {
            adjs.add(i);
        }

        public Iterable<Integer> getAdjs() {
            return adjs;
        }

        @Override
        public String toString() {
            return (isRed ? "(red) " : "") + adjs.toString();
        }

        public String toStringColored(Graph graph) {
            String string = "[";

            for (int adjI : adjs) {
                Node adj = graph.get(adjI);

                if (adj.isRed)
                    string += RED;
                string += adjI + RESET + ", ";
            }

            if (adjs.isEmpty())
                string += "]";
            else
                string = string.substring(0, string.length() - 2) + "]";

            return string;
        }
    }
}

class UnionFind {
    private int[] parents;

    UnionFind(int n) {
        parents = new int[n];
    }
}    

