import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Clustering {
    private CC connected; // CC implementation
    private int numClusters; // number of clusters
    private int numLocations; // number of locations

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        // corner cases
        if (locations == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }
        if (k < 1 || k > locations.length) {
            throw new IllegalArgumentException("k is out of range");
        }
        numLocations = locations.length;
        // create EdgeWeightedGraph with locations as vertices
        EdgeWeightedGraph map = new EdgeWeightedGraph(locations.length);
        for (int i = 0; i < numLocations; i++) {
            // no duplicates
            for (int j = i + 1; j < numLocations; j++) {
                map.addEdge(new Edge(i, j, locations[i].distanceTo(locations[j])));
            }
        }
        // insert EdgeWeightedGraph into Kruskal's algorithm
        KruskalMST minimumLocations = new KruskalMST(map);
        MinPQ<Edge> edgeMinPQ = new MinPQ<>();
        for (Edge e : minimumLocations.edges()) {
            edgeMinPQ.insert(e);
        }
        // build cluster graph
        EdgeWeightedGraph clusterGraph = new EdgeWeightedGraph(locations.length);
        for (int i = 0; i < numLocations - k; i++) {
            clusterGraph.addEdge(edgeMinPQ.delMin());
        }
        connected = new CC(clusterGraph);
        numClusters = k;
    }

    // return the cluster of the ith location
    public int clusterOf(int i) {
        // corner case
        if (i < 0 || i > numLocations - 1) {
            throw new IllegalArgumentException("Argument out of range");
        }
        return connected.id(i);
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {
        // corner cases
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        if (input.length != numLocations) {
            throw new IllegalArgumentException("Input has different length "
                                                       + "than number of locations");
        }
        int[] reducedDimensions = new int[numClusters];
        for (int i = 0; i < input.length; i++) {
            reducedDimensions[clusterOf(i)] += input[i];
        }
        return reducedDimensions;
    }

    // unit testing (required)
    public static void main(String[] args) {
        // arguments c point clouds with p points each
        int c = Integer.parseInt(args[0]);
        int p = Integer.parseInt(args[1]);
        // Point2D array centers for each point cloud center
        Point2D[] centers = new Point2D[c];
        for (int i = 0; i < c; i++) {
            while (true) {
                double x = StdRandom.uniformDouble(0, 1000);
                double y = StdRandom.uniformDouble(0, 1000);
                Point2D candidate = new Point2D(x, y);
                boolean valid = true;
                for (int j = 0; j < i; j++) {
                    if (candidate.distanceTo(centers[j]) < 4.0) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    centers[i] = candidate;
                    break;
                }
            }
        }
        // fill in Point2D array locations
        Point2D[] locations = new Point2D[c * p];
        int idx = 0;
        for (int i = 0; i < c; i++) {
            Point2D center = centers[i];
            for (int j = 0; j < p; j++) {
                while (true) {
                    double x = StdRandom.uniformDouble(center.x() - 1, center.x() + 1);
                    double y = StdRandom.uniformDouble(center.y() - 1, center.y() + 1);
                    Point2D candidate = new Point2D(x, y);
                    if (candidate.distanceTo(center) <= 1.0) {
                        locations[idx++] = candidate;  // store in locations
                        break;  // exit while loop
                    }
                }
            }
        }
        Clustering test = new Clustering(locations, c);

        for (int i = 0; i < c; i++) {
            int start = i * p;
            int end = start + p;
            int clusterId = test.clusterOf(start);

            for (int j = start + 1; j < end; j++) {
                if (test.clusterOf(j) != clusterId) {
                    StdOut.println("Error! " + j);
                }
            }
        }
    }
}
