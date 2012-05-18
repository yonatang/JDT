package il.ac.idc.jdt.gui3d2;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.IOParsers;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		DelaunayTriangulation dt = new DelaunayTriangulation(IOParsers.readPoints("./terra_13000.smf"));

		System.out.println(dt.getBoundingBox().minX());
		System.out.println(dt.getBoundingBox().minY());
		System.out.println(dt.getBoundingBox().maxX());
		System.out.println(dt.getBoundingBox().maxY());
		MainFrame mf = new MainFrame(dt);

		mf.setVisible(true);
		// TODO Auto-generated method stub

	}
}
