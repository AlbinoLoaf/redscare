import java.util.*;

public record Graph(List<Node> nodes, HashMap<String, Integer> map) {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes:\n");
        for (int i = 0; i < nodes.size(); i++) {
            sb.append(i).append(": ").append(nodes.get(i)).append("\n");
        }
        return sb.toString();
    }

    public static record Node(boolean isRed, List<Integer> adjs) {
        @Override
        public String toString() {
            return (isRed ? "(red) " : "") + adjs.toString();
        }
    }
}
