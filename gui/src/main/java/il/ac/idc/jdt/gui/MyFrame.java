package il.ac.idc.jdt.gui;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.IOParsers;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;
import il.ac.idc.jdt.extra.los.Section;
import il.ac.idc.jdt.extra.los.Visibility;
import il.ac.idc.jdt.extra.topographic.CounterLine;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * GUI class to test the DelaunayTriangulation Triangulation package:
 */

public class MyFrame extends Frame implements ActionListener {

	public static void main(String[] args) {
		MyFrame win = new MyFrame();
		win.start();
	}

	private static final long serialVersionUID = 1L;
	// *** private data ***
	public static final int POINT = 1, FIND = 2, VIEW1 = 3, VIEW2 = 4, VIEW3 = 5, VIEW4 = 6, SECTION1 = 7, SECTION2 = 8, GUARD = 9,
			CLIENT = 10, DELETE = 11, VORONOI = 12;
	private int _stage, _view_flag = VIEW1, _mc = 0;
	private Triangle _t1, _t2; // tmp triangle for find testing for selection
	private DelaunayTriangulation _ajd = null;

	private Section section;
	protected Vector<Point> _clients, _guards;
	protected Point _dx_f, _dy_f, _dx_map, _dy_map, _p1, _p2;// ,_guard=null,
																// _client=null;
	protected boolean _visible = false;
	private double _topo_dz = 100.0, GH = 30, CH = 5;
	// private Vector<Triangle_dt> _tr = null;//new Vector<Triangle_dt>();
	private Visibility _los = new Visibility();

