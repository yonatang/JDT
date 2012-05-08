package il.ac.idc.jdt.gui3d;

import java.util.Iterator;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PickRay;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.picking.Intersect;


/*
 * This Class is used to display the triangulation triangles, it holds a triangle array containing
 * all triangles.
 */
public class TrianglesArr3DObject extends Shape3D {
	
	public Appearance _appearance;
	public TriangleArray _triangle_arr;
	il.ac.idc.jdt.BoundingBox _bound_box;
	
	public TrianglesArr3DObject(Iterator<il.ac.idc.jdt.Triangle> triangles_iter,int num_of_triangles, il.ac.idc.jdt.BoundingBox bound_box)
	{
		_appearance = null;
		_triangle_arr = null;
		_bound_box = bound_box;
		
		this.setGeometry(Triangle3DGeometry(triangles_iter, num_of_triangles));
        this.setAppearance(Triangle3DAppearance());
	}
	
	/*
	 * This function gives a triangle its color, the color is set according to the triangle's relative
	 * height.
	 */
	private Color3f get_col_by_height(int z)
	{
		int z_range = (int)(_bound_box.getMaxPoint().getZ() - _bound_box.getMinPoint().getZ());
		if (z < z_range/3)
		{
			float shade = (float)(0.3 + (z/(z_range/3))*0.3);
			return new Color3f(shade, 0f, 0f);
		}
		else if (z < (2*z_range)/3)
		{
			float shade = (float)(0.5 + ((z-z_range/3)/(z_range/3))*0.3);
			return new Color3f(shade, 0f, 0f);
		}
		else
		{	
			float shade = (float)(0.8 + ((z-(2*z_range)/3)/(z_range/3))*0.2);
			return new Color3f(shade, 0f, 0f);
		}
	}
	
	private Geometry Triangle3DGeometry(Iterator<il.ac.idc.jdt.Triangle> triangles_iter,int num_of_triangles)
	{
		_triangle_arr = new TriangleArray(3*num_of_triangles, TriangleArray.COORDINATES | TriangleArray.COLOR_3);
		
		int i=0;
		while ((i<num_of_triangles) && (triangles_iter.hasNext()))
		{
			il.ac.idc.jdt.Triangle curr_triangle = triangles_iter.next();
				
			if((curr_triangle.getA() != null) && (curr_triangle.getB() != null) && (curr_triangle.getC() != null))
			{
				_triangle_arr.setCoordinate(3*i, new Point3d(curr_triangle.getA().getX(), curr_triangle.getA().getY(), curr_triangle.getA().getZ()));
				_triangle_arr.setCoordinate(3*i+1, new Point3d(curr_triangle.getB().getX(), curr_triangle.getB().getY(), curr_triangle.getB().getZ()));
				_triangle_arr.setCoordinate(3*i+2, new Point3d(curr_triangle.getC().getX(), curr_triangle.getC().getY(), curr_triangle.getC().getZ()));
				_triangle_arr.setColor(3*i, get_col_by_height((int)curr_triangle.getA().getZ()));
				_triangle_arr.setColor(3*i+1, get_col_by_height((int)curr_triangle.getB().getZ()));
				_triangle_arr.setColor(3*i+2, get_col_by_height((int)curr_triangle.getC().getZ()));
				i++;
			}
		}
		System.out.println("DEBUG INFO successfuly displayed "+i+" Triangles");
		
		return _triangle_arr;
	}

	private Appearance Triangle3DAppearance()
	{
		_appearance = new Appearance();
		
	    PolygonAttributes polyAttrib = new PolygonAttributes();
	    polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
	    
	    _appearance.setCapability(ALLOW_APPEARANCE_READ);
	    _appearance.setCapability(ALLOW_APPEARANCE_WRITE);
	    _appearance.setPolygonAttributes(polyAttrib);
	    
	    PointAttributes pointatt = new PointAttributes(4, true);
	    _appearance.setPointAttributes(pointatt);
	    
	    LineAttributes lineatt = new LineAttributes(2, LineAttributes.PATTERN_SOLID, false);
	    _appearance.setLineAttributes(lineatt);

	    return _appearance;
	}

}
