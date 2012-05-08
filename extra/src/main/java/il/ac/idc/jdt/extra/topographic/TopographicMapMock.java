package il.ac.idc.jdt.extra.topographic;

import il.ac.idc.jdt.Triangle;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Mock Implementation of {@link ITopographicMap}
 * 
 * @version 1.0 12 December 2009
 * @author Omri Gutman
 * 
 */
public class TopographicMapMock implements ITopographicMap {

	/**
	 * Mock implementation
	 * 
	 * @return an empty list of counter lines.
	 * @see ITopographicMap#createCounterLines(java.util.Iterator, int)
	 */
	@Override
	public ArrayList<CounterLine> createCounterLines(Iterator<Triangle> triangles, int height) {
		return new ArrayList<CounterLine>();
	}

}
