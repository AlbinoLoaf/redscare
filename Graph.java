import java.util.*;

public record Graph(List<Node> nodes, Set<Integer> reds) {
    @Override
    public String toString() {
        String string = "";

        string += "Reds: " + reds + "\n";

        string += "Nodes:\n";
        for (int i = 0; i < nodes.size(); i++) {
            string += i + ": " + nodes.get(i) + "\n";
        }

        return string;
    }

    public static record Node(boolean isRed, List<Integer> adjs) {
        @Override
        public String toString() {
            return adjs.toString();
        }
    }
}
