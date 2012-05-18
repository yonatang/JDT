package il.ac.idc.jdt.gui3d2;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.ericaro.surfaceplotter.DefaultSurfaceModel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.surface.JSurface;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;

public class MainFrame extends Frame {

	private DelaunayTriangulation dt;

	private JSurface surface;

	public MainFrame(DelaunayTriangulation dt) {
		setTitle("Title");
		setSize(300, 300);
		this.dt = dt;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		surface = new JSurface();
		init();
		add(surface);
	}

	private void init() {
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
		sm.setMapper(new Mapper() {
			public float f1(float x, float y) {
				double newX = (x + 3) / 6 * (dt.getBoundingBox().maxX() - dt.getBoundingBox().minX())
						+ dt.getBoundingBox().minX();
				double newY = (y + 3) / 6 * (dt.getBoundingBox().maxY() - dt.getBoundingBox().minY())
						+ dt.getBoundingBox().minY();

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

	public void start() {
		setVisible(true);
	}

}
