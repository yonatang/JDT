package il.ac.idc.jdt.gui.view;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.gui.view.d3.DTJSurface;
import il.ac.idc.jdt.gui.view.d3.DTSurfaceModel;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.ItemSelectable;
import java.awt.Menu;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;

@SuppressWarnings("serial")
public class View3d extends Panel implements View {

	private DTJSurface surface = new DTJSurface();
	private DelaunayTriangulation dt;
	private DTSurfaceModel sm;

	public View3d() {
		setLayout(new BorderLayout());
		initSurface();
		add(surface, BorderLayout.CENTER);
	}

	public void setDT(DelaunayTriangulation dt) {
		this.dt = dt;
		initSurface();
	}

	private void initSurface() {
		sm = new DTSurfaceModel();
		sm.setPlotFunction2(false);

		sm.setCalcDivisions(70);
		sm.setDispDivisions(70);
		sm.setContourLines(50);

		sm.setXMin(-3);
		sm.setXMax(3);
		sm.setYMin(-3);
		sm.setYMax(3);

		sm.setBoxed(true);
		sm.setDisplayXY(false);
		sm.setExpectDelay(false);
		sm.setAutoScaleZ(true);
		sm.setDisplayZ(false);
		sm.setMesh(false);
		sm.setPlotType(PlotType.SURFACE);
		sm.setFirstFunctionOnly(true);

		sm.setPlotColor(PlotColor.SPECTRUM);
		if (dt != null && dt.getBoundingBox() != null) {
			sm.setXMin((float) dt.getBoundingBox().minX());
			sm.setXMax((float) dt.getBoundingBox().maxX());
			sm.setYMin((float) dt.getBoundingBox().minY());
			sm.setYMax((float) dt.getBoundingBox().maxY());
		}
		sm.setDT(dt);
		sm.plot().execute();
		surface.setModel(sm);
	}

	@Override
	public Menu getViewMenu() {
		Menu menu = new Menu("Options");
		final Menu colorMenu = new Menu("Color");
		menu.add(colorMenu);

		final CheckboxMenuItem spectrumMenu = new CheckboxMenuItem("Spectrum", true);
		final CheckboxMenuItem fogMenu = new CheckboxMenuItem("Fog");
		final CheckboxMenuItem daulShadeMenu = new CheckboxMenuItem("Dualshade");
		final CheckboxMenuItem opaqueMenu = new CheckboxMenuItem("Opaque");

		ItemListener il = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				ItemSelectable item = e.getItemSelectable();
				for (int i = 0; i < colorMenu.getItemCount(); i++) {
					CheckboxMenuItem thisItem = (CheckboxMenuItem) colorMenu.getItem(i);
					if (thisItem != e.getItemSelectable())
						thisItem.setState(false);
					else {
						thisItem.setState(true);
					}
				}
				PlotColor color = null;
				if (item == spectrumMenu) {
					color = PlotColor.SPECTRUM;
				} else if (item == fogMenu) {
					color = PlotColor.FOG;
				} else if (item == daulShadeMenu) {
					color = PlotColor.DUALSHADE;
				} else if (item == opaqueMenu) {
					color = PlotColor.OPAQUE;
				}
				if (color != null) {
					sm.setPlotColor(color);
				}
			}
		};
		fogMenu.addItemListener(il);
		spectrumMenu.addItemListener(il);
		daulShadeMenu.addItemListener(il);
		opaqueMenu.addItemListener(il);

		colorMenu.add(spectrumMenu);
		colorMenu.add(fogMenu);
		colorMenu.add(daulShadeMenu);
		colorMenu.add(opaqueMenu);

		CheckboxMenuItem showMeshMenu = new CheckboxMenuItem("Show mesh");
		showMeshMenu.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				sm.setMesh(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		menu.add(showMeshMenu);

		return menu;
	}
}
