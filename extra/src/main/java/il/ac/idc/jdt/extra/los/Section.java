package il.ac.idc.jdt.extra.los;

import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class Section implements Serializable {
	public Section(Point p1, Point p2, List<Triangle> triangles, List<Point> points) {
		this.p1 = p1;
		this.p2 = p2;
		this.triangles = Collections.unmodifiableList(triangles);
		this.points = Collections.unmodifiableList(points);
	}

	private Point p1;
	private Point p2;
	private List<Triangle> triangles;
	private List<Point> points;

	public List<Triangle> getTriangles() {
		return triangles;
	}

	public List<Point> getPoints() {
		return points;
	}

	public Point getP1() {
		return p1;
	}

	public Point getP2() {
		return p2;
	}

}
