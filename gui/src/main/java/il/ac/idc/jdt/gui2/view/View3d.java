package il.ac.idc.jdt.gui2.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.awt.BorderLayout;
import java.awt.Panel;

import net.ericaro.surfaceplotter.DefaultSurfaceModel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.surface.JSurface;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;

@SuppressWarnings("serial")
public class View3d extends Panel {

	private JSurface surface = new JSurface();
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
		DefaultSurfaceModel sm = new DefaultSurfaceModel();
		sm.setPlotFunction2(false);

		sm.setCalcDivisions(70);
		sm.setDispDivisions(70);
		sm.setContourLines(50);

		sm.setXMin(-3);
		sm.setXMax(3);
		sm.setYMin(-3);
		sm.setYMax(3);

		sm.setBoxed(false);
		sm.setDisplayXY(false);
		sm.setExpectDelay(false);
		sm.setAutoScaleZ(true);
		sm.setDisplayZ(false);
		sm.setMesh(true);
		sm.setPlotType(PlotType.SURFACE);
		sm.setFirstFunctionOnly(true);
		// sm.setPlotType(PlotType.WIREFRAME);
		// sm.setPlotType(PlotType.CONTOUR);
		// sm.setPlotType(PlotType.DENSITY);

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

		sm.setMapper(new Mapper() {
			public float f1(float x, float y) {
				if (dt == null || dt.getBoundingBox() == null)
					return 0;
				double newX = x;
				double newY = y;

				float r;
				Point p = new Point(newX, newY);
				Triangle t = dt.find(p);
				if (t.isHalfplane()) {
					r = 0;
				} else {
					r = (float) t.zValue(p);
				}
				return r;
			}

			public float f2(float x, float y) {
				return 0;
			}
		});
		sm.plot().execute();
		surface.setModel(sm);
	}
}
