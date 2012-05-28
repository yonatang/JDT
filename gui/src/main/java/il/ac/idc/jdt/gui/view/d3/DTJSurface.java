package il.ac.idc.jdt.gui.view.d3;

/*----------------------------------------------------------------------------------------*
 * JSurface.java                                                                     *
 *                                                                                        *
 * Surface Plotter   version 1.10    14 Oct 1996                                          *
 *                   version 1.20     8 Nov 1996                                          *
 *                   version 1.30b1  17 May 1997                                          *
 *                   version 1.30b2  18 Oct 2001                                          *
 *                                                                                        *
 * Copyright (c) Yanto Suryono <yanto@fedu.uec.ac.jp>                                     *
 *                                                                                        *
 * This program is free software; you can redistribute it and/or modify it                *
 * under the terms of the GNU Lesser General Public License as published by the                  *
 * Free Software Foundation; either version 2 of the License, or (at your option)         *
 * any later version.                                                                     *
 *                                                                                        *
 * This program is distributed in the hope that it will be useful, but                    *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or          *
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for               *
 * more details.                                                                          *
 *                                                                                        *
 * You should have received a copy of the GNU Lesser General Public License along                *
 * with this program; if not, write to the Free Software Foundation, Inc.,                *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA                                  *
 *       
 eric : Modified to be swing compliant:                                                       *
 : using default swing doubleBuffering for interactive painting (drag etc.)
 : and VolativeBuffering for rotation process
 : and use a SurfaceModel interface to drive it.
 *----------------------------------------------------------------------------------------*/

import il.ac.idc.jdt.Triangle;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Point;
import java.awt.PrintGraphics;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.ericaro.surfaceplotter.surface.Projector;
import net.ericaro.surfaceplotter.surface.SurfaceColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceVertex;

import org.apache.batik.svggen.SVGGraphics2D;

/**
 * The class <code>JSurface</code> is responsible for the generation of surface
 * images and user mouse events handling. It relies on a SurfaceModel that
 * handles everything. This class only display data available in the model.
 * 
 * @author Yanto Suryono
 */

public class DTJSurface extends javax.swing.JComponent {
	private DTSurfaceModel model; // the parent, Surface Plotter model
	private Projector projector; // the projector, controls the point of view
	// private SurfaceVertex[][] surfaceVertex; // vertices array
	private boolean data_available; // data availability flag
	private boolean interrupted; // interrupted flag
	private boolean printing; // printing flag
	private int prevwidth, prevheight; // canvas size
	private int printwidth, printheight; // print size
	private SurfaceVertex cop; // center of projection

	private List<Triangle> triangles;

	private int curve = 0;

	private Graphics graphics; // the actual graphics used by all private
								// methods

	// setting variables

	private boolean plotfunc1, plotfunc2;
	private boolean isBoxed, isMesh, isScaleBox, isDisplayXY, isDisplayZ, isDisplayGrids;
	private float xmin, xmax, ymin;
	private float ymax, zmin, zmax;

	private String xLabel = "X";
	private String yLabel = "Y";

	// constants
	private static final int TOP = 0;
	private static final int CENTER = 1;

	// for splitting polygons

	private static final int UPPER = 1;
	private static final int COINCIDE = 0;
	private static final int LOWER = -1;

	SurfaceColor colors;
	private JSurfaceChangesListener surfaceChangesListener;
	private static DTJSurface lastFocused;

	// TODO makes the JSurface works without model, so that it can become a real
	// bean
	public DTJSurface() {
		this(new DTSurfaceModel());
	}

	/**
	 * The constructor of <code>JSurface</code>
	 * 
	 * @see SurfaceFrame
	 */

