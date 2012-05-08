package il.ac.idc.jdt.gui3d;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.picking.Intersect;
import com.sun.j3d.utils.geometry.Sphere;

/*
 * This class is used to pick scene graph nodes, it implements a behavior that is
 * attached to the branch group. The class calls a callback function on each press event.
 * in case a graph node was picked, a second callback is called
 */
public class MousePick3DObjectBehavior extends Behavior {
	
	private final static Vector3d IN_VEC = new Vector3d(0.f, 0.f, -1.f);   
	
	private WakeupCriterion[] _mouseEvents;
	private WakeupOr _mouseCriterion;
	
	private Canvas3D _canvas3D = null;
	private BranchGroup _bg = null;
	
	private Point3d mousePos;
	private Transform3D imWorldT3d;   // for image plate-->world transform
	private PickRay _pick_ray;
	private SceneGraphPath nodePath;
	
	private GraphNodePicker _picker_callback = null;
	private MousePosListener _mouse_pos_callback = null;
	
	public MousePick3DObjectBehavior(Canvas3D canvas3D, BranchGroup bg, GraphNodePicker callback, MousePosListener pos_listener)
	{
		_canvas3D = canvas3D;
		_bg = bg;
		_picker_callback = callback;
		_mouse_pos_callback = pos_listener;
		
		mousePos = new Point3d();
		imWorldT3d = new Transform3D();
		_pick_ray = new PickRay();
	}
	
	public void initialize() 
	{
	  _mouseEvents = new WakeupCriterion[3];
	  _mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_ENTERED);
	  _mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
	  _mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
	  _mouseCriterion = new WakeupOr(_mouseEvents);
	  wakeupOn(_mouseCriterion);
	}
	
	public void processStimulus(Enumeration criteria) 
	{
	  WakeupCriterion wakeup;
	  AWTEvent[] event;
	  int id;
	  int xPos, yPos;
	  while (criteria.hasMoreElements()) {
	      wakeup = (WakeupCriterion) criteria.nextElement();
	      if (wakeup instanceof WakeupOnAWTEvent) {
	         event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
	         for (int i=0; i<event.length; i++) { 
	            xPos = ((MouseEvent)event[i]).getX();
	            yPos = ((MouseEvent)event[i]).getY();
	            id = event[i].getID();
	            if (id == MouseEvent.MOUSE_PRESSED)
	            {
	            	 processPress(xPos, yPos);
	            }
	              
	          }
	      }
	   }
	   wakeupOn (_mouseCriterion);
	} // end of processStimulus()
	
	private void processPress(int xPos, int yPos)
	/* Send a pick ray into the world starting from the mouse 
	  press position. Get the closest intersecting node, and
	  accept the first Shape3D as the selected board position.
	*/
	{ 
		
		Point3d eyePos = new Point3d();
	    _canvas3D.getCenterEyeInImagePlate(eyePos);

	    Point3d mousePos = new Point3d();
	    _canvas3D.getPixelLocationInImagePlate(xPos, yPos, mousePos);
	    
	    
	    Transform3D transform3D = new Transform3D();
	    _canvas3D.getImagePlateToVworld(transform3D);

	    transform3D.transform(eyePos);
	    transform3D.transform(mousePos);
	    
	    Vector3d mouseVec = new Vector3d();
	    mouseVec.sub(mousePos, eyePos);
	    mouseVec.normalize();
	    
	    _pick_ray.set(mousePos, mouseVec);
	    nodePath = _bg.pickAny(_pick_ray);

	    if (_mouse_pos_callback != null)
	    {
	    	_mouse_pos_callback.notify_mouse_position(mousePos, mouseVec, _pick_ray);
	    }
	    
	    if ((nodePath != null) && (_picker_callback!=null))
	    {
	    	_picker_callback.notify_graph_node_picked(nodePath);
	    }
	}  // end of processPress()
}
