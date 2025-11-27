/*
 * The below algorithm uses Depth First Search to find a path of alternating nodes from s to t.
 * The Algorithm assumes the graph is directed!
 * Author: Adam Aabye (aaab@itu.dk)
 * 
 * Authors note:
 * I keep track of visited nodes in an array of size Graph.size(). This allows for N(1) lookup time when determining
 * the visited-status of a given node. The space complexity of the array is O(n), which I don't think is an issue.
 */

import java.util.BitSet;

public class Alternate {

    static BitSet visited;

    public static boolean alternatingPathExist(Graph G, int s, int t) {
        // Graph.Node sNode = G.get(s); // Get start node
        visited = new BitSet(G.size()); // Keeps track of whether Node at index has been visited
        // to make sure we don't enter infinit loops in cyclic graphs.
        return traverse(G, s, t); // Traverse Graph G from s to t
    }

    // I believe this is what the kids call DFS
    public static boolean traverse(Graph G, int n, int t) {
        boolean answer = false; // If the entire graph is traversed without finding an alternating path, then
                                // nothing happens.
        visited.set(n); // Mark current node as visited
        Graph.Node cur = G.get(n);
        for (int adj : cur.getAdjs()) { // For each node adjecent to n
            if (!visited.get(adj)) { // If the node we're looking at hasn't been visited yet.
                // Mark the node as visited.
                Graph.Node node = G.get(adj); // Get the node from index
                if (cur.isRed() != node.isRed()) { // If the adjecent node has the same color as n, then do nothing.
                    if (node == G.get(t)) // If the node is t, then we've found an alternating path from s to t.
                        return true;

                    answer = traverse(G, adj, t); // Traverse from
                    if (answer)
                        break;
                }
            }
        }
        return answer;
    }
}