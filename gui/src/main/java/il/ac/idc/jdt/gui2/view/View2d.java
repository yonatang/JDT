package il.ac.idc.jdt.gui2.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Triangle;
import il.ac.idc.jdt.gui2.GraphicUtils;

import java.awt.Graphics;
import java.awt.Menu;
import java.util.Iterator;

@SuppressWarnings("serial")
public class View2d extends ViewBase {

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		DelaunayTriangulation dt = getDT();
		GraphicUtils graphicUtils = getGraphUtils();
		if (dt == null || dt.size() == 0)
			return;
		Triangle curr = null;
		Iterator<Triangle> it = dt.trianglesIterator();
		while (it.hasNext()) {
			curr = it.next();
			graphicUtils.drawTriangle(g, curr, null);
		}

	}

	@Override
	public Menu getViewMenu() {
		return null;
	}

}
