import java.util.*;

public class Some {
    static class Edge {
        int to, rev;
        int cap;

        Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
        }
    }

    static class MaxFlow {
        List<Edge>[] g;
        boolean[] visited;

        MaxFlow(int n) {
            g = new ArrayList[n];
            for (int i = 0; i < n; i++)
                g[i] = new ArrayList<>();
        }

        void addEdge(int u, int v, int cap) {
            g[u].add(new Edge(v, g[v].size(), cap));
            g[v].add(new Edge(u, g[u].size() - 1, 0));
        }

        int dfs(int u, int t, int f) {
            if (u == t)
                return f;
            visited[u] = true;
            for (Edge e : g[u]) {
                if (!visited[e.to] && e.cap > 0) {
                    int pushed = dfs(e.to, t, Math.min(f, e.cap));
                    if (pushed > 0) {
                        e.cap -= pushed;
                        g[e.to].get(e.rev).cap += pushed;
                        return pushed;
                    }
                }
            }
            return 0;
        }

        int maxFlow(int s, int t) {
            int flow = 0;
            while (true) {
                visited = new boolean[g.length];
                int pushed = dfs(s, t, Integer.MAX_VALUE);
                if (pushed == 0)
                    break;
                flow += pushed;
            }
            return flow;
        }
    }

    private static boolean dfs(Graph G, int current, int target, boolean[] visited, boolean hasRed) {
        if (visited[current])
            return false;
        visited[current] = true;

        if (G.reds.contains(current))
            hasRed = true;

        if (current == target)
            return hasRed;

        for (int neighbor : G.get(current).getAdjs()) {
            if (dfs(G, neighbor, target, visited, hasRed))
                return true;
        }

        return false;
    }

    public static boolean doesPathWithRedExist(Graph G, int s, int t) {
        int n = G.size();

        // vertex IDs for splitting
        int[] v_in = new int[n];
        int[] v_out = new int[n];
        int ID = 0;
        for (int i = 0; i < n; i++) {
            v_in[i] = ID++;
            v_out[i] = ID++;
        }

        // super-sink to collect flow from red nodes
        int superSink = ID++;
        MaxFlow mf = new MaxFlow(ID);

        if (false) { // undirected graph
            // Check if it's a tree (acyclic)
            if (true) {// G.isAcyclic) {if (G.isAcyclic) {
                // Each undirected edge is unique path, simple DFS would suffice
                return dfs(G, s, t, new boolean[G.size()], false);
            } else {
                // general undirected graph with cycles → max-flow does NOT guarantee simple
                // path
                throw new IllegalArgumentException("Cannot guarantee correctness on cyclic undirected graph");
            }
        } else if (G.isDirected) {
            if (true) {// G.isAcyclic) //DAG
                for (int i = 0; i < n; i++) {
                    mf.addEdge(v_in[i], v_out[i], 1);
                }

                for (int u = 0; u < n; u++) {
                    for (int v : G.get(u).getAdjs()) {
                        mf.addEdge(v_out[u], v_in[v], 1);
                    }
                }
            } else {
                // cyclic directed graph → max-flow may fail
                throw new IllegalArgumentException("Cannot guarantee correctness on cyclic directed graph");
            }
        }

        // Connect all red nodes to superSink
        for (int r : G.reds) {
            mf.addEdge(v_out[r], superSink, 1);
        }

        // source is s_in
        int source = v_in[s];
        int flow = mf.maxFlow(source, superSink);

        // If flow >= 1 it is connected to at least one red node
        if (flow == 0)
            return false;

        // finally check if the red reached can reach t
        for (int r : G.reds) {
            if (reachable(G, r, t))
                return true;
        }
        return false;

    }

    // simple DFS to see if red node can reach t in original graph
    private static boolean reachable(Graph G, int start, int target) {
        boolean[] vis = new boolean[G.size()];
        return dfsReach(G, start, target, vis);
    }

    private static boolean dfsReach(Graph G, int u, int target, boolean[] vis) {
        if (u == target)
            return true;
        vis[u] = true;
        for (int v : G.get(u).getAdjs()) {
            if (!vis[v] && dfsReach(G, v, target, vis))
                return true;
        }
        return false;
    }
}

// public static boolean doesPathWithRedExist(Graph graph, int s, int t, boolean
// directed) {
// // Step 1: vertices reachable from s
// BitSet reachableFromS = bfs(graph, s);

// // Step 2: vertices that can reach t
// BitSet canReachT = directed ? bfsReversed(graph, t) : bfs(graph, t);

// // Step 3: check overlap on red vertices
// for (int v = 0; v < graph.size(); v++) {
// Graph.Node node = graph.get(v);
// if (node.isRed && reachableFromS.get(v) && canReachT.get(v)) {
// return true;
// }
// }

// return false;
// }

// /** Standard BFS */
// private static BitSet bfs(Graph graph, int start) {
// BitSet visited = new BitSet();
// Deque<Integer> queue = new ArrayDeque<>();
// queue.add(start);
// visited.set(start);

// while (!queue.isEmpty()) {
// int cur = queue.removeFirst();
// for (int nxt : graph.get(cur).getAdjs()) {
// if (!visited.get(nxt)) {
// visited.set(nxt);
// queue.addLast(nxt);
// }
// }
// }
// return visited;
// }

// /** BFS on reversed edges (for directed graphs only) */
// private static BitSet bfsReversed(Graph graph, int start) {
// BitSet visited = new BitSet();
// Deque<Integer> queue = new ArrayDeque<>();
// queue.add(start);
// visited.set(start);

// // For each node, check all others that point to it
// while (!queue.isEmpty()) {
// int cur = queue.removeFirst();
// for (int u = 0; u < graph.size(); u++) {
// for (int v : graph.get(u).getAdjs()) {
// if (v == cur && !visited.get(u)) {
// visited.set(u);
// queue.addLast(u);
// }
// }
// }
// }

// return visited;
// }
// }