	// *** text area ***
	public MyFrame() {
		this.setTitle("Delaunay GUI tester");
		this.setSize(500, 500);
		_stage = 0;
		_ajd = new DelaunayTriangulation();

		_dx_f = new Point(10, this.getWidth() - 10);
		_dy_f = new Point(55, this.getHeight() - 10);
		_dx_map = new Point(_dx_f);
		_dy_map = new Point(_dy_f);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public MyFrame(DelaunayTriangulation aj) {
		this.setTitle("ajDelaunay GUI tester");
		this.setSize(500, 500);
		_stage = 0;
		_ajd = aj;
		_dx_f = new Point(10, this.getWidth() - 10);
		_dy_f = new Point(55, this.getHeight() - 10);
		_dx_map = new Point(aj.maxBoundingBox().getX(), aj.minBoundingBox().getX());
		_dy_map = new Point(aj.maxBoundingBox().getY(), aj.minBoundingBox().getY());
		_clients = null;
		_guards = null;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		// _ajd.initTriangle();
		// ajTriangle[] tt = _ajd._triangles;
		if (_ajd == null || _ajd.size() == 0)
			return;
		_dx_f = new Point(10, this.getWidth() - 10);
		_dy_f = new Point(55, this.getHeight() - 10);

		Triangle curr = null;
		Iterator<Triangle> it = _ajd.trianglesIterator();
		while (it.hasNext()) {
			curr = it.next();
			if (!curr.isHalfplane() && _view_flag != VORONOI)
				drawTriangle(g, curr, null);
		}
		it = _ajd.trianglesIterator();
		while (it.hasNext()) {
			curr = it.next();
			if (curr.isHalfplane() && _view_flag != VORONOI)
				drawTriangle(g, curr, null);
		}
		if (_t2 != null)
			drawTriangle(g, _t2, Color.red);
		if (_t1 != null && _stage == FIND)
			drawTriangle(g, _t1, Color.green);
		if (this._view_flag == VIEW3)
			drawTopo(g);

		// debug
		if (_mc < _ajd.getModeCounter() && _view_flag != VORONOI) {
			_mc = _ajd.getModeCounter();
			int i = 0;
			for (Iterator<Triangle> it2 = _ajd.getLastUpdatedTriangles(); it2.hasNext();) {
				i++;
				drawTriangle(g, it2.next(), Color.CYAN);
			}
			System.out.println("   MC: " + _mc + "  number of triangles updated: " + i);

		}

		if (section != null) {
			for (Triangle tr : section.getTriangles()) {
				if (!tr.isHalfplane())
					drawTriangle(g, tr, Color.RED);
			}
			if (_view_flag != VORONOI) {
				for (Point p : section.getPoints()) {
					if (p != null) {
						drawPoint(g, p, Color.BLUE);
					}
				}
			}
			Color c;
			if (_los.isVisible(section)) {
				c = Color.GREEN;
			} else {
				c = Color.BLUE;
			}
			drawLine(g, _p1, _p2, c);
		}

		// if (_los != null && (_stage == SECTION1 || _stage == SECTION2)) {
		// if (_los != null && _los._tr != null) {
		// it = _los._tr.iterator();
		// while (it.hasNext()) {
		// curr = it.next();
		// if (!curr.isHalfplane())
		// drawTriangle(g, curr, Color.RED);
		// }
		// }
		// Iterator<Point> pit = _los._section.iterator();
		// int i = 0;
		// while (pit.hasNext()) {
		// Point curr_p = pit.next();
		// if (curr_p != null && _view_flag != VORONOI) {
		// drawPoint(g, curr_p, Color.BLUE);
		// System.out.println(i + ") " + curr_p + "  dist _p1: " +
		// _p1.distance(curr_p));
		// i++;
		// }
		// }
		// drawLine(g, _p1, _p2);
		// }
		/*
		 * if(_stage == GUARD | _stage == CLIENT) { if(_p1!=null)
		 * drawPoint(g,_p1,6,Color.ORANGE); if(_p2!=null) { if(_visible)
		 * drawPoint(g,_p2,6,Color.BLUE); else drawPoint(g,_p2,6, Color.RED); }
		 * }
		 */
		if (_stage == GUARD || _stage == CLIENT) {
			int[] ccc = new int[0];
			if (_clients != null)
				ccc = new int[_clients.size()];
			for (int gr = 0; _guards != null && gr < _guards.size(); gr++) {
				Point gg = _guards.elementAt(gr);
				drawPoint(g, gg, 8, Color.ORANGE);

				for (int c = 0; _clients != null && c < _clients.size(); c++) {
					Point cc = _clients.elementAt(c);
					drawPoint(g, cc, 6, Color.white);
					// Color cl = Color.RED;
					Section section = _los.computeSection(_ajd, gg, cc);
					if (_los.isVisible(section)) {
						this.drawLine(g, gg, cc);
						ccc[c]++;
					}
				}
			}
			int c1 = 0, c2 = 0;
			for (int i = 0; i < ccc.length; i++) {
				if (ccc[i] > 0) {
					c1++;
					c2 += ccc[i];
				}
			}
			if (c1 > 0)
				System.out.println("clients:" + ccc.length + "  visible c:" + c1 + "   ave:" + c2 / c1);
		}
		if (_view_flag == VORONOI) {
			drawVoronoi(g);
		}

	}

	/**
	 * Draws Voronoi diagram based on current triangulation A Voronoi diagram
	 * can be created from a Delaunay triangulation by connecting the
	 * circumcenters of neighboring triangles
	 * 
	 * By Udi Schneider
	 * 
	 * @param g
	 *            Graphics object
	 */
	private void drawVoronoi(Graphics g) {
		Iterator<Triangle> it = _ajd.trianglesIterator();

		while (it.hasNext()) {
			Triangle curr = it.next();
			Color temp = g.getColor();
			g.setColor(Color.BLACK);

			// For a half plane, only one corner is needed
			if (curr.isHalfplane()) {
				try {
					drawPolygon(g, _ajd.calcVoronoiCell(curr, curr.getA()));
				} catch (NullPointerException e) {
				}
			}
			// for a full triangle, check every corner
			else {
				// if a triangle has no neighbors, a null exception will be
				// caught
				// and no action taken.
				// this is expected, for example when there is only one triangle
				// at the start of the user input
				try {
					drawPolygon(g, _ajd.calcVoronoiCell(curr, curr.getA()));
				} catch (NullPointerException e) {
				}
				try {
					drawPolygon(g, _ajd.calcVoronoiCell(curr, curr.getB()));
				} catch (NullPointerException e) {
				}
				try {
					drawPolygon(g, _ajd.calcVoronoiCell(curr, curr.getC()));
				} catch (NullPointerException e) {
				}

				drawPoint(g, curr.getA(), Color.RED);
				drawPoint(g, curr.getB(), Color.RED);
				drawPoint(g, curr.getC(), Color.RED);
			}
			g.setColor(temp);
		}
	}

	private void drawTopo(Graphics g) {
		Triangle curr = null;
		Iterator<Triangle> it = _ajd.trianglesIterator();
		g.setColor(Color.red);
		while (it.hasNext()) {
			curr = it.next();
			if (!curr.isHalfplane())
				drawTriangleTopoLines(g, curr, this._topo_dz, null);
		}
	}

	private void drawTriangleTopoLines(Graphics g, Triangle t, double dz, Color cl) {
		if (t.getA().getZ() < 0 || t.getB().getZ() < 0 || t.getC().getZ() < 0)
			return;
		Point[] p12 = computePoints(t.getA(), t.getB(), dz);
		Point[] p23 = computePoints(t.getB(), t.getC(), dz);
		Point[] p31 = computePoints(t.getC(), t.getA(), dz);

		int i12 = 0, i23 = 0, i31 = 0;
		boolean cont = true;
		while (cont) {
			cont = false;
			if (i12 < p12.length && i23 < p23.length && p12[i12].getZ() == p23[i23].getZ()) {
				g.setColor(Color.YELLOW);
				if (p12[i12].getZ() % 200 > 100)
					g.setColor(Color.red);
				drawLine(g, p12[i12], p23[i23]);
				i12++;
				i23++;
				cont = true;
			}
			if (i23 < p23.length && i31 < p31.length && p23[i23].getZ() == p31[i31].getZ()) {
				g.setColor(Color.YELLOW);
				if (p23[i23].getZ() % 200 > 100)
					g.setColor(Color.red);
				drawLine(g, p23[i23], p31[i31]);
				i23++;
				i31++;
				cont = true;
			}
			if (i12 < p12.length && i31 < p31.length && p12[i12].getZ() == p31[i31].getZ()) {
				g.setColor(Color.YELLOW);
				if (p12[i12].getZ() % 200 > 100)
					g.setColor(Color.red);
				drawLine(g, p12[i12], p31[i31]);
				i12++;
				i31++;
				cont = true;
			}
		}
	}

	private Point[] computePoints(Point p1, Point p2, double dz) {
		Point[] ans = new Point[0];
		double z1 = Math.min(p1.getZ(), p2.getZ()), z2 = Math.max(p1.getZ(), p2.getZ());
		if (z1 == z2)
			return ans;
		double zz1 = ((int) (z1 / dz)) * dz;
		if (zz1 < z1)
			zz1 += dz;
		double zz2 = ((int) (z2 / dz)) * dz;
		int len = (int) ((zz2 - zz1) / dz) + 1, i = 0;
		ans = new Point[len];
		double DZ = p2.getZ() - p1.getZ(), DX = p2.getX() - p1.getX(), DY = p2.getY() - p1.getY();
		for (double z = zz1; z <= zz2; z += dz) {
			double scale = (z - p1.getZ()) / DZ;
			double x = p1.getX() + DX * scale;
			double y = p1.getY() + DY * scale;
			ans[i] = new Point(x, y, z);
			i++;
		}
		return ans;
	}

	public void drawTopographicMap(Graphics g, List<CounterLine> counterLines) {
		g.setColor(Color.YELLOW);
		for (CounterLine line : counterLines) {
			int[] xPoints = new int[line.getNumberOfPoints()];
			int[] yPoints = new int[line.getNumberOfPoints()];

			Iterator<Point> pointsItr = line.getPointsListIterator();
			int index = 0;
			while (pointsItr.hasNext()) {
				Point point = pointsItr.next();
				Point screenPoint = world2screen(point);
				xPoints[index] = (int) screenPoint.getX();
				yPoints[index] = (int) screenPoint.getY();
				index++;
			}
			if (line.isClosed())
				g.drawPolygon(xPoints, yPoints, xPoints.length);
			else
				g.drawPolyline(xPoints, yPoints, xPoints.length);
		}
	}

	public void drawTriangle(Graphics g, Triangle t, Color cl) {
		if (_view_flag == VIEW1 || t.isHalfplane()) {
			if (cl != null)
				g.setColor(cl);
			if (t.isHalfplane()) {
				if (cl == null)
					g.setColor(Color.blue);
				drawLine(g, t.getA(), t.getB());
			} else {
				if (cl == null)
					g.setColor(Color.black);
				drawLine(g, t.getA(), t.getB());
				drawLine(g, t.getB(), t.getC());
				drawLine(g, t.getC(), t.getA());
			}
		} else {
			// //////////////////////////////////////////////////////////////////
			double maxZ = _ajd.maxBoundingBox().getZ();
			double minZ = _ajd.minBoundingBox().getZ();
			double z = (t.getA().getZ() + t.getB().getZ() + t.getC().getZ()) / 3.0;
			double dz = maxZ - minZ;
			int co = 30 + (int) (220 * ((z - minZ) / dz));
			if (cl == null)
				cl = new Color(co, co, co);
			g.setColor(cl);
			int[] xx = new int[3], yy = new int[3];
			// double f = 0;
			// double dx_map = _dx_map.getY()- _dx_map.getX();
			// double dy_map = _dy_map.getY()- _dy_map.getX();

			// f = (t.getA().getX() -_dx_map.getX())/dx_map;
			Point p1 = world2screen(t.getA());
			xx[0] = (int) p1.getX();
			yy[0] = (int) p1.getY();
			Point p2 = world2screen(t.getB());
			xx[1] = (int) p2.getX();
			yy[1] = (int) p2.getY();
			Point p3 = world2screen(t.getC());
			xx[2] = (int) p3.getX();
			yy[2] = (int) p3.getY();

			g.fillPolygon(xx, yy, 3);

			// ////////////////////////////////////
		}
	}

	/**
	 * Draws a polygon represented by Point_dt points
	 * 
	 * By Udi Schneider
	 */
	public void drawPolygon(Graphics g, Point[] polygon) {
		int[] x = new int[polygon.length];
		int[] y = new int[polygon.length];
		for (int i = 0; i < polygon.length; i++) {
			polygon[i] = this.world2screen(polygon[i]);
			x[i] = (int) polygon[i].getX();
			y[i] = (int) polygon[i].getY();
		}
		g.drawPolygon(x, y, polygon.length);
	}

	public void drawLine(Graphics g, Point p1, Point p2) {
		drawLine(g, p1, p2, null);
	}

	public void drawLine(Graphics g, Point p1, Point p2, Color color) {
		if (color != null) {
			g.setColor(color);
		}
		Point t1 = this.world2screen(p1);
		Point t2 = this.world2screen(p2);
		g.drawLine((int) t1.getX(), (int) t1.getY(), (int) t2.getX(), (int) t2.getY());
	}

	public void drawPoint(Graphics g, Point p1, Color cl) {
		drawPoint(g, p1, 4, cl);
	}

	public void drawPoint(Graphics g, Point p1, int r, Color cl) {
		// g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(),
		// (int)p2.getY());
		Point t1 = this.world2screen(p1);
		g.setColor(cl);
		g.fillOval((int) t1.getX() - r / 2, (int) t1.getY() - r / 2, r, r);
	}

	public void start() {
		setVisible(true);
		initMenuBar();
	}

	private void initMenuBar() {
		MenuBar mbar = new MenuBar();

		Menu m = new Menu("File");
		MenuItem m1;
		m1 = new MenuItem("Open");
		m1.addActionListener(this);
		m.add(m1);
		m1 = new MenuItem("Save tsin");
		m1.addActionListener(this);
		m.add(m1);
		m1 = new MenuItem("Save smf");
		m1.addActionListener(this);
		m.add(m1);

		MenuItem m6 = new MenuItem("Clear");
		m6.addActionListener(this);
		m.add(m6);

		MenuItem m2 = new MenuItem("Exit");
		m2.addActionListener(this);
		m.add(m2);
		mbar.add(m);

		m = new Menu("Input");
		MenuItem m3 = new MenuItem("Point");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("100-rand-ps");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Guard-30m");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Client-5m");
		m3.addActionListener(this);
		m.add(m3);

		mbar.add(m);

		m = new Menu("Functions");
		MenuItem m5 = new MenuItem("Delete");
		m5.addActionListener(this);
		m.add(m5);
		mbar.add(m);

		m = new Menu("View");
		m3 = new MenuItem("Lines");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Triangles");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Topo");
		m3.addActionListener(this);
		m.add(m3);
		MenuItem m4 = new MenuItem("Find");
		m4.addActionListener(this);
		m.add(m4);
		m4 = new MenuItem("Section");
		m4.addActionListener(this);
		m.add(m4);
		m4 = new MenuItem("Info");
		m4.addActionListener(this);
		m.add(m4);
		m4 = new MenuItem("CH");
		m4.addActionListener(this);
		m.add(m4);
		mbar.add(m);
		m4 = new MenuItem("Voronoi");
		m4.addActionListener(this);
		m.add(m4);
		mbar.add(m);

