import java.util.*;

public class None {
    public static int lengthOfShortestPathWithoutReds(Graph graph, int s, int t) {
        if (graph.get(s).isRed() ||
            graph.get(t).isRed())
            return -1;

        BitSet visited = new BitSet();

        int lastInLayer = s;
        int pathLen = 0;

        Deque<Integer> toVisit = new ArrayDeque<>();
        int cur = s;
        while (true) {
            if (cur == t)
                return pathLen;

            visited.set(cur);

            Graph.Node curNode = graph.get(cur);
            if (!curNode.isRed()) {
                for (Integer adjI : curNode.adjs()) {
                    if (!visited.get(adjI))
                        toVisit.addLast(adjI);
                }
            }

            if (toVisit.isEmpty())
                return -1;

            if (cur == lastInLayer) {
                lastInLayer = toVisit.peekLast();
                pathLen++;
            }

            cur = toVisit.removeFirst();
        }
    }
}
