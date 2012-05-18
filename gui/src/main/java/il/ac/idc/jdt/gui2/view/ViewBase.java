package il.ac.idc.jdt.gui2.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.gui2.GraphicUtils;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public abstract class ViewBase extends Component implements View {
	private DelaunayTriangulation dt = new DelaunayTriangulation();
	private GraphicUtils graph = new GraphicUtils(this, dt);

	@Override
	public void setDT(DelaunayTriangulation dt) {
		this.dt = dt;
		graph = new GraphicUtils(this, dt);
		addMouseListener(new MouseHandler());
		validate();
	}

	private class MouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			dt.insertPoint(graph.descale(x, y));
			repaint();
		}
	}

	GraphicUtils getGraphUtils() {
		return graph;
	}

	DelaunayTriangulation getDT() {
		return dt;
	}
}
