package il.ac.idc.jdt.gui;

import il.ac.idc.jdt.DelaunayTriangulation;
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
		if (fi == null) {
			return null;
		}
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

	public void saveFileAsTsin(Frame frame, DelaunayTriangulation dt) {
		FileDialog d = new FileDialog(frame, "Saving TSIN text file", FileDialog.SAVE);
		d.setVisible(true);
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi == null)
			return;
		if (!fi.endsWith(".tsin")) {
			fi = fi + ".tsin";
		}

		File file = new File(dr, fi);
		try {
			if (file.exists() && !file.delete())
				throw new RuntimeException("Cannot delete file");
			IOParsers.exportTsin(dt, file);
		} catch (Exception e) {
			MessageBox mb = new MessageBox(frame, "Cannot save file");
			mb.setVisible(true);
		}
	}

	public void saveFileAsSmf(Frame frame, DelaunayTriangulation dt) {
		FileDialog d = new FileDialog(frame, "Saving SMF text file", FileDialog.SAVE);
		d.setVisible(true);
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi == null)
			return;
		if (!fi.endsWith(".smf")) {
			fi = fi + ".smf";
		}
		File file = new File(dr, fi);
		try {
			if (file.exists() && !file.delete())
				throw new RuntimeException("Cannot delete file");
			IOParsers.exportSmf(dt.getTriangulation(), file);
		} catch (Exception e) {
			MessageBox mb = new MessageBox(frame, "Cannot save file");
			mb.setVisible(true);
		}
	}
}
