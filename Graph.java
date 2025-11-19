import java.util.*;

public class Graph {
    public final List<Node> nodes;
    public final HashMap<String, Integer> map = new HashMap<>();
    public final Set<Integer> reds = new HashSet<>();

    public Boolean isDirected = null;
    public Boolean isCyclic = null;
    public Boolean isTree = null;

    public final UnionFind unionFind;

    public Graph(int n) {
        nodes = new ArrayList<>(n);
        unionFind = new UnionFind(n);
    }

    public void addEdgeDirected(int fromI, int toI) {
        nodes.get(fromI).adjs.add(toI);
        nodes.get(toI).incomming++;

        unionFind.union(fromI, toI);
    }

    public void addEdgeUndirected(int fromI, int toI) {
        Node from = nodes.get(fromI);
        Node to = nodes.get(toI);

        from.adjs.add(toI);
        to.adjs.add(fromI);

        from.incomming++;
        to.incomming++;

        unionFind.union(fromI, toI);
    }

    /**
     * @param rootI is just an arbitrary node in the graph component to check.
     * (Because for unconnected graphs we need to know which component to check.)
     */
    public void checkIfCyclic(int rootI) {
        if (isCyclic != null)
            panic("Already checked if graph is cyclic.");

        if (isDirected == null)
            panic("Graph directionality not set.");

        if (isDirected) {
            // Topological sort

            // Find root (node with no incomming edges)
            Integer curI = null;
            int nodeCount = 0;
            for (int i = 0; i < nodes.size(); i++) {
                // Might be slow to do in a loop?
                if (!unionFind.connected(rootI, i))
                    continue;

                nodeCount++;

                if (nodes.get(i).incomming == 0) {
                    assert curI == null;
                    curI = i;
                }
            }

            if (curI == null) {
                isCyclic = true;
                return;
            }

            Queue<Integer> queue = new ArrayDeque<>();
            int visitedCount = 0;
            while (true) {
                visitedCount++;

                Node cur = nodes.get(curI);
                for (int adjI : cur.getAdjs()) {
                    Node adj = nodes.get(adjI);
                    assert adj.incomming > 0;

                    adj.incomming--;

                    if (adj.incomming == 0)
                        queue.add(adjI);
                }

                if (queue.isEmpty())
                    break;

                curI = queue.remove();
            }

            isCyclic = visitedCount != nodeCount;
        }
        else {
            // Simple BFS check
            BitSet visited = new BitSet(nodes.size());

            record Pair(int parentI, int nodeI) {}

            Queue<Pair> toVisit = new ArrayDeque<>();
            Pair cur = new Pair(rootI, rootI); // parent doesn't matter for first node
            while (true) {
                visited.set(cur.nodeI);

                Node curNode = nodes.get(cur.nodeI);
                for (int adjI : curNode.getAdjs()) {
                    if (adjI == cur.parentI)
                        continue;

                    if (visited.get(adjI)) {
                        isCyclic = true;
                        return;
                    }

                    toVisit.add(new Pair(cur.nodeI, adjI));
                }

                if (toVisit.isEmpty())
                    break;

                cur = toVisit.remove();
            }

            isCyclic = false;
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
        private int incomming = 0; // Only used by checkIfCyclic

        public Node(boolean isRed) {
            this.isRed = isRed;
        }

        public Iterable<Integer> getAdjs() {
            return adjs;
        }

        public int getIncomming() {
            return incomming;
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

    private static void panic(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}

