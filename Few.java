import java.util.*;

public class Few {
    public static int LeastRedPath(Graph graph, int s, int t) {
        BitSet visited = new BitSet();

        int reds = 0;

        Deque<Integer> toVisitBlacks = new ArrayDeque<>();
        Deque<Integer> toVisitReds = new ArrayDeque<>();

        int cur = s;
        while (true) {
            if (graph.nodes().get(cur).isRed()) {
                reds++;
            }
            if (cur == t)
                return reds;

            visited.set(cur);

            Graph.Node curNode = graph.get(cur);
            for (Integer adjI : curNode.adjs()) {
                if (!visited.get(adjI))
                    if (graph.nodes().get(adjI).isRed()) {
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
}
