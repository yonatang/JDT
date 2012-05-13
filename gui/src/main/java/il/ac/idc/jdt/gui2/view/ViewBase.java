package il.ac.idc.jdt.gui2.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.gui2.GraphicUtils;

import java.awt.Component;

@SuppressWarnings("serial")
public abstract class ViewBase extends Component {
	private DelaunayTriangulation dt = new DelaunayTriangulation();
	private GraphicUtils graph = new GraphicUtils(this, dt);

	public void setDT(DelaunayTriangulation dt) {
		this.dt = dt;
		graph = new GraphicUtils(this, dt);
		validate();
	}

	GraphicUtils getGraphUtils() {
		return graph;
	}

	DelaunayTriangulation getDT() {
		return dt;
	}
}
