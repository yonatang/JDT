package il.ac.idc.jdt.gui;

import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

/**
 * This class is responsible for drawing a triangulation. NOTE: Most of this
 * code is taken from MyFrame.java.
 * 
 * @author NS
 * 
 */
public class TriangulationDrawer {

	private Point _dx_f, _dy_f, _dx_map, _dy_map;
	private Point m_boundingBoxMin, m_boundingBoxMax;

	/**
	 * Constructor.
	 */
	public TriangulationDrawer(int width, int height) {
		int startX = width / 4;
		int startY = height / 8;
		_dx_f = new Point(startX, width - startX + 30);
		_dy_f = new Point(startY, height);
	}

	/**
	 * Draws the triangulation.
	 * 
	 * @param g
	 *            The Graphics object used to draw the triangulation on.
	 * @param triangulation
	 *            A Vector objects that contains multiple Triangle objects that
	 *            represent the triangulation.
	 */
	public void drawTriangulation(Graphics g, Vector<Triangle> triangulation) {
		setBoundingBox(triangulation);

		_dx_map = new Point(m_boundingBoxMax.getX(), m_boundingBoxMin.getX());
		_dy_map = new Point(m_boundingBoxMax.getY(), m_boundingBoxMin.getY());

		for (Integer i = 0; i < triangulation.size(); i++) {
			Triangle currentTriangle = triangulation.get(i);
			drawTriangle(g, currentTriangle, null);
		}
	}

	/**
	 * Sets the bounding box.
	 * 
	 * @param triangulation
	 *            The Vector of Triangle objects that represents the
	 *            triangulation.
	 */
	private void setBoundingBox(Vector<Triangle> triangulation) {
		for (Integer i = 0; i < triangulation.size(); i++) {
			Triangle currentTriangle = triangulation.get(i);
			Point p1 = currentTriangle.getA();
			Point p2 = currentTriangle.getB();
			Point p3 = currentTriangle.getC();

			updateBoundingBox(p1);
			updateBoundingBox(p2);
			updateBoundingBox(p3);
		}
	}

	/**
	 * Updates the bounding box.
	 * 
	 * @param p
	 *            The current box to compare to the bounding box.
	 */
	private void updateBoundingBox(Point p) {
		double x = p.getX(), y = p.getY(), z = p.getZ();
		if (m_boundingBoxMin == null) {
			m_boundingBoxMin = new Point(p);
			m_boundingBoxMax = new Point(p);
		} else {
			if (x < m_boundingBoxMin.getX())
				m_boundingBoxMin.setX(x);
			else if (x > m_boundingBoxMax.getX())
				m_boundingBoxMax.setX(x);
			if (y < m_boundingBoxMin.getY())
				m_boundingBoxMin.setY(y);
			else if (y > m_boundingBoxMax.getY())
				m_boundingBoxMax.setY(y);
			if (z < m_boundingBoxMin.getZ())
				m_boundingBoxMin.setZ(z);
			else if (z > m_boundingBoxMax.getZ())
				m_boundingBoxMax.setZ(z);
		}
	}

	/**
	 * Draws a triangle.
	 * 
	 * @param g
	 *            The Graphics object used to draw the triangulation on.
	 * @param t
	 *            The current Triangle object to draw
	 * @param c
	 *            The color of the triangle NOTE: Taken from MyFrame.java.
	 */
	private void drawTriangle(Graphics g, Triangle t, Color c) {
		if (c != null)
			g.setColor(c);
		if (t.isHalfplane()) {
			if (c == null)
				g.setColor(Color.blue);
			drawLine(g, t.getA(), t.getB());
		} else {
			if (c == null)
				g.setColor(Color.black);
			drawLine(g, t.getA(), t.getB());
			drawLine(g, t.getB(), t.getC());
			drawLine(g, t.getC(), t.getA());
		}
	}

	/**
	 * Draws a line.
	 * 
	 * @param g
	 *            The Graphics object used to draw the triangulation on.
	 * @param p1
	 *            The first point on the line.
	 * @param p2
	 *            The second point on the line. NOTE: Taken from MyFrame.java.
	 */
	private void drawLine(Graphics g, Point p1, Point p2) {
		Point t1 = this.world2screen(p1);
		Point t2 = this.world2screen(p2);
		g.drawLine((int) t1.getX(), (int) t1.getY(), (int) t2.getX(), (int) t2.getY());
	}

	/**
	 * Taken from MyFrame.java.
	 */
	private Point world2screen(Point p) {
		double x = transform(_dx_map, p.getX(), _dx_f);
		double y = transformY(_dy_map, p.getY(), _dy_f);
		return new Point(x, y);
	}

	/**
	 * Transforms the point p from the Rectangle th into this Rectangle. NOTE:
	 * r.contains(p) must be true! assume p.x
	 * < p
	 * .y. NOTE: Taken from MyFrame.java.
	 **/
	private double transform(Point range, double x, Point new_range) {
		double dx1 = range.getY() - range.getX();
		double dx2 = new_range.getY() - new_range.getX();

		double scale = (x - range.getX()) / dx1;
		double ans = new_range.getX() + dx2 * scale;
		return ans;
	}

	/**
	 * Transform the point p from the Rectangle into this Rectangle. NOTE: flips
	 * the Y coordination for frame!. NOTE: r.contains(p) must be true! assume
	 * p.x
	 * < p
	 * .y. NOTE: Taken from MyFrame.java.
	 * */
	private double transformY(Point range, double x, il.ac.idc.jdt.Point new_range) {
		double dy1 = range.getY() - range.getX();
		double dy2 = new_range.getY() - new_range.getX();

		double scale = (x - range.getX()) / dy1;
		double ans = new_range.getY() - dy2 * scale;
		return ans;
	}
}