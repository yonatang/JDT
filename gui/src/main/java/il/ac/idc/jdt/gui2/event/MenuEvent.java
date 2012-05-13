package il.ac.idc.jdt.gui2.event;

public class MenuEvent {

	public static enum Type {
		FILE_OPEN, FILE_SAVE_TSIN, FILE_SAVE_SMF, FILE_CLEAR, FILE_EXIT,

		VIEW_2D, VIEW_3D, VIEW_VORONOI, VIEW_TOPO
	}

	private final Type type;

	public MenuEvent(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "MenuEvent [type=" + type + "]";
	}

}
