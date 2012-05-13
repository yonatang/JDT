package il.ac.idc.jdt.gui2;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.gui2.event.MenuEvent;
import il.ac.idc.jdt.gui2.view.View2d;
import il.ac.idc.jdt.gui2.view.View3d;
import il.ac.idc.jdt.gui2.view.ViewTopology;
import il.ac.idc.jdt.gui2.view.ViewVoroni;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class MainFrame extends Frame {

	private GuiEventBus eventBus;
	private FileHandler fileHandler = new FileHandler();
	private MenuBar menuBar = new MenuBar();
	private DelaunayTriangulation dt = new DelaunayTriangulation();
	private View2d view2d = new View2d();
	private View3d view3d = new View3d();
	private ViewTopology viewTopology = new ViewTopology();
	private ViewVoroni viewVoroni = new ViewVoroni();
	private Panel displayPanel = new Panel();

	private void clearView() {
		remove(view2d);
		remove(view3d);
		remove(viewTopology);
		remove(viewVoroni);
	}

	public MainFrame() {
		eventBus = GuiEventBus.instance();
		eventBus.register(this);
		setSize(500, 500);
		setMenuBar(menuBar);
		setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	@Subscribe
	public void handleMenuEvents(MenuEvent event) {
		switch (event.getType()) {
		case FILE_EXIT:
			System.exit(0);
			break;
		case FILE_OPEN:
			List<Point> points = fileHandler.openFile(this);
			dt = new DelaunayTriangulation(points);
			view2d.setDT(dt);
			clearView();
			add(view2d, BorderLayout.CENTER);
			validate();
			break;
		case VIEW_2D:
			view2d.setDT(dt);
			clearView();
			add(view2d, BorderLayout.CENTER);
			validate();
			break;
		case VIEW_3D:
			view3d.setDT(dt);
			clearView();
			add(view3d, BorderLayout.CENTER);
			validate();
			break;
		case VIEW_TOPO:
			viewTopology.setDT(dt);
			clearView();
			add(viewTopology, BorderLayout.CENTER);
			validate();
			break;
		case VIEW_VORONOI:
			viewVoroni.setDT(dt);
			clearView();
			add(viewVoroni, BorderLayout.CENTER);
			validate();
			break;

		default:
			break;
		}

	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
	}
}
