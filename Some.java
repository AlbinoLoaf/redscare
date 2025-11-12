import java.util.*;

public class Some {
    public static boolean doesPathWithRedExist(Graph graph, int s, int t, boolean directed) {
        // Step 1: vertices reachable from s
        BitSet reachableFromS = bfs(graph, s);

        // Step 2: vertices that can reach t
        BitSet canReachT = directed ? bfsReversed(graph, t) : bfs(graph, t);

        // Step 3: check overlap on red vertices
        for (int v = 0; v < graph.size(); v++) {
            Graph.Node node = graph.get(v);
            if (node.isRed && reachableFromS.get(v) && canReachT.get(v)) {
                return true;
            }
        }

        return false;
    }

    /** Standard BFS */
    private static BitSet bfs(Graph graph, int start) {
        BitSet visited = new BitSet();
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        visited.set(start);

        while (!queue.isEmpty()) {
            int cur = queue.removeFirst();
            for (int nxt : graph.get(cur).getAdjs()) {
                if (!visited.get(nxt)) {
                    visited.set(nxt);
                    queue.addLast(nxt);
                }
            }
        }
        return visited;
    }

    /** BFS on reversed edges (for directed graphs only) */
    private static BitSet bfsReversed(Graph graph, int start) {
        BitSet visited = new BitSet();
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        visited.set(start);

        // For each node, check all others that point to it
        while (!queue.isEmpty()) {
            int cur = queue.removeFirst();
            for (int u = 0; u < graph.size(); u++) {
                for (int v : graph.get(u).getAdjs()) {
                    if (v == cur && !visited.get(u)) {
                        visited.set(u);
                        queue.addLast(u);
                    }
                }
            }
        }

        return visited;
    }
}
