package il.ac.idc.jdt.gui2.view;

import il.ac.idc.jdt.DelaunayTriangulation;

import java.awt.Menu;

public interface View {

	public abstract void setDT(DelaunayTriangulation dt);

	public Menu getViewMenu();

}