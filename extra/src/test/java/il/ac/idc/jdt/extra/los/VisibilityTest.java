package il.ac.idc.jdt.extra.los;

import static org.hamcrest.MatcherAssert.assertThat;
import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;

import org.testng.annotations.Test;

@Test
public class VisibilityTest {

	public void shouldSeeTwoPointsSameLine() {
		Point[] points = new Point[] { new Point(0, 0, 1), new Point(1, 1, 1), new Point(1, 0, 0), new Point(0, 1, 0) };

		DelaunayTriangulation dt = new DelaunayTriangulation(points);
		Visibility v = new Visibility();
		Section section = v.computeSection(dt, new Point(0, 0, 1), new Point(1, 1, 1));
		assertThat("Has simple visibility", v.isVisible(section));
	}

	public void shouldSeeTwoPointsCuttingTriangle() {
		// DT creates two triangles:
		// t1 [1,1,1][1.5,1,0][1.1,2,0]
		// t2 [1.1,2,0][1.5,1,0][2,2,1]
		// and therefore visibility should be between [1,1,0] and [2,2,0]
		Point[] points = new Point[] { new Point(1, 1, 1), new Point(2, 2, 1), new Point(1.5, 1, 0), new Point(1.1, 2, 0) };

		DelaunayTriangulation dt = new DelaunayTriangulation(points);
		System.out.println(dt.getTriangulation());
		Visibility v = new Visibility();
		Section section = v.computeSection(dt, new Point(1, 1, 1), new Point(2, 2, 1));
		assertThat("Doens't have simple visibility", v.isVisible(section));

	}

	public void shouldNotSeeTwoPoints() {
		// DT creates two triangles:
		// t1 [1,1,0][1.5,1,1][1.1,2,1]
		// t2 [1.1,2,1][1.5,1,1][2,2,0]
		// and therefore no visibility should be between [1,1,0] and [2,2,0]
		Point[] points = new Point[] { new Point(1, 1, 0), new Point(2, 2, 0), new Point(1.5, 1, 1), new Point(1.1, 2, 1) };

		DelaunayTriangulation dt = new DelaunayTriangulation(points);
		System.out.println(dt.getTriangulation());
		Visibility v = new Visibility();
		Section section = v.computeSection(dt, new Point(1, 1, 0), new Point(2, 2, 0));
		assertThat("Doens't have simple visibility", !v.isVisible(section));

	}
}