		setMenuBar(mbar);
		this.addMouseListener(new MouseManeger());
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String arg = evt.getActionCommand();
		if (arg.equals("Open"))
			openTextFile();
		else if (arg.equals("Save tsin"))
			saveTextFile();
		else if (arg.equals("Save smf"))
			saveTextFile2();
		else if (arg.equals("Lines")) {
			this._view_flag = VIEW1;
			repaint();
		} else if (arg.equals("Triangles")) {
			this._view_flag = VIEW2;
			repaint();
		} else if (arg.equals("Topo")) {
			this._view_flag = VIEW3;
			repaint();
		} else if (arg.equals("Clear")) {
			_ajd = new DelaunayTriangulation();
			_dx_map = new Point(_dx_f);
			_dy_map = new Point(_dy_f);
			_clients = null;
			_guards = null;
			_mc = 0;
			repaint();
		} else if (arg.equals("Exit")) {
			System.exit(209);
		}

		else if (arg.equals("Point")) {
			_stage = POINT;
		} else if (arg.equals("CH")) {
			_ajd.getConvexHullVerticesIterator();
		} else if (arg.equals("100-rand-ps")) {
			double x0 = 10, y0 = 60, dx = this.getWidth() - x0 - 10, dy = this.getHeight() - y0 - 10;
			for (int i = 0; i < 100; i++) {
				double x = Math.random() * dx + x0;
				double y = Math.random() * dy + y0;
				Point q = new Point(x, y);
				Point p = screen2world(q);
				_ajd.insertPoint(p);
			}
			repaint();
		} else if (arg.equals("Find")) {
			_stage = FIND;
		} else if (arg.equals("Section")) {
			_stage = SECTION1;
		} else if (arg.equals("Client-5m")) {
			// System.out.println("CL!");
			_stage = MyFrame.CLIENT;

		} else if (arg.equals("Guard-30m")) {// System.out.println("GR!");
			_stage = MyFrame.GUARD;
		} else if (arg.equals("Info")) {
			String ans = "" + _ajd.getClass().getCanonicalName() + "  # vertices:" + _ajd.size() + "  # triangles:" + _ajd.trianglesSize();
			ans += "   min BB:" + _ajd.minBoundingBox() + "   max BB:" + _ajd.maxBoundingBox();
			System.out.println(ans);
			System.out.println();
		} else if (arg.equals("Delete")) {
			_stage = DELETE;
		} else if (arg.equals("Voronoi")) {
			_view_flag = VORONOI;
			repaint();
		}

	}

	// *** private methodes - random points obs ****

	// ********** Private methodes (open,save...) ********

	private void openTextFile() {
		_stage = 0;
		FileDialog d = new FileDialog(this, "Open text file", FileDialog.LOAD);
		d.setVisible(true);
		String dr = d.getDirectory();
		String fi = d.getFile();
		_clients = null;
		_guards = null;
		if (fi != null) { // the user actualy choose a file.
			try {
				_ajd = new DelaunayTriangulation(IOParsers.readPoints(dr + fi));
				_dx_map = new Point(_ajd.minBoundingBox().getX(), _ajd.maxBoundingBox().getX());
				_dy_map = new Point(_ajd.minBoundingBox().getY(), _ajd.maxBoundingBox().getY());
				repaint();
			} catch (Exception e) { // in case something went wrong.
				System.out.println("** Error while reading text file **");
				System.out.println(e);
			}

		}
	}

	private void saveTextFile() {
		_stage = 0;
		FileDialog d = new FileDialog(this, "Saving TSIN text file", FileDialog.SAVE);
		d.setVisible(true);
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) {
			try {
				IOParsers.exportTsin(_ajd, dr + fi);
			} catch (Exception e) {
				// TODO - better error message
				System.out.println("ERR cant save to text file: " + dr + fi);
				e.printStackTrace();
			}
		}
	}

	public void saveTextFile2() {
		_stage = 0;
		FileDialog d = new FileDialog(this, "Saving SMF text file", FileDialog.SAVE);
		d.setVisible(true);
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) {
			try {
				IOParsers.exportSmf(_ajd.getTriangulation(), dr + fi);
			} catch (Exception e) {
				// TODO - better error message
				System.out.println("ERR cant save to text file: " + dr + fi);
				e.printStackTrace();
			}
		}
	}

	// ***** inner classes (mouse maneger) *****
	// class mouseManeger1 extends MouseMotionAdapter {
	// public void mouseMoved(MouseEvent e) {
	// m_x = e.getX(); m_y = e.getY();
	// }
	// }

	private class MouseManeger extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			int xx = e.getX();
			int yy = e.getY();
			// System.out.println("_stage: "+_stage+"  selected: "+len);
			switch (_stage) {
			case (0): {
				System.out.println("[" + xx + "," + yy + "]");
				break;
			}
			case (POINT): {
				Point q = new Point(xx, yy);
				Point p = screen2world(q);
				_ajd.insertPoint(p);
				repaint();
				break;
			}
			case (DELETE): {
				Point q = new Point(xx, yy);
				// finds
				Point p = screen2world(q);
				Point pointToDelete = _ajd.findClosePoint(p);
				if (pointToDelete == null) {
					System.err.println("Error : the point doesn't exists");
					return;
				}
				_ajd.deletePoint(pointToDelete);
				repaint();
				break;
			}
			case (FIND): {
				Point q = new Point(xx, yy);
				Point p = screen2world(q);
				_t1 = _ajd.find(p);
				repaint();
				break;
			}
			case (SECTION1): {
				Point q = new Point(xx, yy);
				_p1 = screen2world(q);
				_stage = SECTION2;
				break;
			}
			case (SECTION2): {
				Point q = new Point(xx, yy);
				_p2 = screen2world(q);
				section = _los.computeSection(_ajd, _p1, _p2);
				repaint();
				_stage = SECTION1;
				break;
			}
			case (GUARD): {
				Point q = new Point(xx, yy);
				_p1 = screen2world(q);
				if (_guards == null)
					_guards = new Vector<Point>();
				_guards.add(new Point(_p1.getX(), _p1.getY(), GH));
				/*
				 * if(_p2!=null) { _los = new Visibility(_ajd);
				 * _los.computeSection(_p1,_p2); _visible =
				 * _los.isVisible(30,5); }
				 */
				repaint();
				break;
			}
			case (CLIENT): {
				Point q = new Point(xx, yy);
				_p2 = screen2world(q);
				if (_clients == null)
					_clients = new Vector<Point>();
				_clients.add(new Point(_p2.getX(), _p2.getY(), CH));
				/*
				 * if(_p1!=null) { _los = new Visibility(_ajd);
				 * _los.computeSection(_p1,_p2); _visible =
				 * _los.isVisible(30,5); }
				 */
				repaint();
				break;
			}

			// //////////////
			}
		}
	}

	private Point screen2world(Point p) {
		double x = transform(_dx_f, p.getX(), _dx_map);
		double y = transformY(_dy_f, p.getY(), _dy_map);
		return new Point(x, y);
	}

	private Point world2screen(Point p) {
		double x = transform(_dx_map, p.getX(), _dx_f);
		double y = transformY(_dy_map, p.getY(), _dy_f);
		return new Point(x, y);
	}

	/**
	 * transforms the point p from the Rectangle th into this Rectangle, Note:
	 * r.contains(p) must be true! assume p.x
	 * < p
	 * .y
	 * 
	 * */

	static double transform(Point range, double x, Point new_range) {
		double dx1 = range.getY() - range.getX();
		double dx2 = new_range.getY() - new_range.getX();

		double scale = (x - range.getX()) / dx1;
		double ans = new_range.getX() + dx2 * scale;
		return ans;
	}

	/**
	 * transform the point p from the Rectangle th into this Rectangle ,Note:
	 * flips the Y cordination for frame!, Note: r.contains(p) must be true!
	 * assume p.x
	 * < p
	 * .y
	 * 
	 * */

	static double transformY(Point range, double x, Point new_range) {
		double dy1 = range.getY() - range.getX();
		double dy2 = new_range.getY() - new_range.getX();

		double scale = (x - range.getX()) / dy1;
		double ans = new_range.getY() - dy2 * scale;
		return ans;
	}
}
