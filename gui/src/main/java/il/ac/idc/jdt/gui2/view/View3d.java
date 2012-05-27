package il.ac.idc.jdt.gui2.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.gui2.view.d3.DTJSurface;
import il.ac.idc.jdt.gui2.view.d3.DTSurfaceModel;

import java.awt.BorderLayout;
import java.awt.Panel;

import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;

@SuppressWarnings("serial")
public class View3d extends Panel implements View {

	private DTJSurface surface = new DTJSurface();
	private DelaunayTriangulation dt;

	public View3d() {
		setLayout(new BorderLayout());
		initSurface();
		add(surface, BorderLayout.CENTER);
	}

	public void setDT(DelaunayTriangulation dt) {
		this.dt = dt;
		initSurface();
	}

	private void initSurface() {
		DTSurfaceModel sm = new DTSurfaceModel();
		sm.setPlotFunction2(false);

		sm.setCalcDivisions(70);
		sm.setDispDivisions(70);
		sm.setContourLines(50);

		sm.setXMin(-3);
		sm.setXMax(3);
		sm.setYMin(-3);
		sm.setYMax(3);

		sm.setBoxed(true);
		sm.setDisplayXY(false);
		sm.setExpectDelay(false);
		sm.setAutoScaleZ(true);
		sm.setDisplayZ(true);
		sm.setMesh(false);
		sm.setPlotType(PlotType.SURFACE);
		sm.setFirstFunctionOnly(true);

		sm.setPlotColor(PlotColor.SPECTRUM);
		// sm.setPlotColor(PlotColor.DUALSHADE);
		// sm.setPlotColor(PlotColor.FOG);
		// sm.setPlotColor(PlotColor.OPAQUE);
		if (dt != null && dt.getBoundingBox() != null) {
			sm.setXMin((float) dt.getBoundingBox().minX());
			sm.setXMax((float) dt.getBoundingBox().maxX());
			sm.setYMin((float) dt.getBoundingBox().minY());
			sm.setYMax((float) dt.getBoundingBox().maxY());
		}
		sm.setDT(dt);
		sm.plot().execute();
		surface.setModel(sm);
	}
}
