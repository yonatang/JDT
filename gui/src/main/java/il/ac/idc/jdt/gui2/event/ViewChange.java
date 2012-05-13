package il.ac.idc.jdt.gui2.event;

import il.ac.idc.jdt.gui2.View;

public class ViewChange {

	public ViewChange(View view) {
		this.view = view;
	}

	private View view;

	public View getView() {
		return view;
	}

}
