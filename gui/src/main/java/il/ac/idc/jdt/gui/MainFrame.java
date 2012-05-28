package il.ac.idc.jdt.gui;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.gui.event.MenuEvent;
import il.ac.idc.jdt.gui.view.View;
import il.ac.idc.jdt.gui.view.View2d;
import il.ac.idc.jdt.gui.view.View3d;
import il.ac.idc.jdt.gui.view.ViewTopology;
import il.ac.idc.jdt.gui.view.ViewVoroni;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Menu;
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
	private Menu viewMenu;

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
		setView(view2d);

	}

	private void setView(View view) {
		if (viewMenu != null) {
			menuBar.remove(viewMenu);
		}
		viewMenu = view.getViewMenu();
		if (viewMenu != null) {
			menuBar.add(viewMenu);
		}
		view.setDT(dt);
		clearView();
		add((Component) view, BorderLayout.CENTER);
		validate();
		repaint();
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
			setView(view2d);
			break;
		case FILE_SAVE_TSIN:
			fileHandler.saveFileAsTsin(this, dt);
			break;
		case FILE_SAVE_SMF:
			fileHandler.saveFileAsSmf(this, dt);
			break;

		case VIEW_2D:
			setView(view2d);
			break;
		case VIEW_3D:
			setView(view3d);
			break;
		case VIEW_TOPO:
			setView(viewTopology);
			break;
		case VIEW_VORONOI:
			setView(viewVoroni);
			break;
		case FILE_CLEAR:
			dt = new DelaunayTriangulation();
			setView(view2d);
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
