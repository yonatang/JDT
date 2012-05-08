package il.ac.idc.jdt.gui3d;

import javax.media.j3d.PickRay;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/*
 * This interface allows a class to register a callback for mouse position data,
 * upon a mouse press the callback function will be called with the vector data for the
 * mouse position
 */
public interface MousePosListener {

	public void notify_mouse_position(Point3d p3d, Vector3d v3d, PickRay pick_ray);
	
}
