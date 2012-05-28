package il.ac.idc.jdt.gui;

import il.ac.idc.jdt.gui.event.MenuEvent;
import il.ac.idc.jdt.gui.event.MenuEvent.Type;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class MenuBar extends java.awt.MenuBar {

	public MenuBar() {
		init();
	}

	private void addMenuItem(Menu menu, String label, final MenuEvent event) {
		MenuItem menuItem = new MenuItem(label);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GuiEventBus.instance().post(event);
			}
		});
		menu.add(menuItem);
	}

	private void init() {
		Menu fileMenu = new Menu("File");
		addMenuItem(fileMenu, "Open", new MenuEvent(Type.FILE_OPEN));
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "Save as TSIN", new MenuEvent(Type.FILE_SAVE_TSIN));
		addMenuItem(fileMenu, "Save as SMF", new MenuEvent(Type.FILE_SAVE_SMF));
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "Clear", new MenuEvent(Type.FILE_CLEAR));
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "Exit", new MenuEvent(Type.FILE_EXIT));
		add(fileMenu);

		Menu viewMenu = new Menu("View");
		addMenuItem(viewMenu, "2d view", new MenuEvent(Type.VIEW_2D));
		addMenuItem(viewMenu, "3d view", new MenuEvent(Type.VIEW_3D));
		addMenuItem(viewMenu, "Topology", new MenuEvent(Type.VIEW_TOPO));
		addMenuItem(viewMenu, "Voroni", new MenuEvent(Type.VIEW_VORONOI));
		add(viewMenu);
	}
}
