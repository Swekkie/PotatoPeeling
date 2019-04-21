package problem2D;

import java.util.ArrayList;
import java.util.List;

public class LongestPathItem {
    public Edge edge;
    public double area;
    public double areaChain;
    public List<Edge> chain;
    public boolean visited;

    public LongestPathItem(Edge edge){
        this.edge = edge;
        this.visited = false;
        this.chain = new ArrayList<>();
    }

    public void firstVisit(){
        visited = true;
        areaChain = area;
        chain.add(edge);
    }

    public void updateChain(LongestPathItem parent){
        this.chain = new ArrayList<>(parent.chain);
        this.chain.add(edge);
        this.areaChain = area + parent.areaChain;
    }

}
