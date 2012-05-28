package il.ac.idc.jdt.gui.view.d3;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import net.ericaro.surfaceplotter.surface.LineAccumulator;

/**
 * The class <code>LineAccumulator</code> accumulates line drawing information
 * and then draws all accumulated lines together. It is used as contour lines
 * accumulator in Surface Plotter.
 * 
 * @author Yanto Suryono
 */

public class DTLineAccumulator {
	private List<LineRecord> accumulator;

	/**
	 * The constructor of <code>LineAccumulator</code>
	 */

	public DTLineAccumulator() {
		accumulator = new LinkedList<LineRecord>();
	}

	/**
	 * Adds a line to the accumulator.
	 * 
	 * @param x1
	 *            the first point's x coordinate
	 * @param y1
	 *            the first point's y coordinate
	 * @param x2
	 *            the second point's x coordinate
	 * @param y2
	 *            the second point's y coordinate
	 */

	public void addLine(int x1, int y1, int x2, int y2) {
		if (x1 <= 0 || y1 <= 0 || x2 <= 0 || y2 <= 0)
			return;
		// System.out.println("("+x1+","+y1+","+x2+","+y2+")");
		accumulator.add(new LineRecord(x1, y1, x2, y2));
	}

	/**
	 * Clears accumulator.
	 */

	public void clearAccumulator() {
		accumulator.clear();
	}

	/**
	 * Draws all accumulated lines.
	 * 
	 * @param g
	 *            the graphics context to draw
	 */

	public void drawAll(Graphics g) {
		for (LineRecord line : accumulator)
			g.drawLine(line.x1, line.y1, line.x2, line.y2);
	}
}

/**
 * Represents a stright line. Used by <code>LineAccumulator</code> class.
 * 
 * @see LineAccumulator
 */

class LineRecord {
	/**
	 * @param x1
	 *            the first point's x coordinate
	 */
	public final int x1;

	/**
	 * @param y1
	 *            the first point's y coordinate
	 */
	public final int y1;

	/**
	 * @param x2
	 *            the second point's x coordinate
	 */
	public final int x2;

	/**
	 * @param y2
	 *            the second point's y coordinate
	 */
	public final int y2;

	/**
	 * The constructor of <code>LineRecord</code>
	 * 
	 * @param x1
	 *            the first point's x coordinate
	 * @param y1
	 *            the first point's y coordinate
	 * @param x2
	 *            the second point's x coordinate
	 * @param y2
	 *            the second point's y coordinate
	 */

	LineRecord(int x1, int y1, int x2, int y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}