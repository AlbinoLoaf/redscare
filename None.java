import java.util.*;

public class None {
    public static int lengthOfShortestPathWithoutReds(Graph graph, int startI, int terminalI) {
        if (graph.nodes().get(startI).isRed() ||
            graph.nodes().get(terminalI).isRed())
            return -1;

        BitSet visited = new BitSet();

        int lastInLayerI = startI;
        int length = 0;

        Deque<Integer> toVisit = new ArrayDeque<>();
        int currentI = startI;
        while (true) {
            if (currentI == terminalI)
                return length;

            visited.set(currentI);

            Graph.Node current = graph.nodes().get(currentI);
            if (!current.isRed()) {
                for (Integer adjI : current.adjs()) {
                    if (!visited.get(adjI))
                        toVisit.addLast(adjI);
                }
            }

            if (toVisit.isEmpty())
                return -1;

            if (currentI == lastInLayerI) {
                lastInLayerI = toVisit.peekLast();
                length++;
            }

            currentI = toVisit.removeFirst();
        }
    }
}
