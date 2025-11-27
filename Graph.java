import java.util.*;

public class Graph {
    public final List<Node> nodes;
    public final HashMap<String, Integer> identMap = new HashMap<>();

    public final BitSet redSet = new BitSet();
    public final Iterable<Integer> reds = () -> redSet.stream().iterator();

    public Boolean isDirected = null;

    public enum Kind {
        Cyclic,
        DirectedAcyclic,
        Tree,
    }

    // Topologically sorted nodes (only if DAG)
    public Iterable<Integer> nodesTopological;
    public Kind kind = null;

    public final UnionFind unionFind;

    public Graph(int n) {
        nodes = new ArrayList<>(n);
        unionFind = new UnionFind(n);
    }

    public void addEdge(int fromI, int toI) {
        Node from = nodes.get(fromI);
        Node to = nodes.get(toI);

        if (isDirected) {
            from.adjs.add(toI);

            to.inAdjs.add(fromI);
            to.inCount++;
        } else {
            from.adjs.add(toI);
            to.adjs.add(fromI);

            from.inCount++;
            to.inCount++;
        }

        unionFind.union(fromI, toI);
    }

    /**
     * @param rootI is just an arbitrary node in the graph component to check.
     *              (Because for unconnected graphs we need to know which component
     *              to check.)
     */
    public void checkKindForComponentWith(int rootI, boolean isDirected) {
        if (kind != null)
            return;

        this.isDirected = isDirected;

        if (isDirected) {
            // Topological sort

            // Find the node with no incomming edges
            Integer curI = null;
            boolean possiblyTree = true;
            int nodeCount = 0;
            for (int nodeI = 0; nodeI < nodes.size(); nodeI++) {
                // Might be slow to do in a loop?
                if (!unionFind.connected(rootI, nodeI))
                    continue;

                nodeCount++;

                Node node = nodes.get(nodeI);
                if (node.inCount == 0) {
                    assert curI == null;
                    curI = nodeI;
                }

                if (node.inCount > 1)
                    possiblyTree = false;
            }

            if (curI == null) {
                kind = Kind.Cyclic;
                return;
            }

            Queue<Integer> queue = new ArrayDeque<>();
            List<Integer> nodesSorted = new ArrayList<>();
            while (true) {
                nodesSorted.add(curI);

                Node cur = nodes.get(curI);
                for (int adjI : cur.getAdjs()) {
                    Node adj = nodes.get(adjI);
                    assert adj.inCount > 0;

                    adj.inCount--;

                    if (adj.inCount == 0)
                        queue.add(adjI);
                }

                if (queue.isEmpty())
                    break;

                curI = queue.remove();
            }

            if (nodesSorted.size() == nodeCount) {
                this.nodesTopological = nodesSorted;
                if (possiblyTree)
                    kind = Kind.Tree;
                else
                    kind = Kind.DirectedAcyclic;
            } else {
                kind = Kind.Cyclic;
            }
        } else {
            // Simple BFS check
            BitSet visited = new BitSet(nodes.size());

            record Pair(int parentI, int nodeI) {
            }

            Queue<Pair> toVisit = new ArrayDeque<>();
            Pair cur = new Pair(rootI, rootI); // parent doesn't matter for first node
            while (true) {
                visited.set(cur.nodeI);

                Node curNode = nodes.get(cur.nodeI);
                for (int adjI : curNode.getAdjs()) {
                    if (adjI == cur.parentI)
                        continue;

                    if (visited.get(adjI)) {
                        kind = Kind.Cyclic;
                        return;
                    }

                    toVisit.add(new Pair(cur.nodeI, adjI));
                }

                if (toVisit.isEmpty())
                    break;

                cur = toVisit.remove();
            }

            kind = Kind.Tree;
        }
    }

    public Node get(int i) {
        return nodes.get(i);
    }

    public int size() {
        return nodes.size();
    }

    public boolean isRed(int i) {
        return redSet.get(i);
    }

    public String toStringUncolored() {
        String string = "Nodes:\n";
        for (int i = 0; i < nodes.size(); i++) {
            string += i + ": ";
            string += nodes.get(i) + "\n";
        }

        return string;
    }

    @Override
    public String toString() {
        String string = "";

        string += "Reds: [";

        for (int redI = 0; redI < redSet.length(); redI++) {
            if (!redSet.get(redI))
                continue;

            string += RED + BOLD + redI + RESET + ", ";
        }

        if (!redSet.isEmpty())
            string = string.substring(0, string.length() - 2);

        string += "]\n";

        string += "Nodes:\n";
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);

            if (node.isRed())
                string += RED;
            string += BOLD + i + RESET + ": ";
            string += node + "\n";
        }
        return string;
    }

    private static final String BOLD = "\033[1m";
    private static final String RED = "\033[31m";
    private static final String RESET = "\033[0m";

    public class Node {
        public final int i;
        private final List<Integer> adjs = new ArrayList<>();

        // Incomming edges (only if directed)
        private final List<Integer> inAdjs = new ArrayList<>();
        private int inCount = 0; // Only used by checkIfCyclic

        public Node(int i) {
            this.i = i;
        }

        public boolean isRed() {
            return Graph.this.redSet.get(i);
        }

        public Iterable<Integer> getAdjs() {
            return adjs;
        }

        public Iterable<Integer> getInAdjs() {
            assert Graph.this.isDirected;

            return inAdjs;
        }

        public int getInCount() {
            return inCount;
        }

        public String toStringUncolored() {
            return (isRed() ? "(red) " : "") + adjs.toString();
        }

        @Override
        public String toString() {
            String string = "[";

            for (int adjI : adjs) {
                Node adj = Graph.this.get(adjI);

                if (adj.isRed())
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
            // find root
            int root = i;
            while (roots[root] != root) {
                root = roots[root];
            }

            // balance the tree
            int cur = i;
            while (roots[cur] != root) {
                int next = roots[cur];
                roots[cur] = root;
                cur = next;
            }

            return root;
        }

        private void union(int u, int v) {
            int rootU = rootOf(u);
            int rootV = rootOf(v);

            if (rootU != rootV)
                roots[rootU] = rootV;
        }

        public boolean connected(int u, int v) {
            return rootOf(u) == rootOf(v);
        }
    }
}
