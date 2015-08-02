package com.jacobmdavidson.MusicOrganizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class MusicFileList extends SimpleFileVisitor<Path> {

	/** Track the number of visible files encountered */
	private int numVisibleFiles = 0;

	/** Track the number of hidden files encountered */
	private int numHiddenFiles = 0;

	/** Track the number of errors */
	private int numErrors = 0;

	/** Track the numnber of successful migrations */
	private int numSuccess = 0;

	/** The default documents path */
	private String documentsPath;

	/** The main GUI */
	private GUI gui;

	/** A list of all errors encountered */
	private List<String> errors;

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * Build the MusicFileList
	 * 
	 * @param documentsPath
	 *            default documents path
	 * @param gui
	 *            main application GUI
	 */
	public MusicFileList(String documentsPath, GUI gui) {
		this.gui = gui;
		this.documentsPath = documentsPath;
	}

	/**
	 * Update the GUI with the results of the migration attempt
	 */
	public void reportResults() {
		gui.updateListing("Traversal results:");
		gui.updateListing("Destination folder: " + documentsPath + File.separator
				+ "MusicOrganizerOutput" + File.separator);
		gui.updateListing((numVisibleFiles + numHiddenFiles)
				+ " total files traversed (" + numVisibleFiles
				+ " visible, and " + numHiddenFiles + " hidden files)");
		gui.updateListing(numSuccess + " files successfully migrated");
		gui.updateListing(numErrors + " errors.");
		for (String error : errors) {
			gui.updateListing(error);
		}
	}

	/**
	 * Enumerate the chosen directory, and migrate all music files to the
	 * destination folder
	 */
	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {
		try {
			// If the file is visible, attempt to migrate it
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

				// Otherwise, the file is hidden
			} else {
				numHiddenFiles++;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

		return FileVisitResult.CONTINUE;
	}

	/**
	 * If the enumeration failed for a specific file, prin the results to
	 * console
	 */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.out.println(exc.toString());

		return FileVisitResult.CONTINUE;
	}

	/**
	 * Clear the tracking variables
	 */
	public void clear() {
		numVisibleFiles = 0;
		numHiddenFiles = 0;
		numSuccess = 0;
		numErrors = 0;

	}

	/**
	 * Return the number of visible files
	 * 
	 * @return number of visible files
	 */
	public int getNumVisibleFiles() {
		return numVisibleFiles;
	}

	/**
	 * Return the number of hidden files encountered
	 * 
	 * @return number of hidden files
	 */
	public int getNumHiddenFiles() {
		return numHiddenFiles;
	}

	/**
	 * Return the number of successful migrations
	 * 
	 * @return number of successful migrations
	 */
	public int getNumSuccess() {
		return numSuccess;
	}

	/**
	 * Return the number of errors encountered during the migration process
	 * 
	 * @return number of errors
	 */
	public int getNumErrors() {
		return numErrors;
	}

	/**
	 * Initialize the list of errors
	 */
	public void initializeErrors() {
		errors = new LinkedList<String>();
	}

}
