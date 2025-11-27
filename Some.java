import java.util.*;

public class Some {

    public static Boolean doesPathWithRedExist(Graph G, int s, int t) {

        // If s or t is red: reduce to simple s→t reachability
        if (G.isRed(s) || G.isRed(t)) {
            return bfsReachable(G, s, t);
        }

        // ===== CASE 1: Directed graph =====
        if (G.isDirected) {

            // Detect cycles → NP-hard case
            if (G.kind == Graph.Kind.Cyclic) {
                return null;
            }

            boolean[] reachFromS = bfs(G, s);

            // For every red node r:
            for (int r : G.reds) {
                boolean[] reachFromR = bfs(G, r);
                if (reachFromS[r] && reachFromR[t]) {
                    return true;
                }
            }
            return false;
        }

        // ===== CASE 2: Undirected graph (flow construction) =====
        FlowNetwork FN = buildFlowNetwork(G, s, t);
        int flowSink = G.size() * 2;

        for (int r : G.reds) {
            int r_out = r * 2 + 1;

            FN.resetFlow();
            int maxf = FN.maxFlow(r_out, flowSink);

            if (maxf == 2)
                return true;
        }

        return false;
    }

    private static boolean bfsReachable(Graph G, int s, int t) {
        boolean[] vis = new boolean[G.size()];
        ArrayDeque<Integer> q = new ArrayDeque<>();
        vis[s] = true;
        q.add(s);

        while (!q.isEmpty()) {
            int u = q.poll();
            if (u == t)
                return true;
            for (int v : G.get(u).getAdjs()) {
                if (!vis[v]) {
                    vis[v] = true;
                    q.add(v);
                }
            }
        }
        return false;
    }

    private static boolean[] bfs(Graph G, int start) {
        boolean[] vis = new boolean[G.size()];
        ArrayDeque<Integer> q = new ArrayDeque<>();
        vis[start] = true;
        q.add(start);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : G.get(u).getAdjs()) {
                if (!vis[v]) {
                    vis[v] = true;
                    q.add(v);
                }
            }
        }
        return vis;
    }

    private static FlowNetwork buildFlowNetwork(Graph G, int s, int t) {
        int n = G.size();
        int sink = n * 2;
        int N = sink + 1;

        FlowNetwork FN = new FlowNetwork(N);

        // Add undirected edges as two directed with cap=1
        for (int u = 0; u < n; u++) {
            for (int v : G.get(u).getAdjs()) {

                // u_out -> v_in
                FN.addEdge(u * 2 + 1, v * 2, 1);
            }
        }

        // Add v_in -> v_out edges
        for (int v = 0; v < n; v++) {
            FN.addEdge(2 * v, 2 * v + 1, 1);
        }

        // Connect s_out and t_out to the sink
        FN.addEdge(s * 2 + 1, sink, 1);
        FN.addEdge(t * 2 + 1, sink, 1);

        return FN;
    }

    static class FlowNetwork {
        private final int N;
        private int[][] capacity;
        private int[][] flow;

        public FlowNetwork(int N) {
            this.N = N;
            capacity = new int[N][N];
            flow = new int[N][N];
        }

        public void addEdge(int u, int v, int cap) {
            capacity[u][v] += cap;
        }

        public void resetFlow() {
            for (int i = 0; i < N; i++)
                Arrays.fill(flow[i], 0);
        }

        public int maxFlow(int s, int t) {
            int result = 0;

            while (true) {
                int[] parent = new int[N];
                Arrays.fill(parent, -1);
                parent[s] = s;

                ArrayDeque<Integer> q = new ArrayDeque<>();
                q.add(s);

                while (!q.isEmpty() && parent[t] == -1) {
                    int u = q.poll();
                    for (int v = 0; v < N; v++) {
                        if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                            parent[v] = u;
                            q.add(v);
                        }
                    }
                }

                if (parent[t] == -1)
                    break;

                int aug = Integer.MAX_VALUE;
                for (int v = t; v != s; v = parent[v]) {
                    int u = parent[v];
                    aug = Math.min(aug, capacity[u][v] - flow[u][v]);
                }

                for (int v = t; v != s; v = parent[v]) {
                    int u = parent[v];
                    flow[u][v] += aug;
                    flow[v][u] -= aug;
                }

                result += aug;
            }

            return result;
        }
    }
}