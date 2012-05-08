package il.ac.idc.jdt.gui3d;

import il.ac.idc.jdt.DelaunayTriangulation;

import java.applet.Applet;

import com.sun.j3d.utils.applet.MainFrame;

/*
 * Main class used to run the GUI3d
 */
public class Gui3DMain extends Applet {

	public Gui3DMain() {
		DelaunayTriangulation triangulation = new DelaunayTriangulation();

		Graphics3DEngine g_engine = new Graphics3DEngine(triangulation);
		Gui3DFrame frame = new Gui3DFrame(new MainFrame(g_engine, 512, 512), g_engine, triangulation);
		g_engine.set_gui_frame(frame);
	}

	public static void main(String[] args) {
		Gui3DMain gui = new Gui3DMain();
	}
}
