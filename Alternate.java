/*
 * The below algorithm uses Depth First Search to find a path of alternating nodes from s to t.
 * The Algorithm assumes the graph is directed!
 * Author: Adam Aabye (aaab@itu.dk)
 */

public class Alternate {
    public static boolean doesAlternatingPathExist(Graph G, int s, int t) {
        Graph.Node sNode = G.nodes().get(s); // Get start node
        return traverse(G, sNode, t); // Traverse Graph G from s to t
    }

    // I believe this is what the kids call DFS
    public static boolean traverse(Graph G, Graph.Node n, int t) {

        boolean answer = false; // If the entire graph is traversed without finding an alternating path, then
                                // nothing happens.

        for (int adj : n.adjs()) { // For each node adjecent to n
            Graph.Node node = G.nodes().get(adj); // Get the node from index
            if (n.isRed() != node.isRed()) { // If the adjecent node has the same color as n, then do nothing.
                if (node == G.nodes().get(t)) { // If the node is t, then we've found an alternating path from s to t.
                    return true;
                }
                traverse(G, n, t); // Traverse from
            }
        }
        return answer;
    }
}
