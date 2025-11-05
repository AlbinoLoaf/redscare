/*
 * The below algorithm uses Depth First Search to find a path of alternating nodes from s to t.
 * The Algorithm assumes the graph is directed!
 * Author: Adam Aabye (aaab@itu.dk)
 * 
 * Authors note:
 * I keep track of visited nodes in an array of size Graph.nodes().size(). This allows for N(1) lookup time when determining
 * the visited-status of a given node. The space complexity of the array is O(n), which I don't think is an issue.
 */

public class Alternate {
    public static boolean doesAlternatingPathExist(Graph G, int s, int t) {
        Graph.Node sNode = G.nodes().get(s); // Get start node
        boolean[] visitedList = new boolean[G.nodes().size()]; // Keeps track of whether Node at index has been visited
        // to make sure we don't enter infinit loops in cyclic graphs.
        return traverse(G, sNode, t, visitedList); // Traverse Graph G from s to t
    }

    // I believe this is what the kids call DFS
    public static boolean traverse(Graph G, Graph.Node n, int t, boolean[] visited) {

        boolean answer = false; // If the entire graph is traversed without finding an alternating path, then
                                // nothing happens.

        for (int adj : n.adjs()) { // For each node adjecent to n
            if (!visited[adj]) { // If the node we're looking at hasn't been visited yet.
                visited[adj] = true; // Mark the node as visited.
                Graph.Node node = G.nodes().get(adj); // Get the node from index
                if (n.isRed() != node.isRed()) { // If the adjecent node has the same color as n, then do nothing.
                    if (node == G.nodes().get(t)) { // If the node is t, then we've found an alternating path from s to
                                                    // t.
                        return true;
                    }
                    traverse(G, n, t, visited); // Traverse from
                }
            }
        }
        return answer;
    }
}
