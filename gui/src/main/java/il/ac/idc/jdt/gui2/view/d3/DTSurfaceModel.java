package il.ac.idc.jdt.gui2.view.d3;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.surface.AbstractSurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceVertex;

public class DTSurfaceModel extends AbstractSurfaceModel implements SurfaceModel {

	private Mapper mapper = new Mapper() {

		@Override
		public float f2(float x, float y) {
			return 0;
		}

		@Override
		public float f1(float x, float y) {
			return 0;
		}
	};
	protected SurfaceVertex[][] surfaceVertex;
	private DelaunayTriangulation dt;

	private List<Triangle> triangles = new ArrayList<Triangle>();

	public List<Triangle> getTriangles() {
		return Collections.unmodifiableList(triangles);
	}

	/**
	 * Empty Surface Model
	 */
	public DTSurfaceModel() {
		super();
	}

	final public PlotType getPlotType() {
		return PlotType.SURFACE;
	}

	public SwingWorker<Void, Void> plot() {
		if ((xMin >= xMax) || (yMin >= yMax))
			throw new NumberFormatException();
		setDataAvailable(false); // clean space
		// reads the calcDivision that will be used
		final float stepx = (xMax - xMin) / calcDivisions;
		final float stepy = (yMax - yMin) / calcDivisions;
		final float xfactor = 20 / (xMax - xMin); // 20 aint magic: surface
													// vertex requires a value
													// in [-10 ; 10]
		final float yfactor = 20 / (yMax - yMin);

		// final int total = dt.size();
		if (dt == null) {
			dt = new DelaunayTriangulation();
		}

		// This part is required due to original implementation of the JSurface
		// component. DT uses the only the triangles.
		final int total = (calcDivisions + 1) * (calcDivisions + 1); //

		surfaceVertex = allocateMemory(hasFunction1, hasFunction2, total); // allocate
																			// surfaceVertex

		// just put any value in surfaceVertex, in order to JSurface to display
		// something.
		for (int i = 0; i <= calcDivisions; i++)
			for (int j = 0; j <= calcDivisions; j++) {
				int k = i * (calcDivisions + 1) + j;
				float x = xMin + i * stepx;
				float y = yMin + j * stepy;
				surfaceVertex[0][k] = new SurfaceVertex((x - xMin) * xfactor - 10, (y - yMin) * yfactor - 10, 0);
			}

		setSurfaceVertex(surfaceVertex); // define as the current surfaceVertex
		setDataAvailable(true); // cause the JSurface to display an empty plot

		getProjector();

		return new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				setProgress(0);
				setProgress(1);
				List<Triangle> triangles = dt.getTriangulation();
				DTSurfaceModel.this.triangles.clear();
				int count = 0;
				for (Triangle t : triangles) {
					count++;
					Triangle newT;
					Point a = t.getA();
					Point b = t.getB();
					Point c = t.getC();
					if (a.getZ() > z1Max) {
						z1Max = (float) a.getZ();
					}
					if (b.getZ() > z1Max) {
						z1Max = (float) a.getZ();
					}
					if (c != null && c.getZ() > z1Max) {
						z1Max = (float) a.getZ();
					}
					if (a.getZ() < z1Min) {
						z1Min = (float) a.getZ();
					}
					if (b.getZ() < z1Min) {
						z1Min = (float) a.getZ();
					}
					if (c != null && c.getZ() < z1Min) {
						z1Min = (float) a.getZ();
					}
					Point newA = new Point((a.getX() - xMin) * xfactor - 10, (a.getY() - yMin) * yfactor - 10, a.getZ());
					Point newB = new Point((b.getX() - xMin) * xfactor - 10, (b.getY() - yMin) * yfactor - 10, b.getZ());
					if (c != null) {
						Point newC = new Point((c.getX() - xMin) * xfactor - 10, (c.getY() - yMin) * yfactor - 10,
								c.getZ());
						newT = new Triangle(newA, newB, newC);
					} else {
						newT = new Triangle(newA, newB);
					}
					DTSurfaceModel.this.triangles.add(newT);
					setProgress((int) ((float) 100 * count) / triangles.size());
					publish();
				}

				setProgress(100);
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				z1Min = (float) floor(z1Min, 2);
				z1Max = (float) ceil(z1Max, 2);

				autoScale();
				fireStateChanged();
			}

			@Override
			protected void process(List<Void> chunks) {
				fireStateChanged();
			}

		};

	}

	/**
	 * Allocates Memory
	 */

	private SurfaceVertex[][] allocateMemory(boolean f1, boolean f2, int total) {
		SurfaceVertex[][] vertex = null;
		try {
			vertex = new SurfaceVertex[2][total];
			if (!f1)
				vertex[0] = null;
			if (!f2)
				vertex[1] = null;
		} catch (OutOfMemoryError e) {
			setMessage("Not enough memory");
		} catch (Exception e) {
			setMessage("Error: " + e.toString());
		}
		return vertex;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		getPropertyChangeSupport().firePropertyChange("mapper", this.mapper, this.mapper = mapper);
	}

	public void setDT(DelaunayTriangulation dt) {
		getPropertyChangeSupport().firePropertyChange("dt", this.dt, this.dt = dt);
	}

	public SurfaceVertex[][] getSurfaceVertex() {
		return surfaceVertex;
	}

	protected void setSurfaceVertex(SurfaceVertex[][] surfaceVertex) {
		getPropertyChangeSupport().firePropertyChange("surfaceVertex", this.surfaceVertex,
				this.surfaceVertex = surfaceVertex);
	}
}
