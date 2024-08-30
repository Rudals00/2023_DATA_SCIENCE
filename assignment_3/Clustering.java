package clustering;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public class Clustering {
    public static void main(String[] args) throws IOException {
        String filename = args[0];
        int n = Integer.parseInt(args[1]);
        double eps = Double.parseDouble(args[2]);
        int minPts = Integer.parseInt(args[3]);

        List<Point> points = loadPoints(filename);
        List<List<Point>> clusters = dbscan(points, eps, minPts);
        clusters = getTopClusters(clusters, n);

        writeOutput(clusters, filename);
    }

    static List<Point> loadPoints(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(Paths.get(filename))));
        List<Point> points = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            Point p = new Point(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            points.add(p);
        }

        reader.close();
        return points;
    }

    static List<List<Point>> dbscan(List<Point> points, double eps, int minPts) {
        List<List<Point>> clusters = new ArrayList<>();
        for (Point p : points) {
            if (!p.visited) {
                p.visited = true;
                List<Point> neighbors = getNeighbors(p, points, eps);
                if (neighbors.size() >= minPts) {
                    List<Point> cluster = new ArrayList<>();
                    expandCluster(p, neighbors, cluster, points, eps, minPts);
                    clusters.add(cluster);
                }
            }
        }
        return clusters;
    }

    static void expandCluster(Point p, List<Point> neighbors, List<Point> cluster,
                              List<Point> points, double eps, int minPts) {
        cluster.add(p);
        for (Point point : neighbors) {
            if (!point.visited) {
                point.visited = true;
                List<Point> pointNeighbors = getNeighbors(point, points, eps);
                if (pointNeighbors.size() >= minPts) {
                    expandCluster(point, pointNeighbors, cluster, points, eps, minPts);
                }
            }
            if (!isMemberOfAnyCluster(point, cluster)) {
                cluster.add(point);
            }
        }
    }

    static List<Point> getNeighbors(Point p, List<Point> points, double eps) {
        List<Point> neighbors = new ArrayList<>();
        for (Point point : points) {
            if (distance(p, point) <= eps) {
                neighbors.add(point);
            }
        }
        return neighbors;
    }

    static boolean isMemberOfAnyCluster(Point point, List<Point> cluster) {
        return cluster.contains(point);
    }

    static double distance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    static List<List<Point>> getTopClusters(List<List<Point>> clusters, int n) {
        clusters.sort(Comparator.comparingInt(List::size));
        Collections.reverse(clusters);
        return clusters.subList(0, Math.min(n, clusters.size()));
    }

    static void writeOutput(List<List<Point>> clusters, String filename) throws IOException {
        int count = 0;
        for (List<Point> cluster : clusters) {
            String outFilename = filename.replace(".txt", "") + "_cluster_" + count++ + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename));
            for (Point point : cluster) {
                writer.write(point.id + "\n");
            }
            writer.close();
        }
    }
}