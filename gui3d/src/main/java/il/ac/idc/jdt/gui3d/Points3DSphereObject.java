package il.ac.idc.jdt.gui3d;

import java.util.Iterator;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

/*
 * The class holds an array of spheres, one for every triangulation point.
 * It is used to draw all points in case a point selection is needed (in deletion for instance)
 */
public class Points3DSphereObject implements GraphNodePicker {
	
	// main transform group for all spheres
	TransformGroup _spheres_tg = null;
	
	Graphics3DEngine _3d_engine = null;
	
	//all spheres share the same apperance
	Appearance _sphere_app = null;
	
	//default sphere size
	int _point_size = 1;

	public Points3DSphereObject(Graphics3DEngine engine, Iterator<il.ac.idc.jdt.Point> points_iter,int num_of_points, il.ac.idc.jdt.BoundingBox bound_box)
	{
		_3d_engine = engine;
		_spheres_tg = new TransformGroup();
		_spheres_tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		_spheres_tg.setCapability(TransformGroup.ALLOW_PICKABLE_READ);
		_spheres_tg.setCapability(TransformGroup.ALLOW_PICKABLE_WRITE);
	
		setPointSize(bound_box);
		createSpheres(points_iter, num_of_points);
	}
	
	public void setPointSize(il.ac.idc.jdt.BoundingBox bound_box)
	{
		int max_point_size = 20;
		int min_point_size = 4;
		double bound_box_size = bound_box.getMaxPoint().getX() - bound_box.getMinPoint().getX();
		_point_size = Math.min(max_point_size, (int)(min_point_size + (max_point_size-min_point_size)*(bound_box_size / 1000)));
	}
	
	public void notify_graph_node_picked(SceneGraphPath node_path)
	{
		Node node = node_path.getNode(2);
		if (node instanceof TransformGroup)
		{
			Transform3D T3d = new Transform3D();
			TransformGroup tg = (TransformGroup)node;
			tg.getTransform(T3d);
			Vector3f transform_vec = new Vector3f();
			T3d.get(transform_vec);
			_3d_engine.point_picked_callback(new Point3d(transform_vec.x, transform_vec.y, transform_vec.z));
		}
	}
	
	public void make_points_visible_and_pickable()
	{
		RenderingAttributes ra = new RenderingAttributes();
        ra.setVisible(true);
        _sphere_app.setRenderingAttributes(ra);
	}
	
	/*
	 * The function creates a sphere for each point, a translated transform group is 
	 * also attached to each sphere.
	 */
	private void createSpheres(Iterator<il.ac.idc.jdt.Point> points_iter,int num_of_points)
	{
		Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
		
		ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(green);
        PolygonAttributes pa = new PolygonAttributes();
        RenderingAttributes ra = new RenderingAttributes();
        ra.setVisible(false);
        pa.setPolygonMode (PolygonAttributes.POLYGON_FILL);
        
        //note that all spheres share the same appearance
        _sphere_app = new Appearance();
        _sphere_app.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
        _sphere_app.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);
        _sphere_app.setColoringAttributes(ca);
        _sphere_app.setPolygonAttributes(pa);
        _sphere_app.setRenderingAttributes(ra);
        
		int i=0;
		while ((i<num_of_points) && (points_iter.hasNext()))
		{
			TransformGroup trans = new TransformGroup();
			trans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
			trans.setCapability(TransformGroup.ALLOW_PICKABLE_READ);
			trans.setCapability(TransformGroup.ALLOW_PICKABLE_WRITE);
			Vector3f translate = new Vector3f();
	        Transform3D T3D = new Transform3D();
	        
	        il.ac.idc.jdt.Point currp = points_iter.next();
	        translate.set( (float)currp.getX(), (float)currp.getY(), (float)currp.getZ());
	        T3D.setTranslation(translate);
	        trans.setTransform(T3D);
	        
	        Sphere sphere = new Sphere(_point_size, _sphere_app);
	        sphere.setPickable(true);
	        sphere.setCapability(Shape3D.ENABLE_PICK_REPORTING);
	        sphere.setCapability(TransformGroup.ALLOW_PICKABLE_READ);
	        sphere.setCapability(TransformGroup.ALLOW_PICKABLE_WRITE);
	        
	        trans.addChild(sphere); 
			_spheres_tg.addChild(trans);
			i++;
		}
		
	}
	
	public TransformGroup getSpheresBG()
	{
		return _spheres_tg;
	}
	
	
}
