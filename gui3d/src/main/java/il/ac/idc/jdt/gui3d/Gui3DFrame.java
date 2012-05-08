package il.ac.idc.jdt.gui3d;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.IOParsers;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.FileDialog;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class Gui3DFrame implements ActionListener {

	private Frame _frame = null;
	private Graphics3DEngine _g_engine = null;
	private DelaunayTriangulation _current_dt = null;
	private boolean _in_add_point_tool = false;
	private boolean _in_delete_point_tool = false;

	public Gui3DFrame(Frame frame, Graphics3DEngine engine, DelaunayTriangulation dt) {
		_frame = frame;
		_g_engine = engine;

		_current_dt = dt;

		_frame.setTitle("Delaunay Triangulation 3D");
		_frame.setSize(512, 512);

		CreateDialog();

		_frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private void CreateDialog() {
		MenuBar mbar = new MenuBar();

		Menu menu_inst = new Menu("File");
		MenuItem m1 = new MenuItem("New");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Open");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Save smf");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Save tsin");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);

		menu_inst = new Menu("View");
		m1 = new MenuItem("Surface");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Grid");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Points");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);

		menu_inst = new Menu("Tools");
		m1 = new MenuItem("Add Point");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Delete Point");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Scale Z");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);

		menu_inst = new Menu("Help");
		m1 = new MenuItem("Help");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);

		_frame.setMenuBar(mbar);
	}

	public void point_picked_callback(il.ac.idc.jdt.Point point_picked) {
		if (_in_delete_point_tool == true) {
			_in_add_point_tool = false;
			_in_delete_point_tool = false;

			_current_dt
					.deletePoint(new il.ac.idc.jdt.Point((int) point_picked.getX(), (int) point_picked.getY(), (int) point_picked.getZ()));
			_g_engine.setNewDelaunayTriangulation(_current_dt, false, false);
		}
	}

	public void mouse_position_callback(il.ac.idc.jdt.Point pdt) {
		if (_in_add_point_tool == true) {
			_in_delete_point_tool = false;
			_in_add_point_tool = false;

			String response = JOptionPane.showInputDialog(null, "Enter the point's Z value: (lowest Z value is " + (int) pdt.getZ() + ")",
					"Point height", JOptionPane.QUESTION_MESSAGE);

			if (response == null) {
				return;
			}

			il.ac.idc.jdt.Point point_to_add = new il.ac.idc.jdt.Point((int) pdt.getX(), (int) pdt.getY(),
					(int) (Double.parseDouble(response)));

			_current_dt.insertPoint(point_to_add);
			_g_engine.setNewDelaunayTriangulation(_current_dt, false, false);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String arg = evt.getActionCommand();
		if (arg.equals("New")) {
			clearTriangulation();
		} else if (arg.equals("Open")) {
			openCoordinatesFile();
		} else if (arg.equals("Save tsin")) {
			saveCoordinatesFile_tsin();
		} else if (arg.equals("Save smf")) {
			saveCoordinatesFile_smf();
		} else if (arg.equals("Surface")) {
			_g_engine.changeViewType(Graphics3DEngine.VIEW_TYPE_SURFACE);
		} else if (arg.equals("Grid")) {
			_g_engine.changeViewType(Graphics3DEngine.VIEW_TYPE_GRID);
		} else if (arg.equals("Points")) {
			_g_engine.changeViewType(Graphics3DEngine.VIEW_TYPE_POINTS);
		} else if (arg.equals("Delete Point")) {
			_in_add_point_tool = false;
			_in_delete_point_tool = true;
			_g_engine.make_points_visible_and_pickable();
		} else if (arg.equals("Add Point")) {
			_in_delete_point_tool = false;
			_in_add_point_tool = true;
		} else if (arg.equals("Scale Z")) {
			String response = JOptionPane.showInputDialog(null, "Enter Z scale:", "Height scaling", JOptionPane.QUESTION_MESSAGE);

			if (response == null) {
				return;
			}
			float scale = 1.0f;
			try {
				scale = Float.parseFloat(response);
				scaleZ(scale);
			} catch (Exception e) {
			}
		} else if (arg.equals("Help")) {
			showInfo();
		}
	}

	private void showInfo() {
		String help_info = "Use the mouse and keyboard buttons to navigate around\n"
				+ "Mouse controls: hold and click left mouse button to rotate, right button to translate, middle mouse button to zoom in and out\n"
				+ "Keyboard controls: use the arrow keys to navigate, page-up and down to look up or down\n"
				+ "*********************************************************************************************************\n"
				+ "Use the add/delete point tools to dynamically add points to the triangulation\n"
				+ "*********************************************************************************************************\n"
				+ "Asaf & Tzach @ IDC JAN 2010";
		JOptionPane.showMessageDialog(null, help_info, "Help", JOptionPane.INFORMATION_MESSAGE);
	}

	private void scaleZ(float scale) throws Exception {
		Iterator<il.ac.idc.jdt.Point> point_iter = _current_dt.verticesIterator();
		il.ac.idc.jdt.Point scaled_points_arr[] = new il.ac.idc.jdt.Point[_current_dt.size()];
		for (int i = 0; i < _current_dt.size(); ++i) {
			if (point_iter.hasNext() == false) {
				throw new Exception();
			}
			il.ac.idc.jdt.Point curr_p = point_iter.next();
			scaled_points_arr[i] = new il.ac.idc.jdt.Point(curr_p.getX(), curr_p.getY(), scale * curr_p.getZ());
		}
		_current_dt = new DelaunayTriangulation(scaled_points_arr);
		_g_engine.setNewDelaunayTriangulation(_current_dt, false, false);
	}

	public void saveCoordinatesFile_tsin() {
		FileDialog d = new FileDialog(_frame, "Saving TSIN text file", FileDialog.SAVE);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) {
			try {
				IOParsers.exportTsin(_current_dt, dr + fi);
			} catch (Exception e) {
				System.out.println("ERR cant save to text file: " + dr + fi);
				e.printStackTrace();
			}
		}
	}

	public void saveCoordinatesFile_smf() {
		FileDialog d = new FileDialog(_frame, "Saving SMF text file", FileDialog.SAVE);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) {
			try {
				IOParsers.exportSmf(_current_dt.getTriangulation(), dr + fi);
			} catch (Exception e) {
				System.out.println("ERR cant save to text file: " + dr + fi);
				e.printStackTrace();
			}
		}
	}

	public DelaunayTriangulation getTriangulation() {
		return _current_dt;
	}

	private void clearTriangulation() {
		_current_dt = new DelaunayTriangulation();
		_g_engine.setNewDelaunayTriangulation(_current_dt, true, true);
	}

	private void openCoordinatesFile() {
		FileDialog d = new FileDialog(_frame, "Open text file", FileDialog.LOAD);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if ((dr != null) && (fi != null)) {
			String file_name = dr + fi;
			try {
				_current_dt = new DelaunayTriangulation(IOParsers.readPoints(file_name));
				_g_engine.setNewDelaunayTriangulation(_current_dt, true, true);
			} catch (Exception e) {
				System.err.println("failed to open input coordinated file!!");
				e.printStackTrace();
			}

		}
	}

}
