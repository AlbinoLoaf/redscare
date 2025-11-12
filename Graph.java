import java.util.*;

public class Graph {
    public final List<Node> nodes;
    public final HashMap<String, Integer> map = new HashMap<>();
    public final Set<Integer> reds = new HashSet<>();
    public Boolean isDirected;

    public final UnionFind unionFind;

    public Graph(int n) {
        nodes = new ArrayList<>(n);
        this.isDirected = null;
        unionFind = new UnionFind(n);
    }

    public void addEdgeDirected(int from, int to) {
        nodes.get(from).adjs.add(to);
        unionFind.union(from, to);
    }

    public void addEdgeUndirected(int from, int to) {
        nodes.get(from).adjs.add(to);
        nodes.get(to).adjs.add(from);
        unionFind.union(from, to);
    }

    public void removeUnconnectedComponents(int s, int t) {
        int keepRoot = unionFind.rootOf(s);

        for (int u = nodes.size(); u >= 0; u--) {
            int root = unionFind.rootOf(u);

            if (root == keepRoot)
                continue;

            nodes.remove(u);
            reds.remove(u);
        }
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

    public static class UnionFind {
        private int[] roots;

        public UnionFind(int n) {
            roots = new int[n];
            for (int i = 0; i < n; i++)
                roots[i] = i;
        }

        public int rootOf(int i) {
            if (roots[i] == i)
                return i;
          
            return rootOf(roots[i]);
        }

        private void union(int u, int v) {
            roots[rootOf(u)] = rootOf(v);
        }

        public boolean connected(int u, int v) {
            return rootOf(u) == rootOf(v);
        }
    }
}

