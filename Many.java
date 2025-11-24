import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Many {
    public static int mostRedPath(Graph graph, int s, int t) {
        if (graph.kind == Graph.Kind.Cyclic)
            return -1; // TODO: Solve NP = P

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
        // TODO use the topological sort approach instead
        int[] maxReds = new int[graph.size()];
        Arrays.fill(maxReds, -1);

        if (graph.isRed(s))
            maxReds[s]++;

        Queue<Integer> toVisit = new ArrayDeque<>();
        int curI = s;
        while (true) {
            if (maxReds[curI] == -1)
                maxReds[curI] = 0;

            Graph.Node curNode = graph.get(curI);
            for (int adjI : curNode.getAdjs()) {
                int potentialReds = maxReds[curI];
                if (graph.isRed(adjI))
                    potentialReds++;

                if (potentialReds > maxReds[adjI]) {
                    maxReds[adjI] = potentialReds;

                    // TODO

                    toVisit.add(adjI);
                }
            }

            if (toVisit.isEmpty())
                break;

            curI = toVisit.remove();
        }

        return maxReds[t];
    }
}
