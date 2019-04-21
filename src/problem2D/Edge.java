package problem2D;

import java.util.ArrayList;
import java.util.List;

public class Edge {

    public int from;
    public int to;
    public List<Edge> validChildEdges;

    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
        this.validChildEdges = new ArrayList<>();
    }

    public void addChild(Edge edge){
        validChildEdges.add(edge);
    }

}
