package problem2D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class VisibilityGraph2D {

    public List<Point2D> starShapedPolygon;
    public List<List<Edge>> edges;
    public List<Queue<Integer>> queues;

    public VisibilityGraph2D(List<Point2D> starShapedPolygon) {
        this.starShapedPolygon = starShapedPolygon;
        this.queues = new ArrayList<>();
        this.edges = new ArrayList<>();
        createGraph();
    }

    public void createGraph() {
        setupQueues();
        setupEdgeLists();
        for (int i = 0; i < starShapedPolygon.size() - 1; i++) {
            proceed(i, i + 1);
        }

    }

    private void setupEdgeLists() {
        for (int i = 0; i < starShapedPolygon.size()-1; i++) {
            edges.add(new ArrayList<>());
        }
    }

    private void setupQueues() {
        for (int i = 0; i < starShapedPolygon.size(); i++) {
            queues.add(new LinkedList<>());
        }
    }

    public void add(int from, int to) {
        edges.get(from).add(new Edge(from, to));
    }

    public boolean turn(int i, int j, int k) {
        return starShapedPolygon.get(k).isOnLeftOfVector(starShapedPolygon.get(i), starShapedPolygon.get(j));
    }

    public void proceed(int i, int j) {
        Queue<Integer> queue = queues.get(i);
        while (!queue.isEmpty() && turn(queue.peek(), i, j)) {
            proceed(queue.peek(), j);
            queue.poll();
        }
        add(i, j);
        queues.get(j).add(i);
    }

}
