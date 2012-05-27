package il.ac.idc.jdt.gui2;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class GraphicUtils {

	private Component c;
	private DelaunayTriangulation dt;

	public GraphicUtils(Component c, DelaunayTriangulation dt) {
		this.dt = dt;
		this.c = c;
	}

	public void drawLine(Graphics g, Point p1, Point p2) {
		drawLine(g, p1, p2, null);
	}

	public void drawLine(Graphics g, Point p1, Point p2, Color color) {
		if (color != null) {
			g.setColor(color);
		}
		java.awt.Point gp1 = scale(p1);
		java.awt.Point gp2 = scale(p2);

		g.drawLine(gp1.x, gp1.y, gp2.x, gp2.y);
	}

	public Point descale(int x, int y) {
		if (dt == null)
			throw new IllegalStateException("Cannot scale without DT");
		double minX;
		double maxX;
		double minY;
		double maxY;
		if (dt.size() < 3) {
			return new Point(x, y);
		}
		minX = dt.getBoundingBox().minX();
		minY = dt.getBoundingBox().minY();
		maxX = dt.getBoundingBox().maxX();
		maxY = dt.getBoundingBox().maxY();

		return new Point(minX + ((double) x / (double) c.getWidth() * (maxX - minX)), minY
				+ ((double) y / (double) c.getHeight() * (maxY - minY)));
	}

	private java.awt.Point scale(Point p) {
		if (dt == null)
			throw new IllegalStateException("Cannot scale without DT");
		double minX = dt.getBoundingBox().minX();
		double minY = dt.getBoundingBox().minY();
		double maxX = dt.getBoundingBox().maxX();
		double maxY = dt.getBoundingBox().maxY();

		return new java.awt.Point((int) (((p.getX() - minX) / (maxX - minX)) * (double) c.getWidth()),
				(int) ((p.getY() - minY) / (maxY - minY) * (double) c.getHeight()));

	}

	public void drawPoint(Graphics g, Point p1, Color cl) {
		drawPoint(g, p1, 4, cl);
	}

	public void drawPoint(Graphics g, Point p1, int r, Color cl) {
		java.awt.Point rp = scale(p1);
		g.setColor(cl);
		g.fillOval(rp.x - r / 2, rp.y - r / 2, r, r);
	}

	public void drawTriangle(Graphics g, Triangle t, Color cl) {
		if (t.isHalfplane()) {
			if (cl == null)
				cl = Color.BLUE;
			drawLine(g, t.getA(), t.getB(), cl);
		} else {
			if (cl == null)
				cl = Color.BLACK;
			drawLine(g, t.getA(), t.getB(), cl);
			drawLine(g, t.getB(), t.getC(), cl);
			drawLine(g, t.getC(), t.getA(), cl);
		}
	}

	public void drawFilledTriangle(Graphics g, Color cl, Triangle t) {
		g.setColor(cl);
		int[] xx = new int[3], yy = new int[3];
		// double f = 0;
		// double dx_map = _dx_map.getY()- _dx_map.getX();
		// double dy_map = _dy_map.getY()- _dy_map.getX();

		// f = (t.getA().getX() -_dx_map.getX())/dx_map;
		java.awt.Point p1 = scale(t.getA());
		// Point p1 = world2screen(t.getA());
		xx[0] = (int) p1.x;
		yy[0] = (int) p1.y;
		// Point p2 = world2screen(t.getB());
		java.awt.Point p2 = scale(t.getB());
		xx[1] = (int) p2.x;
		yy[1] = (int) p2.y;
		java.awt.Point p3 = scale(t.getC());
		// Point p3 = world2screen(t.getC());
		xx[2] = (int) p3.x;
		yy[2] = (int) p3.y;

		g.fillPolygon(xx, yy, 3);
	}

	public void drawPolygon(Graphics g, Point[] polygon) {
		int skip = 0;
		int j = 0;
		int[] x = new int[polygon.length];
		int[] y = new int[polygon.length];
		for (int i = 0; i < polygon.length; i++) {
			if (Double.isInfinite(polygon[i].getX()) || Double.isInfinite(polygon[i].getY())) {
				skip++;
				continue;
			}

			java.awt.Point p = scale(polygon[i]);
			// polygon[i] = this.world2screen(polygon[i]);

			x[j] = p.x;
			y[j] = p.y;
			j++;
		}
		g.drawPolygon(x, y, polygon.length - skip);
	}
}
