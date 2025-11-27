import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Many {
    public static Integer mostRedPath(Graph graph, int s, int t) {
        if (graph.kind == Graph.Kind.Cyclic)
            return null;

        if (graph.isDirected)
            return checkMaxPath(graph, s, t);
        else
            return checkOnlyPath(graph, s, t);
    }

    private static int checkOnlyPath(Graph graph, int s, int t) {
        int[] reds = new int[graph.size()];
        if (graph.isRed(s))
            reds[s]++;

        Queue<Integer> toVisit = new ArrayDeque<>();
        int curI = s;
        while (true) {
            if (graph.isRed(curI))
                reds[curI]++;

            if (curI == t)
                return reds[curI];

            Graph.Node curNode = graph.get(curI);
            for (int adjI : curNode.getAdjs()) {
                if (adjI == curI)
                    continue;

                reds[adjI] = reds[curI];
                toVisit.add(adjI);
            }

            if (toVisit.isEmpty())
                return -1;

            curI = toVisit.remove();
        }
    }

    private static int checkMaxPath(Graph graph, int s, int t) {
        int[] lengths = new int[graph.size()];
        Arrays.fill(lengths, -1);
        lengths[s] = graph.isRed(s) ? 1 : 0;

        for (int curI : graph.nodesTopological) {
            if (curI == s)
                continue;

            int maxLength = -1;
            for (int inI : graph.get(curI).getInAdjs()) {
                int inLength = lengths[inI];

                if (inLength > maxLength)
                    maxLength = inLength;
            }

            if (curI == t) {
                if (maxLength == -1)
                    return -1;
                else
                    return maxLength + (graph.isRed(curI) ? 1 : 0);
            }

            if (maxLength == -1)
                continue;
            else
                lengths[curI] = maxLength + (graph.isRed(curI) ? 1 : 0);
        }

        throw new IllegalStateException("Should have returned when reaching t");
    }
}