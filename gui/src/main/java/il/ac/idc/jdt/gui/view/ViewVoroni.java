package il.ac.idc.jdt.gui.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Triangle;
import il.ac.idc.jdt.gui.GraphicUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.util.Iterator;

@SuppressWarnings("serial")
public class ViewVoroni extends ViewBase {

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		drawVoronoi(g);
	}

	/**
	 * Draws Voronoi diagram based on current triangulation A Voronoi diagram
	 * can be created from a Delaunay triangulation by connecting the
	 * circumcenters of neighboring triangles
	 * 
	 * By Udi Schneider
	 * 
	 * @param g
	 *            Graphics object
	 */
	private void drawVoronoi(Graphics g) {
		DelaunayTriangulation dt = getDT();
		GraphicUtils graphicUtils = getGraphUtils();
		Iterator<Triangle> it = dt.trianglesIterator();

		while (it.hasNext()) {
			Triangle curr = it.next();
			Color temp = g.getColor();
			g.setColor(Color.BLACK);

			// For a half plane, only one corner is needed
			if (curr.isHalfplane()) {
				try {
					graphicUtils.drawPolygon(g, dt.calcVoronoiCell(curr, curr.getA()));
				} catch (NullPointerException e) {
				}
			}
			// for a full triangle, check every corner
			else {
				// if a triangle has no neighbors, a null exception will be
				// caught
				// and no action taken.
				// this is expected, for example when there is only one triangle
				// at the start of the user input
				try {
					graphicUtils.drawPolygon(g, dt.calcVoronoiCell(curr, curr.getA()));
				} catch (NullPointerException e) {
				}
				try {
					graphicUtils.drawPolygon(g, dt.calcVoronoiCell(curr, curr.getB()));
				} catch (NullPointerException e) {
				}
				try {
					graphicUtils.drawPolygon(g, dt.calcVoronoiCell(curr, curr.getC()));
				} catch (NullPointerException e) {
				}

				graphicUtils.drawPoint(g, curr.getA(), Color.RED);
				graphicUtils.drawPoint(g, curr.getB(), Color.RED);
				graphicUtils.drawPoint(g, curr.getC(), Color.RED);
			}
			g.setColor(temp);
		}
	}

	@Override
	public Menu getViewMenu() {
		return null;
	}
}
