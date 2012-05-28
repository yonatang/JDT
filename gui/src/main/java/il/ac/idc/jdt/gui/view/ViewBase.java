package il.ac.idc.jdt.gui.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.gui.GraphicUtils;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public abstract class ViewBase extends Component implements View {
	private DelaunayTriangulation dt = new DelaunayTriangulation();
	private GraphicUtils graph = new GraphicUtils(this, dt);

	private static MouseHandler mh;

	@Override
	public void setDT(DelaunayTriangulation dt) {
		this.dt = dt;
		graph = new GraphicUtils(this, dt);
		if (mh == null) {
			mh = new MouseHandler(dt, this);
		} else {
			mh.dt = dt;
			mh.comp = this;
		}
		addMouseListener(mh);
		validate();
	}

	private class MouseHandler extends MouseAdapter {
		DelaunayTriangulation dt;

		Component comp;

		public MouseHandler(DelaunayTriangulation dt, Component comp) {
			this.dt = dt;
			this.comp = comp;
		}

		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			dt.insertPoint(graph.descale(x, y));
			comp.repaint();
		}
	}

	GraphicUtils getGraphUtils() {
		return graph;
	}

	DelaunayTriangulation getDT() {
		return dt;
	}
}
