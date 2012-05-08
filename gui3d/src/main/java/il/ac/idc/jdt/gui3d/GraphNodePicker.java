package il.ac.idc.jdt.gui3d;

import javax.media.j3d.SceneGraphPath;

/*
 * An interface for a callback that allows a class to be called each time a scene graph object is
 * picked.
 */
public interface GraphNodePicker {

	public void notify_graph_node_picked(SceneGraphPath node_path);
}
