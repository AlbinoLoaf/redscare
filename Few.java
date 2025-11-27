import java.util.*;

public class Few {
    public static int leastRedPath(Graph graph, int s, int t) {
        BitSet visited = new BitSet();

        int reds = 0;

        Deque<Integer> toVisitBlacks = new ArrayDeque<>();
        Deque<Integer> toVisitReds = new ArrayDeque<>();

        int cur = s;
        while (true) {
            Graph.Node curNode = graph.get(cur);
            if (curNode.isRed())
                reds++;

            if (cur == t)
                return reds;

            visited.set(cur);

            for (Integer adjI : curNode.getAdjs()) {
                if (!visited.get(adjI))
                    if (graph.isRed(adjI)) {
                        toVisitReds.addLast(adjI);
                    } else {
                        toVisitBlacks.addLast(adjI);
                    }
            }

            if (toVisitBlacks.isEmpty() && toVisitReds.isEmpty())
                return -1;

            if (!toVisitBlacks.isEmpty()) {
                cur = toVisitBlacks.removeFirst();
            } else {
                cur = toVisitReds.removeFirst();
            }

        }
    }

    public static int dijskraLeastRedPath(Graph graph, int s, int t) {
        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[s] = graph.nodes.get(s).isRed() ? 1 : 0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(a -> dist[a]));
        pq.add(s);

        while (!pq.isEmpty()) {
            int u = pq.poll();

            if (u == t) {
                return dist[u];
            }

            for (int v : graph.get(u).getAdjs()) {
                int alt = dist[u] + (graph.nodes.get(v).isRed() ? 1 : 0);
                if (alt < dist[v]) {
                    dist[v] = alt;
                    pq.add(v);
                }
            }
        }

        return -1;
    }
}