package com.jacobmdavidson.MusicOrganizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MusicFileList extends SimpleFileVisitor<Path> {

	private int numVisibleFiles = 0;
	private int numHiddenFiles = 0;
	private int numErrors = 0;
	private int numSuccess = 0;
	private String documentsPath;
	private GUI gui;
	private List<String> errors;

	public MusicFileList(String documentsPath, GUI gui) {
		this.gui = gui;
		this.documentsPath = documentsPath;
		errors = new LinkedList<String>();
	}

	public void reportResults() {
		gui.updateListing("Traversal results:");
		gui.updateListing((numVisibleFiles + numHiddenFiles)
				+ " total files traversed (" + numVisibleFiles
				+ " visible, and " + numHiddenFiles + " hidden files)");
		gui.updateListing(numSuccess + " files successfully migrated");
		gui.updateListing(numErrors + " errors.");
		for (String error : errors) {
			gui.updateListing(error);
		}
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {
		try {
			if (!Files.isHidden(path)) {
				numVisibleFiles++;
				File file = new File(path.toUri());
				MusicFile musicFile = new MusicFile(file, documentsPath);
				boolean errorCheck = musicFile.migrateFile();
				if (errorCheck) {
					numSuccess++;
				} else {
					numErrors++;
					errors.add(musicFile.toString());
				}
			} else {
				numHiddenFiles++;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.out.println(exc.toString());

		return FileVisitResult.CONTINUE;
	}

	public void clear() {
		numVisibleFiles = 0;
		numHiddenFiles = 0;
		numSuccess = 0;
		numErrors = 0;

	}

	public int getNumVisibleFiles() {
		return numVisibleFiles;
	}

	public int getNumHiddenFiles() {
		return numHiddenFiles;
	}

	public int getNumSuccess() {
		return numSuccess;
	}

	public int getNumErrors() {
		return numErrors;
	}

}
