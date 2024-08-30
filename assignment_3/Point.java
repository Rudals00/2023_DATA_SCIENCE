package clustering;

import java.util.ArrayList;
import java.util.List;

public class Point {
    public String id;
    public double x, y;
    public boolean visited = false;
    public List<Point> neighbors = new ArrayList<>();
    public Point(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}