	public DTJSurface(DTSurfaceModel model) {
		super();
		surfaceChangesListener = new JSurfaceChangesListener();
		JSurfaceMouseListener my = new JSurfaceMouseListener();
		addMouseListener(my);
		addMouseMotionListener(my);
		addMouseWheelListener(my);
		addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				// keep track of the last focused Jsurface to connect actions to
				// them
				lastFocused = DTJSurface.this;
			}
		});
		setModel(model);
	}

	/**
	 * Return the last focused JSurface component. Useful for actions to apply
	 * on it.
	 * 
	 * @return
	 */
	public static DTJSurface getFocusedComponent() {
		return lastFocused;
	}

	public SurfaceModel getModel() {
		return model;
	}

	public void setModel(DTSurfaceModel model) {

		if (this.model != null)
			model.removePropertyChangeListener(surfaceChangesListener);
		if (this.model != null)
			model.removeChangeListener(surfaceChangesListener);

		if (model == null)
			model = new DTSurfaceModel();

		this.model = model;
		interrupted = false;
		data_available = false;
		printing = false;
		// contour = density = false;
		prevwidth = prevheight = -1;
		projector = model.getProjector();
		// surfaceVertex = new SurfaceVertex[2][];
		triangles = model.getTriangles();

		model.addPropertyChangeListener(surfaceChangesListener);
		model.addChangeListener(surfaceChangesListener);
		init(); // fill all availables properties
	}

	class JSurfaceMouseListener extends MouseAdapter implements MouseMotionListener, MouseWheelListener {

		int i = 0;

		public void mouseWheelMoved(MouseWheelEvent e) {
			float new_value = 0.0f;
			float old_value = projector.get2DScaling();
			new_value = old_value * (1 - e.getScrollAmount() * e.getWheelRotation() / 10f);
			if (new_value > 60.0f)
				new_value = 60.0f;
			if (new_value < 2.0f)
				new_value = 2.0f;
			if (new_value != old_value) {
				projector.set2DScaling(new_value);
				repaint();
			}
		}

		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();

			click_x = x;
			click_y = y;
		}

		/**
		 * <code>mouseUp<code> event handler. Regenerates image if dragging operations 
		 * have been done with the delay regeneration flag set on.
		 * 
		 * @param e
		 *            the event
		 * @param x
		 *            the x coordinate of cursor
		 * @param y
		 *            the y coordinate of cursor
		 */

		public void mouseReleased(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();

			if (!is3D())
				return;
			if (model.isExpectDelay() && dragged) {
				destroyImage();
				data_available = is_data_available;
				repaint();
				dragged = false;
			}

		}

		/**
		 * <code>mouseDrag<code> event handler. Tracks dragging operations. 
		 * Checks the delay regeneration flag and does proper actions.
		 * 
		 * @param e
		 *            the event
		 * @param x
		 *            the x coordinate of cursor
		 * @param y
		 *            the y coordinate of cursor
		 */
		public void mouseMoved(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			// System.out.println("dragged"+x+","+y);

			float new_value = 0.0f;

			if (!is3D())
				return;
			// if (!thread.isAlive() || !data_available) {
			if (e.isControlDown()) {
				projector.set2D_xTranslation(projector.get2D_xTranslation() + (x - click_x));
				projector.set2D_yTranslation(projector.get2D_yTranslation() + (y - click_y));
			} else if (e.isShiftDown()) {
				new_value = projector.get2DScaling() + (y - click_y) * 0.5f;
				if (new_value > 60.0f)
					new_value = 60.0f;
				if (new_value < 2.0f)
					new_value = 2.0f;
				projector.set2DScaling(new_value);
			} else {
				new_value = projector.getRotationAngle() + (x - click_x);
				while (new_value > 360)
					new_value -= 360;
				while (new_value < 0)
					new_value += 360;
				projector.setRotationAngle(new_value);
				new_value = projector.getElevationAngle() + (y - click_y);
				if (new_value > 90)
					new_value = 90;
				else if (new_value < 0)
					new_value = 0;
				projector.setElevationAngle(new_value);
			}
			if (!model.isExpectDelay()) {
				repaint();
			} else {
				if (!dragged) {
					is_data_available = data_available;
					dragged = true;
				}
				data_available = false;
				repaint();
			}

			click_x = x;
			click_y = y;
		}

	}

	class JSurfaceChangesListener implements PropertyChangeListener, javax.swing.event.ChangeListener {
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			destroyImage();
		}

		public void propertyChange(java.beans.PropertyChangeEvent pe) {
			init();
			destroyImage();
		}
	}

	private String format(float f) {
		return String.format("%.3G", f);
	}

	private void init() {
		colors = model.getColorModel();
		setRanges(model.getXMin(), model.getXMax(), model.getYMin(), model.getYMax());

		data_available = model.isDataAvailable();
		if (data_available)
			setValuesArray(model.getSurfaceVertex());

		isBoxed = model.isBoxed();
		isMesh = model.isMesh();
		isScaleBox = model.isScaleBox();
		isDisplayXY = model.isDisplayXY();
		isDisplayZ = model.isDisplayZ();
		isDisplayGrids = model.isDisplayGrids();
		plotfunc1 = model.isPlotFunction1();
		plotfunc2 = model.isPlotFunction2();
	}

	/**
	 * Destroys the internal image. It will force <code>SurfaceCanvas</code> to
	 * regenerate all images when the <code>paint</code> method is called.
	 */

	public void destroyImage() {
		repaint();
	}

	/**
	 * Sets the x and y ranges of calculated surface vertices. The ranges will
	 * not affect surface appearance. They affect axes scale appearance.
	 * 
	 * @param xmin
	 *            the minimum x
	 * @param xmax
	 *            the maximum x
	 * @param ymin
	 *            the minimum y
	 * @param ymax
	 *            the maximum y
	 */

	public void setRanges(float xmin, float xmax, float ymin, float ymax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}

	/**
	 * Gets the current x, y, and z ranges.
	 * 
	 * @return array of x,y, and z ranges in order of xmin, xmax, ymin, ymax,
	 *         zmin, zmax
	 */

	public float[] getRanges() {
		float[] ranges = new float[6];

		ranges[0] = xmin;
		ranges[1] = xmax;
		ranges[2] = ymin;
		ranges[3] = ymax;
		ranges[4] = zmin;
		ranges[5] = zmax;

		return ranges;
	}

	/**
	 * Sets the data availability flag. If this flag is <code>false</code>,
	 * <code>SurfaceCanvas</code> will not generate any surface image, even if
	 * the data is available. But it is the programmer's responsiblity to set
	 * this flag to <code>false</code> when data is not available.
	 * 
	 * @param avail
	 *            the availability flag
	 */

	public void setDataAvailability(boolean avail) {
		data_available = avail;
		is_data_available = avail; // see Handlers for mouse input events
									// section
	}

	/**
	 * Sets the new vertices array of surface.
	 * 
	 * @param surfaceVertex
	 *            the new vertices array
	 * @see #getValuesArray
	 */

	public void setValuesArray(SurfaceVertex[][] vertex) {
		// this.surfaceVertex = vertex;
	}

	// /**
	// * Gets the current vertices array.
	// *
	// * @return current vertices array
	// * @see #setValuesArray
	// */
	//
	// public SurfaceVertex[][] getValuesArray() {
	// if (!data_available)
	// return null;
	// return surfaceVertex;
	// }

	private boolean is_data_available; // holds the original data availability
										// flag
	private boolean dragged; // dragged flag
	private int click_x, click_y; // previous mouse cursor position

	/**
	 * <code>mouseDown</code> event handler. Sets internal tracking variables
	 * for dragging operations.
	 * 
	 * @param e
	 *            the event
	 * @param x
	 *            the x coordinate of cursor
	 * @param y
	 *            the y coordinate of cursor
	 */

	public void doExportPNG(File file) throws IOException {
		if (file == null)
			return;
		java.awt.image.BufferedImage bf = new java.awt.image.BufferedImage(getWidth(), getHeight(),
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bf.createGraphics();

		// g2d.setColor(java.awt.Color.white);
		// g2d.fillRect(0,0,getWidth() ,getHeight());
		// g2d.setColor(java.awt.Color.black);
		export(g2d);
		// java.awt.image.BufferedImage bf2=bf.getSubimage(0,0,w,h);
		boolean b = javax.imageio.ImageIO.write(bf, "PNG", file);
	}

	/**
	 * needs batik, will reintroduce it later
	 * 
	 * @throws ParserConfigurationException
	 */
	public void doExportSVG(File file) throws IOException, ParserConfigurationException {
		if (file == null)
			return;

		// Create an instance of org.w3c.dom.Document
		org.w3c.dom.Document document = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.newDocument();

		// Create an instance of the SVG Generator

		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		// Ask the test to render into the SVG Graphics2D implementation
		export(svgGenerator);

		// Finally, stream out SVG to the standard output using UTF-8 //
		// character to byte encoding
		boolean useCSS = true; // we want to use CSS // style attribute
		java.io.Writer out = new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), "UTF-8");
		svgGenerator.stream(out, useCSS);
	}

	/**
	 * Paints surface. Creates surface plot, contour plot, or density plot based
	 * on current vertices array, contour plot flag, and density plot flag. If
	 * no data is available, creates image of base plane and axes.
	 * 
	 * @param g
	 *            the graphics context to paint
	 * @see #setContour
	 * @see #setDensity
	 * @see #setValuesArray
	 * @see #setDataAvailability
	 */

	public void paintComponent(Graphics g) {
		if ((getBounds().width <= 0) || (getBounds().height <= 0))
			return;

		// backing buffer creation

		if ((getBounds().width != prevwidth) || (getBounds().height != prevheight)) {
			// model.setMessage("New image size: " + getBounds().width + "x" +
			// getBounds().height);
			projector.setProjectionArea(new Rectangle(0, 0, getBounds().width, getBounds().height));
			// if (Buffer != null) Buffer.flush();
			// Buffer = createImage(getBounds().width, getBounds().height);
			// if (graphics != null) graphics.dispose();
			// graphics = Buffer.getGraphics();
			prevwidth = getBounds().width;
			prevheight = getBounds().height;
		}

		// if (Buffer == null) Buffer = createImage(getBounds().width,
		// getBounds().height);

		// importVariables();

		printing = g instanceof PrintGraphics;

		if (printing)
			printing(g);

		// uses the buffered image to render the plot
		if (data_available && !interrupted) {
			/*
			 * if (thread.isAlive()) { thread.stop(); while (thread.isAlive()) {
			 * Thread.yield(); } } thread = new Thread(this); thread.start();
			 */
			// do it synchronous instead
			draw(g);
			// renderOffscreen();
			// flushBuffer();
		} else {
			g.setColor(colors.getBackgroundColor());
			g.fillRect(0, 0, getBounds().width, getBounds().height);
			if (is3D())
				drawBoxGridsTicksLabels(g, true);
		}
		interrupted = false;
	}

	private boolean is3D() {
		return true;
	}

	private void printing(Graphics graphics) {

		// modifies variables

		Dimension pagedimension = ((PrintGraphics) graphics).getPrintJob().getPageDimension();

		printwidth = pagedimension.width;
		printheight = prevheight * printwidth / prevwidth;

		if (printheight > pagedimension.height) {
			printheight = pagedimension.height;
			printwidth = prevwidth * printheight / prevheight;
		}

		float savedscalingfactor = projector.get2DScaling();
		projector.setProjectionArea(new Rectangle(0, 0, printwidth, printheight));
		projector.set2DScaling(savedscalingfactor * printwidth / prevwidth);

		graphics.clipRect(0, 0, printwidth, printheight);

		if (!data_available)
			drawBoxGridsTicksLabels(graphics, true);
		graphics.drawRect(0, 0, printwidth - 1, printheight - 1);

		// restores variables

		projector.set2DScaling(savedscalingfactor);
		projector.setProjectionArea(new Rectangle(0, 0, getBounds().width, getBounds().height));
		return;
	}

	/**
	 * Updates image. Just call the <code>paint</code> method to avoid flickers.
	 * 
	 * @param g
	 *            the graphics context to update
	 * @see #paint
	 */

	public void update(Graphics g) {
		paintComponent(g); // do not erase, just paint
	}

	private void export(Graphics g) {
		if (data_available && !interrupted) {
			boolean old = printing;
			printing = true;

			draw(g);

			printing = old;

		} else {
			System.out.println("empty plot");
			g.setColor(colors.getBackgroundColor());
			g.fillRect(0, 0, getBounds().width, getBounds().height);
			if (is3D())
				drawBoxGridsTicksLabels(g, true);
		}
	}

	/*
	 * uses graphics variable to draw the plot
	 */
	private synchronized void draw(Graphics graphics) {
		this.graphics = graphics;

		SurfaceVertex.invalidate();
		plotSurface();
	}

	// /**
	// * Returns the preferred size of this object. This will be the initial
	// size
	// * of <code>SurfaceCanvas</code>.
	// *
	// * @return the preferred size.
	// */
	//
	// public Dimension getPreferredSize() {
	// return new Dimension(550, 550); // initial canvas size
	// }

	public String getXLabel() {
		return xLabel;
	}

	public void setXLabel(String xLabel) {
		firePropertyChange("xLabel", this.xLabel, this.xLabel = xLabel);
	}

	public String getYLabel() {
		return yLabel;
	}

	public void setYLabel(String yLabel) {
		firePropertyChange("yLabel", this.yLabel, this.yLabel = yLabel);
	}

	/*----------------------------------------------------------------------------------------*
	 *                            Private methods begin here                                  *
	 *----------------------------------------------------------------------------------------*/

	private int factor_x, factor_y; // conversion factors

	private int t_x, t_y, t_z; // determines ticks density

	/**
	 * Draws the bounding box of surface.
	 */

	private final void drawBoundingBox() {
		Point startingpoint, projection;

		startingpoint = projector.project(factor_x * 10, factor_y * 10, 10);
		graphics.setColor(colors.getLineBoxColor());
		projection = projector.project(-factor_x * 10, factor_y * 10, 10);
		graphics.drawLine(startingpoint.x, startingpoint.y, projection.x, projection.y);
		projection = projector.project(factor_x * 10, -factor_y * 10, 10);
		graphics.drawLine(startingpoint.x, startingpoint.y, projection.x, projection.y);
		projection = projector.project(factor_x * 10, factor_y * 10, -10);
		graphics.drawLine(startingpoint.x, startingpoint.y, projection.x, projection.y);
	}

	/**
	 * Draws the base plane. The base plane is the x-y plane.
	 * 
	 * @param g
	 *            the graphics context to draw.
	 * @param x
	 *            used to retrieve x coordinates of drawn plane from this
	 *            method.
	 * @param y
	 *            used to retrieve y coordinates of drawn plane from this
	 *            method.
	 */

	private final void drawBase(Graphics g, int[] x, int[] y) {
		Point projection = projector.project(-10, -10, -10);
		x[0] = projection.x;
		y[0] = projection.y;
		projection = projector.project(-10, 10, -10);
		x[1] = projection.x;
		y[1] = projection.y;
		projection = projector.project(10, 10, -10);
		x[2] = projection.x;
		y[2] = projection.y;
		projection = projector.project(10, -10, -10);
		x[3] = projection.x;
		y[3] = projection.y;
		x[4] = x[0];
		y[4] = y[0];

		g.setColor(colors.getBoxColor());
		g.fillPolygon(x, y, 4);

		g.setColor(colors.getLineBoxColor());
		g.drawPolygon(x, y, 5);
	}

	/**
	 * Draws non-surface parts, i.e: bounding box, axis grids, axis ticks, axis
	 * labels, base plane.
	 * 
	 * @param g
	 *            the graphics context to draw
	 * @param draw_axes
	 *            if <code>true</code>, only draws base plane and z axis
	 */

	private final void drawBoxGridsTicksLabels(Graphics g, boolean draw_axes) {
		Point projection, tickpos;
		boolean x_left = false, y_left = false;
		int x[], y[], i;

		x = new int[5];
		y = new int[5];
		if (projector == null)
			return;

		if (draw_axes) {
			drawBase(g, x, y);
			projection = projector.project(0, 0, -10);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = projector.project(10.5f, 0, -10);
			g.drawLine(x[0], y[0], projection.x, projection.y);
			if (projection.x < x[0])
				outString(g, (int) (1.05 * (projection.x - x[0])) + x[0], (int) (1.05 * (projection.y - y[0])) + y[0],
						"x", Label.RIGHT, TOP);
			else
				outString(g, (int) (1.05 * (projection.x - x[0])) + x[0], (int) (1.05 * (projection.y - y[0])) + y[0],
						"x", Label.LEFT, TOP);
			projection = projector.project(0, 11.5f, -10);
			g.drawLine(x[0], y[0], projection.x, projection.y);
			if (projection.x < x[0])
				outString(g, (int) (1.05 * (projection.x - x[0])) + x[0], (int) (1.05 * (projection.y - y[0])) + y[0],
						"y", Label.RIGHT, TOP);
			else
				outString(g, (int) (1.05 * (projection.x - x[0])) + x[0], (int) (1.05 * (projection.y - y[0])) + y[0],
						"y", Label.LEFT, TOP);
			projection = projector.project(0, 0, 10.5f);
			g.drawLine(x[0], y[0], projection.x, projection.y);
			outString(g, (int) (1.05 * (projection.x - x[0])) + x[0], (int) (1.05 * (projection.y - y[0])) + y[0], "z",
					Label.CENTER, CENTER);
		} else {
			factor_x = factor_y = 1;
			projection = projector.project(0, 0, -10);
			x[0] = projection.x;
			projection = projector.project(10.5f, 0, -10);
			y_left = projection.x > x[0];
			i = projection.y;
			projection = projector.project(-10.5f, 0, -10);
			if (projection.y > i) {
				factor_x = -1;
				y_left = projection.x > x[0];
			}
			projection = projector.project(0, 10.5f, -10);
			x_left = projection.x > x[0];
			i = projection.y;
			projection = projector.project(0, -10.5f, -10);
			if (projection.y > i) {
				factor_y = -1;
				x_left = projection.x > x[0];
			}
			setAxesScale();
			drawBase(g, x, y);

			if (isBoxed) {
				projection = projector.project(-factor_x * 10, -factor_y * 10, -10);
				x[0] = projection.x;
				y[0] = projection.y;
				projection = projector.project(-factor_x * 10, -factor_y * 10, 10);
				x[1] = projection.x;
				y[1] = projection.y;
				projection = projector.project(factor_x * 10, -factor_y * 10, 10);
				x[2] = projection.x;
				y[2] = projection.y;
				projection = projector.project(factor_x * 10, -factor_y * 10, -10);
				x[3] = projection.x;
				y[3] = projection.y;
				x[4] = x[0];
				y[4] = y[0];

				g.setColor(colors.getBoxColor());
				g.fillPolygon(x, y, 4);

				g.setColor(colors.getLineBoxColor());
				g.drawPolygon(x, y, 5);

				projection = projector.project(-factor_x * 10, factor_y * 10, 10);
				x[2] = projection.x;
				y[2] = projection.y;
				projection = projector.project(-factor_x * 10, factor_y * 10, -10);
				x[3] = projection.x;
				y[3] = projection.y;
				x[4] = x[0];
				y[4] = y[0];

				g.setColor(colors.getBoxColor());
				g.fillPolygon(x, y, 4);

				g.setColor(colors.getLineBoxColor());
				g.drawPolygon(x, y, 5);
			} else if (isDisplayZ) {
				projection = projector.project(factor_x * 10, -factor_y * 10, -10);
				x[0] = projection.x;
				y[0] = projection.y;
				projection = projector.project(factor_x * 10, -factor_y * 10, 10);
				g.drawLine(x[0], y[0], projection.x, projection.y);

				projection = projector.project(-factor_x * 10, factor_y * 10, -10);
				x[0] = projection.x;
				y[0] = projection.y;
				projection = projector.project(-factor_x * 10, factor_y * 10, 10);
				g.drawLine(x[0], y[0], projection.x, projection.y);
			}

			for (i = -9; i <= 9; i++) {
				if (isDisplayXY || isDisplayGrids) {
					if (!isDisplayGrids || (i % (t_y / 2) == 0) || isDisplayXY) {
						if (isDisplayGrids && (i % t_y == 0))
							projection = projector.project(-factor_x * 10, i, -10);
						else {
							if (i % t_y != 0)
								projection = projector.project(factor_x * 9.8f, i, -10);
							else
								projection = projector.project(factor_x * 9.5f, i, -10);
						}
						tickpos = projector.project(factor_x * 10, i, -10);
						g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
						if ((i % t_y == 0) && isDisplayXY) {
							tickpos = projector.project(factor_x * 10.5f, i, -10);
							if (y_left)
								outFloat(g, tickpos.x, tickpos.y,
										(float) ((double) (i + 10) / 20 * (ymax - ymin) + ymin), Label.LEFT, TOP);
							else
								outFloat(g, tickpos.x, tickpos.y,
										(float) ((double) (i + 10) / 20 * (ymax - ymin) + ymin), Label.RIGHT, TOP);
						}
					}
					if (!isDisplayGrids || (i % (t_x / 2) == 0) || isDisplayXY) {
						if (isDisplayGrids && (i % t_x == 0))
							projection = projector.project(i, -factor_y * 10, -10);
						else {
							if (i % t_x != 0)
								projection = projector.project(i, factor_y * 9.8f, -10);
							else
								projection = projector.project(i, factor_y * 9.5f, -10);
						}
						tickpos = projector.project(i, factor_y * 10, -10);
						g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
						if ((i % t_x == 0) && isDisplayXY) {
							tickpos = projector.project(i, factor_y * 10.5f, -10);
							if (x_left)
								outFloat(g, tickpos.x, tickpos.y,
										(float) ((double) (i + 10) / 20 * (xmax - xmin) + xmin), Label.LEFT, TOP);
							else
								outFloat(g, tickpos.x, tickpos.y,
										(float) ((double) (i + 10) / 20 * (xmax - xmin) + xmin), Label.RIGHT, TOP);
						}
					}
				}

				if (isDisplayXY) {
					tickpos = projector.project(0, factor_y * 14, -10);
					outString(g, tickpos.x, tickpos.y, xLabel, Label.CENTER, TOP);
					tickpos = projector.project(factor_x * 14, 0, -10);
					outString(g, tickpos.x, tickpos.y, yLabel, Label.CENTER, TOP);
				}

				// z grids and ticks

				if (isDisplayZ || (isDisplayGrids && isBoxed)) {
					if (!isDisplayGrids || (i % (t_z / 2) == 0) || isDisplayZ) {
						if (isBoxed && isDisplayGrids && (i % t_z == 0)) {
							projection = projector.project(-factor_x * 10, -factor_y * 10, i);
							tickpos = projector.project(-factor_x * 10, factor_y * 10, i);
						} else {
							if (i % t_z == 0)
								projection = projector.project(-factor_x * 10, factor_y * 9.5f, i);
							else
								projection = projector.project(-factor_x * 10, factor_y * 9.8f, i);
							tickpos = projector.project(-factor_x * 10, factor_y * 10, i);
						}
						g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
						if (isDisplayZ) {
							tickpos = projector.project(-factor_x * 10, factor_y * 10.5f, i);
							if (i % t_z == 0) {
								if (x_left)
									outFloat(g, tickpos.x, tickpos.y,
											(float) ((double) (i + 10) / 20 * (zmax - zmin) + zmin), Label.LEFT, CENTER);
								else
									outFloat(g, tickpos.x, tickpos.y,
											(float) ((double) (i + 10) / 20 * (zmax - zmin) + zmin), Label.RIGHT,
											CENTER);
							}
						}
						if (isDisplayGrids && isBoxed && (i % t_z == 0)) {
							projection = projector.project(-factor_x * 10, -factor_y * 10, i);
							tickpos = projector.project(factor_x * 10, -factor_y * 10, i);
						} else {
							if (i % t_z == 0)
								projection = projector.project(factor_x * 9.5f, -factor_y * 10, i);
							else
								projection = projector.project(factor_x * 9.8f, -factor_y * 10, i);
							tickpos = projector.project(factor_x * 10, -factor_y * 10, i);
						}
						g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
						if (isDisplayZ) {
							tickpos = projector.project(factor_x * 10.5f, -factor_y * 10, i);
							if (i % t_z == 0) {
								if (y_left)
									outFloat(g, tickpos.x, tickpos.y,
											(float) ((double) (i + 10) / 20 * (zmax - zmin) + zmin), Label.LEFT, CENTER);
								else
									outFloat(g, tickpos.x, tickpos.y,
											(float) ((double) (i + 10) / 20 * (zmax - zmin) + zmin), Label.RIGHT,
											CENTER);
							}
						}
						if (isDisplayGrids && isBoxed) {
							if (i % t_y == 0) {
								projection = projector.project(-factor_x * 10, i, -10);
								tickpos = projector.project(-factor_x * 10, i, 10);
								g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
							}
							if (i % t_x == 0) {
								projection = projector.project(i, -factor_y * 10, -10);
								tickpos = projector.project(i, -factor_y * 10, 10);
								g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the axes scaling factor. Computes the proper axis lengths based on
	 * the ratio of variable ranges. The axis lengths will also affect the size
	 * of bounding box.
	 */

	private final void setAxesScale() {
		float scale_x, scale_y, scale_z, divisor;
		int longest;

		if (!isScaleBox) {
			projector.setScaling(1);
			t_x = t_y = t_z = 4;
			return;
		}

		scale_x = xmax - xmin;
		scale_y = ymax - ymin;
		scale_z = zmax - zmin;

		if (scale_x < scale_y) {
			if (scale_y < scale_z) {
				longest = 3;
				divisor = scale_z;
			} else {
				longest = 2;
				divisor = scale_y;
			}
		} else {
			if (scale_x < scale_z) {
				longest = 3;
				divisor = scale_z;
			} else {
				longest = 1;
				divisor = scale_x;
			}
		}
		scale_x /= divisor;
		scale_y /= divisor;
		scale_z /= divisor;

		if ((scale_x < 0.2f) || (scale_y < 0.2f) && (scale_z < 0.2f)) {
			switch (longest) {
			case 1:
				if (scale_y < scale_z) {
					scale_y /= scale_z;
					scale_z = 1.0f;
				} else {
					scale_z /= scale_y;
					scale_y = 1.0f;
				}
				break;
			case 2:
				if (scale_x < scale_z) {
					scale_x /= scale_z;
					scale_z = 1.0f;
				} else {
					scale_z /= scale_x;
					scale_x = 1.0f;
				}
				break;
			case 3:
				if (scale_y < scale_x) {
					scale_y /= scale_x;
					scale_x = 1.0f;
				} else {
					scale_x /= scale_y;
					scale_y = 1.0f;
				}
				break;
			}
		}
		if (scale_x < 0.2f)
			scale_x = 1.0f;
		projector.setXScaling(scale_x);
		if (scale_y < 0.2f)
			scale_y = 1.0f;
		projector.setYScaling(scale_y);
		if (scale_z < 0.2f)
			scale_z = 1.0f;
		projector.setZScaling(scale_z);

		if (scale_x < 0.5f)
			t_x = 8;
		else
			t_x = 4;
		if (scale_y < 0.5f)
			t_y = 8;
		else
			t_y = 4;
		if (scale_z < 0.5f)
			t_z = 8;
		else
			t_z = 4;
	}

	/**
	 * Draws string at the specified coordinates with the specified alignment.
	 * 
	 * @param g
	 *            graphics context to draw
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param s
	 *            the string to draw
	 * @param x_align
	 *            the alignment in x direction
	 * @param y_align
	 *            the alignment in y direction
	 */

	private final void outString(Graphics g, int x, int y, String s, int x_align, int y_align) {
		switch (y_align) {
		case TOP:
			y += g.getFontMetrics(g.getFont()).getAscent();
			break;
		case CENTER:
			y += g.getFontMetrics(g.getFont()).getAscent() / 2;
			break;
		}
		switch (x_align) {
		case Label.LEFT:
			g.drawString(s, x, y);
			break;
		case Label.RIGHT:
			g.drawString(s, x - g.getFontMetrics(g.getFont()).stringWidth(s), y);
			break;
		case Label.CENTER:
			g.drawString(s, x - g.getFontMetrics(g.getFont()).stringWidth(s) / 2, y);
			break;
		}
	}

	/**
	 * Draws float at the specified coordinates with the specified alignment.
	 * 
	 * @param g
	 *            graphics context to draw
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param f
	 *            the float to draw
	 * @param x_align
	 *            the alignment in x direction
	 * @param y_align
	 *            the alignment in y direction
	 */

	private final void outFloat(Graphics g, int x, int y, float f, int x_align, int y_align) {
		// String s = Float.toString(f);
		String s = format(f);
		outString(g, x, y, s, x_align, y_align);
	}

	/*----------------------------------------------------------------------------------------*
	 *                       Plotting routines and methods begin here                         *
	 *----------------------------------------------------------------------------------------*/

	private float color_factor;
	private Point projection;

	private final int poly_x[] = new int[9];
	private final int poly_y[] = new int[9];

	/**
	 * Plots a single plane
	 * 
	 * @param surfaceVertex
	 *            vertices array of the plane
	 * @param verticescount
	 *            number of vertices to process
	 */

	private final void plotPlane(SurfaceVertex[] vertex, int verticescount) {
		int count, loop, index;
		float z, result;
		boolean low1, low2;
		boolean valid1, valid2;
		if (verticescount < 3)
			return;
		count = 0;
		z = 0.0f;
		// line_color = colors.getLineColor();
		low1 = (vertex[0].z < zmin);
		valid1 = !low1 && (vertex[0].z <= zmax);
		index = 1;
		for (loop = 0; loop < verticescount; loop++) {
			low2 = (vertex[index].z < zmin);
			valid2 = !low2 && (vertex[index].z <= zmax);
			if ((valid1 || valid2) || (low1 ^ low2)) {
				if (!valid1) {
					if (low1)
						result = zmin;
					else
						result = zmax;
					float ratio = (result - vertex[index].z) / (vertex[loop].z - vertex[index].z);
					float new_x = ratio * (vertex[loop].x - vertex[index].x) + vertex[index].x;
					float new_y = ratio * (vertex[loop].y - vertex[index].y) + vertex[index].y;
					if (low1)
						projection = projector.project(new_x, new_y, -10);
					else
						projection = projector.project(new_x, new_y, 10);
					poly_x[count] = projection.x;
					poly_y[count] = projection.y;
					count++;
					z += result;
				}
				if (valid2) {
					projection = vertex[index].projection(projector);
					poly_x[count] = projection.x;
					poly_y[count] = projection.y;
					count++;
					z += vertex[index].z;
				} else {
					if (low2)
						result = zmin;
					else
						result = zmax;
					float ratio = (result - vertex[loop].z) / (vertex[index].z - vertex[loop].z);
					float new_x = ratio * (vertex[index].x - vertex[loop].x) + vertex[loop].x;
					float new_y = ratio * (vertex[index].y - vertex[loop].y) + vertex[loop].y;
					if (low2)
						projection = projector.project(new_x, new_y, -10);
					else
						projection = projector.project(new_x, new_y, 10);
					poly_x[count] = projection.x;
					poly_y[count] = projection.y;
					count++;
					z += result;
				}
			}
			if (++index == verticescount)
				index = 0;
			valid1 = valid2;
			low1 = low2;
		}
		if (count > 0) {
			z = (z / count - zmin) * color_factor;
			graphics.setColor(colors.getPolygonColor(curve, z));
			graphics.fillPolygon(poly_x, poly_y, count);
			graphics.setColor(colors.getLineColor(1, z));
			if (isMesh) {

				poly_x[count] = poly_x[0];
				poly_y[count] = poly_y[0];
				count++;
				graphics.drawPolygon(poly_x, poly_y, count);
			}
		}
	}

	private final SurfaceVertex values1[] = new SurfaceVertex[4];

	private void plotTriangles() {

		for (Triangle t : triangles) {
			Thread.yield();
			if (!t.isHalfplane()) {
				values1[0] = new SurfaceVertex((float) t.getA().getX(), (float) t.getA().getY(), (float) t.getA()
						.getZ());
				values1[1] = new SurfaceVertex((float) t.getB().getX(), (float) t.getB().getY(), (float) t.getB()
						.getZ());
				values1[2] = new SurfaceVertex((float) t.getC().getX(), (float) t.getC().getY(), (float) t.getC()
						.getZ());
				curve = 1;
				plotPlane(values1, 3);

			}
		}
	}

	/**
	 * Creates a surface plot
	 */

	private final void plotSurface() {
		float zi, zx;

		try {
			zi = model.getZMin();
			zx = model.getZMax();
			if (zi >= zx)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			return;
		}

		Thread.yield();
		zmin = zi;
		zmax = zx;
		color_factor = 1f / (zmax - zmin);

		if (!printing) {
			graphics.setColor(colors.getBackgroundColor());
			graphics.fillRect(0, 0, getBounds().width, getBounds().height);
		}

		drawBoxGridsTicksLabels(graphics, false);

		if (!plotfunc1 && !plotfunc2) {
			if (isBoxed)
				drawBoundingBox();
			return;
		}

		projector.setZRange(zmin, zmax);

		// direction test

		float distance = projector.getDistance() * projector.getCosElevationAngle();

		// cop : center of projection
		// OMG there is a new SurfaceVertex every time !
		cop = new SurfaceVertex(distance * projector.getSinRotationAngle(), distance * projector.getCosRotationAngle(),
				projector.getDistance() * projector.getSinElevationAngle());
		cop.transform(projector);

		plotTriangles();
		if (isBoxed)
			drawBoundingBox();
	}

}
