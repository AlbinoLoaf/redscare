import java.util.*;

public class None {
    public static int lengthOfShortestPathWithoutReds(Graph graph, int s, int t) {
        if (graph.get(s).isRed ||
            graph.get(t).isRed)
            return -1;

        BitSet visited = new BitSet();

        int lastInLayer = s;
        int pathLen = 0;

        Deque<Integer> toVisit = new ArrayDeque<>();
        int curI = s;
        while (true) {
            if (curI == t)
                return pathLen;

            visited.set(curI);

            Graph.Node cur = graph.get(curI);
            if (!cur.isRed) {
                for (int adjI : cur.getAdjs()) {
                    if (!visited.get(adjI))
                        toVisit.addLast(adjI);
                }
            }

            if (toVisit.isEmpty())
                return -1;

            if (curI == lastInLayer) {
                lastInLayer = toVisit.peekLast();
                pathLen++;
            }

            curI = toVisit.removeFirst();
        }
    }
}
