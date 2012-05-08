package il.ac.idc.jdt.extra.los;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Visibility {

	/**
	 * Calculate the section between p1 and p2. A section contains the list of
	 * triangles between p1 and p2, and the intersection point list of the line
	 * (p1,p2) and those triangles.
	 * 
	 * @param p1
	 * @param p2
	 * @return Section object.
	 */
	public Section computeSection(DelaunayTriangulation dt, Point p1, Point p2) {
		Triangle t1 = dt.find(p1);
		Triangle t2 = dt.find(p2);
		p1 = t1.getZ(p1);
		p2 = t2.getZ(p2);
		List<Triangle> triangles = new ArrayList<Triangle>();
		List<Point> points = new ArrayList<Point>();

		Triangle currTriangle = t1;
		while (currTriangle != t2 && currTriangle != null) {
			triangles.add(currTriangle);
			List<Point> newPoints = cut(p1, p2, currTriangle);
			for (Point p : newPoints) {
				if (!points.contains(p))
					points.add(p);
			}
			currTriangle = nextTriangle(p1, p2, currTriangle, triangles);
		}
		triangles.add(t2);
		return new Section(p1, p2, triangles, points);
	}

	/**
	 * Returns true iff a line of sight exists for a calculated section in the
	 * triangulation. Identical to {@code isVisible(section,0,0);}
	 * 
	 * @param section
	 *            Section of the triangulation, obtained by computeSection()
	 *            method
	 * @return true iff a direct line of sight exists between p1 and p2 of the
	 *         section.
	 */
	public boolean isVisible(Section section) {
		return isVisible(section, 0, 0);
	}

	/**
	 * Returns true iff a line of sight exists for a calculated section in the
	 * triangulation.
	 * 
	 * @param section
	 *            Section of the triangulation, obtained by computeSection()
	 *            method
	 * @param p1TowerHeight
	 *            height of a tower located on p1
	 * @param p2TowerHeight
	 *            height of a tower located on p2
	 * @return true iff a direct line of sight exists between the top of the
	 *         towers.
	 */
	public boolean isVisible(Section section, double p1TowerHeight, double p2TowerHeight) {
		Point p1 = section.getP1();
		Point p2 = section.getP2();
		double z1 = p1.getZ() + p1TowerHeight;
		double z2 = p2.getZ() + p2TowerHeight;
		double dz = z2 - z1;
		double dist = p1.distance(p2);
		for (Point p : section.getPoints()) {
			double d = p1.distance(p);
			if (p.getZ() > z1 + dz * (d / dist))
				return false;
		}
		return true;
	}

	private static Triangle nextTriangle(Point pp1, Point pp2, Triangle curr, Collection<Triangle> tr) {
		Triangle ans = null, t12, t23, t31;
		t12 = curr.getAbTriangle();
		t23 = curr.getBcTriangle();
		t31 = curr.getCaTriangle();
		if (t12 != null && isCut(pp1, pp2, t12) && !tr.contains(t12))
			ans = t12;
		else if (t23 != null && isCut(pp1, pp2, t23) && !tr.contains(t23))
			ans = t23;
		else if (t31 != null && isCut(pp1, pp2, t31) && !tr.contains(t31))
			ans = t31;
		return ans;
	}

	/** return true iff the segment _p1,_p2 is cutting t */
	private static boolean isCut(Point pp1, Point pp2, Triangle t) {
		boolean ans = false;
		if (t.isHalfplane())
			return false;
		Point p1 = t.getA(), p2 = t.getB(), p3 = t.getC();
		int f1 = p1.pointLineTest(pp1, pp2);
		int f2 = p2.pointLineTest(pp1, pp2);
		int f3 = p3.pointLineTest(pp1, pp2);

		if ((f1 == Point.LEFT || f1 == Point.RIGHT) && (f1 == f2 && f1 == f3))
			return false;

		if (f1 != f2 && pp1.pointLineTest(p1, p2) != pp2.pointLineTest(p1, p2))
			return true;
		if (f2 != f3 && pp1.pointLineTest(p2, p3) != pp2.pointLineTest(p2, p3))
			return true;
		if (f3 != f1 && pp1.pointLineTest(p3, p1) != pp2.pointLineTest(p3, p1))
			return true;

		return ans;
	}

	private static List<Point> cut(Point _p1, Point _p2, Triangle t) {
		if (t.isHalfplane())
			return Collections.emptyList();
		List<Point> cuttingPoints = new ArrayList<Point>();

		Point p1 = t.getA(), p2 = t.getB(), p3 = t.getC();
		int f1 = p1.pointLineTest(_p1, _p2);
		int f2 = p2.pointLineTest(_p1, _p2);
		int f3 = p3.pointLineTest(_p1, _p2);

		if ((f1 == Point.LEFT || f1 == Point.RIGHT) && (f1 == f2 && f1 == f3))
			return cuttingPoints;
		if (f1 != f2 && _p1.pointLineTest(p1, p2) != _p2.pointLineTest(p1, p2))
			cuttingPoints.add(intersection(_p1, _p2, p1, p2));
		if (f2 != f3 && _p1.pointLineTest(p2, p3) != _p2.pointLineTest(p2, p3))
			cuttingPoints.add(intersection(_p1, _p2, p2, p3));
		if (f3 != f1 && _p1.pointLineTest(p3, p1) != _p2.pointLineTest(p3, p1))
			cuttingPoints.add(intersection(_p1, _p2, p3, p1));
		return cuttingPoints;
	}

	private static Point intersection(Point _p1, Point _p2, Point q1, Point q2) {
		Point ans = null;
		double x1 = _p1.getX(), x2 = _p2.getX();
		double xx1 = q1.getX(), xx2 = q2.getX();
		double dx = x2 - x1, dxx = xx2 - xx1;
		if (dx == 0 && dxx == 0) {
			ans = q1;
			if (q2.distance(_p1) < q1.distance(_p1))
				ans = q2;
		} else if (dxx == 0) {
			ans = new Point(q1.getX(), f(_p1, _p2, q1.getX()), fz(_p1, _p2, q1.getX()));
		} else if (dx == 0) {
			ans = new Point(_p1.getX(), f(q1, q2, _p1.getX()), fz(q1, q1, _p1.getX()));
		} else {
			double x = (k(_p1, _p2) - k(q1, q2)) / (m(q1, q2) - m(_p1, _p2));
			double y = m(_p1, _p2) * x + k(_p1, _p2);
			double z = mz(q1, q2) * x + kz(q1, q2);
			ans = new Point(x, y, z);
		}
		return ans;
	}

	/** assume z = m*x + k (as a 2D XZ!! linear function) */
	private static double mz(Point p1, Point p2) {
		double ans = 0;
		double dx = p2.getX() - p1.getX(), dz = p2.getZ() - p1.getZ();
		if (dx != 0)
			ans = dz / dx;
		return ans;
	}

	private static double kz(Point p1, Point p2) {
		double k = p1.getZ() - mz(p1, p2) * p1.getX();
		return k;
	}

	private static double f(Point p1, Point p2, double x) {
		return m(p1, p2) * x + k(p1, p2);
	}

	private static double fz(Point p1, Point p2, double x) {
		return mz(p1, p2) * x + kz(p1, p2);
	}

	/** assume y = m*x + k (as a 2D XY !! linear function) */
	private static double m(Point p1, Point p2) {
		double ans = 0;
		double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
		if (dx != 0)
			ans = dy / dx;
		return ans;
	}

	private static double k(Point p1, Point p2) {
		double k = p1.getY() - m(p1, p2) * p1.getX();
		return k;
	}

}
