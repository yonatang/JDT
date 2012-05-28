package il.ac.idc.jdt.gui.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;
import il.ac.idc.jdt.gui.GraphicUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.util.Iterator;

@SuppressWarnings("serial")
public class ViewTopology extends ViewBase {

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		DelaunayTriangulation dt = getDT();

		Iterator<Triangle> it = dt.trianglesIterator();
		g.setColor(Color.red);
		while (it.hasNext()) {
			Triangle curr = it.next();
			if (!curr.isHalfplane()) {
				drawFilledTriangle(g, curr);
				drawTriangleTopoLines(g, curr, 100);

			}
		}
	}

	private void drawFilledTriangle(Graphics g, Triangle t) {
		DelaunayTriangulation dt = getDT();
		GraphicUtils graphicUtils = getGraphUtils();
		double maxZ = dt.maxBoundingBox().getZ();
		double minZ = dt.minBoundingBox().getZ();
		double z = (t.getA().getZ() + t.getB().getZ() + t.getC().getZ()) / 3.0;
		double dz = maxZ - minZ;
		int co = 30 + (int) (220 * ((z - minZ) / dz));
		Color cl = new Color(co, co, co);
		graphicUtils.drawFilledTriangle(g, cl, t);
	}

	private Point[] computePoints(Point p1, Point p2, double dz) {
		Point[] ans = new Point[0];
		double z1 = Math.min(p1.getZ(), p2.getZ()), z2 = Math.max(p1.getZ(), p2.getZ());
		if (z1 == z2)
			return ans;
		double zz1 = ((int) (z1 / dz)) * dz;
		if (zz1 < z1)
			zz1 += dz;
		double zz2 = ((int) (z2 / dz)) * dz;
		int len = (int) ((zz2 - zz1) / dz) + 1, i = 0;
		ans = new Point[len];
		double DZ = p2.getZ() - p1.getZ(), DX = p2.getX() - p1.getX(), DY = p2.getY() - p1.getY();
		for (double z = zz1; z <= zz2; z += dz) {
			double scale = (z - p1.getZ()) / DZ;
			double x = p1.getX() + DX * scale;
			double y = p1.getY() + DY * scale;
			ans[i] = new Point(x, y, z);
			i++;
		}
		return ans;
	}

	private void drawTriangleTopoLines(Graphics g, Triangle t, double dz) {
		if (t.getA().getZ() < 0 || t.getB().getZ() < 0 || t.getC().getZ() < 0)
			return;
		GraphicUtils graphicUtils = getGraphUtils();
		Point[] p12 = computePoints(t.getA(), t.getB(), dz);
		Point[] p23 = computePoints(t.getB(), t.getC(), dz);
		Point[] p31 = computePoints(t.getC(), t.getA(), dz);

		int i12 = 0, i23 = 0, i31 = 0;
		boolean cont = true;
		while (cont) {
			cont = false;
			if (i12 < p12.length && i23 < p23.length && p12[i12].getZ() == p23[i23].getZ()) {
				g.setColor(Color.YELLOW);
				if (p12[i12].getZ() % 200 > 100)
					g.setColor(Color.red);
				graphicUtils.drawLine(g, p12[i12], p23[i23]);
				i12++;
				i23++;
				cont = true;
			}
			if (i23 < p23.length && i31 < p31.length && p23[i23].getZ() == p31[i31].getZ()) {
				g.setColor(Color.YELLOW);
				if (p23[i23].getZ() % 200 > 100)
					g.setColor(Color.red);
				graphicUtils.drawLine(g, p23[i23], p31[i31]);
				i23++;
				i31++;
				cont = true;
			}
			if (i12 < p12.length && i31 < p31.length && p12[i12].getZ() == p31[i31].getZ()) {
				g.setColor(Color.YELLOW);
				if (p12[i12].getZ() % 200 > 100)
					g.setColor(Color.red);
				graphicUtils.drawLine(g, p12[i12], p31[i31]);
				i12++;
				i31++;
				cont = true;
			}
		}
	}

	@Override
	public Menu getViewMenu() {
		return null;
	}

}
