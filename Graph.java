import java.util.*;

public record Graph(List<Node> nodes, HashMap<String, Integer> map, Set<Integer> reds) {
    public Node get(int i) {
        return nodes.get(i);
    }

    public int size() {
        return nodes.size();
    }

    @Override
    public String toString() {
        String string = "Nodes:\n";
        for (int i = 0; i < nodes.size(); i++) {
            string += i + ": ";
            string += nodes.get(i) + "\n";
        }

        return string;
    }

    public String toStringColored() {
        String string = "";

        string += "Reds: [";

        for (int red : reds()) {
            string += RED + red + RESET + ", ";
        }

        if (reds.isEmpty())
            string += "]\n";
        else
            string = string.substring(0, string.length() - 2) + "]\n";

        string += "Nodes:\n";
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);

            if (node.isRed())
                string += RED;
            string += BOLD + i + RESET + ": ";
            string += node.toStringColored(this) + "\n";
        }
        return string;
    }

    public static record Node(boolean isRed, List<Integer> adjs) {
        @Override
        public String toString() {
            return (isRed ? "(red) " : "") + adjs.toString();
        }

        public String toStringColored(Graph graph) {
            String string = "[";

            for (int adjI : adjs) {
                Node adj = graph.get(adjI);

                if (adj.isRed())
                    string += RED;
                string += adjI + RESET + ", ";
            }

            if (adjs.isEmpty())
                string += "]";
            else
                string = string.substring(0, string.length() - 2) + "]";

            return string;
        }
    }

    private static final String BOLD = "\033[1m";
    private static final String RED = "\033[31m";
    private static final String RESET = "\033[0m";
}
