import java.util.*;

public class Some {

    // Standard EK flow graph
    static class EK {
        static class Edge {
            int to;
            int rev;      // index in adj[to]
            int cap;      // residual cap
            final int origCap;

            Edge(int to, int rev, int cap) {
                this.to = to;
                this.rev = rev;
                this.cap = cap;
                this.origCap = cap;
            }
        }

        private final List<Edge>[] adj;
        private final int n;

        @SuppressWarnings("unchecked")
        EK(int n) {
            this.n = n;
            adj = new ArrayList[n];
            for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        }

        void addEdge(int u, int v, int cap) {
            Edge a = new Edge(v, adj[v].size(), cap);
            Edge b = new Edge(u, adj[u].size(), 0);
            adj[u].add(a);
            adj[v].add(b);
        }

        List<Edge>[] getAdj() { return adj; }

        // Runs Edmonds-Karp BFS for next augmenting path
        int bfs(int s, int t, int[] parentV, int[] parentE) {
            Arrays.fill(parentV, -1);
            Arrays.fill(parentE, -1);
            Queue<Integer> q = new ArrayDeque<>();
            q.add(s);
            parentV[s] = s;

            while (!q.isEmpty()) {
                int u = q.poll();
                for (int ei = 0; ei < adj[u].size(); ei++) {
                    Edge e = adj[u].get(ei);
                    if (parentV[e.to] == -1 && e.cap > 0) {
                        parentV[e.to] = u;
                        parentE[e.to] = ei;
                        if (e.to == t) return 1;  // found
                        q.add(e.to);
                    }
                }
            }
            return 0;
        }

        // Returns maximum flow up to capLimit
        int maxFlow(int s, int t, int capLimit) {
            int flow = 0;
            int[] parentV = new int[n];
            int[] parentE = new int[n];

            while (flow < capLimit && bfs(s, t, parentV, parentE) == 1) {
                // Always unit-capacity edges → bottleneck = 1
                int f = 1;

                // Augment
                int cur = t;
                while (cur != s) {
                    int pv = parentV[cur];
                    int ei = parentE[cur];
                    Edge e = adj[pv].get(ei);
                    e.cap -= f;
                    adj[cur].get(e.rev).cap += f;
                    cur = pv;
                }

                flow += f;
            }

            return flow;
        }
    }

    // ------------------------------------------------------------------------------------
    //          Build layered graph & extract path
    // ------------------------------------------------------------------------------------

    public static boolean findPathThroughAnyRed_EK(Graph g, int s, int t) {
        int n = g.size();
        if (n == 0) return false;

        final int LAYERS = 2;
        final int TOTAL = LAYERS * n;
        EK ek = new EK(TOTAL);

        java.util.function.BiFunction<Integer,Integer,Integer> lid =
                (layer, node) -> layer * n + node;

        // Build layered graph
        for (int u = 0; u < n; u++) {
            for (int v : g.get(u).getAdjs()) {
                if (g.isRed(v)) {
                    ek.addEdge(lid.apply(0,u), lid.apply(1,v), 1);
                } else {
                    ek.addEdge(lid.apply(0,u), lid.apply(0,v), 1);
                }
                ek.addEdge(lid.apply(1,u), lid.apply(1,v), 1);
            }
        }

        int source = g.isRed(s) ? lid.apply(1,s) : lid.apply(0,s);
        int sink   = lid.apply(1,t);

        // Try to push 1 unit of flow
        int flow = ek.maxFlow(source, sink, 1);
        if (flow < 1) return false;

        // Extract path by DFS following used edges
        List<EK.Edge>[] adj = ek.getAdj();
        boolean[] visited = new boolean[TOTAL];
        List<Integer> layeredPath = new ArrayList<>();

        class PF {
            boolean dfs(int u) {
                if (u == sink) {
                    layeredPath.add(u);
                    return true;
                }
                visited[u] = true;
                for (EK.Edge e : adj[u]) {
                    if (e.origCap - e.cap <= 0) continue;
                    if (visited[e.to]) continue;

                    // Consume this unit of used flow
                    e.cap += 1;
                    adj[e.to].get(e.rev).cap -= 1;

                    if (dfs(e.to)) {
                        layeredPath.add(u);
                        return true;
                    }

                    // undo
                    e.cap -= 1;
                    adj[e.to].get(e.rev).cap += 1;
                }
                return false;
            }
        }

        new PF().dfs(source);
        Collections.reverse(layeredPath);

        // Convert layered nodes back to original node indices
        List<Integer> result = new ArrayList<>();
        for (int x : layeredPath) {
            int orig = x % n;
            if (result.isEmpty() || result.get(result.size()-1) != orig)
                result.add(orig);
        }

        System.out.println("\"Some\" path found:");
        for (int i : result) {
            Graph.Node node = g.get(i);
            String name = g.identMap.entrySet().stream().filter(e -> e.getValue() == i).findAny().get().getKey();
            System.out.println(node.isRed() ? (name + " *") : name);
        }

        return true;
    }
}
