package il.ac.idc.jdt.gui2;

import il.ac.idc.jdt.IOParsers;
import il.ac.idc.jdt.Point;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.List;

public class FileHandler {

	public List<Point> openFile(Frame frame) {
		FileDialog fileDialog = new FileDialog(frame, "Open text file", FileDialog.LOAD);
		fileDialog.setVisible(true);
		String dr = fileDialog.getDirectory();
		String fi = fileDialog.getFile();
		File file = new File(dr, fi);
		if (!file.exists()) {
			MessageBox mb = new MessageBox(frame, "File not found");
			mb.setVisible(true);
			return null;
		}
		try {
			List<Point> points = IOParsers.readPoints(file);
			return points;
		} catch (Exception e) {
			MessageBox mb = new MessageBox(frame, "Cannot read file");
			mb.setVisible(true);
			return null;
		}
	}
}
