package com.jacobmdavidson.MusicOrganizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.*;

/**
 * MusicOrganizer class. This class allows the user to recursively search the
 * contents of a selected directory for music files, and copy those files to a
 * new location based on the following folder structure:
 * default_documents_folder/artist/album_title/song
 */
public class MusicOrganizer implements ActionListener {

	// -----------------------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------------------

	/** GUI used to display results */
	private GUI gui;

	/** base path of directory to be traversed */
	private String basePath;

	/** document path to which files will be copied */
	private String documentsPath;

	/** List of music files traversed */
	private MusicFileList musicFileList;

	/** Timer used in updated migration progress */
	private Timer timer;

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * Create a new MusicOrganizer that uses the specified GUI.
	 */
	public MusicOrganizer(GUI gui) {
		this.gui = gui;
		documentsPath = new JFileChooser().getFileSystemView()
				.getDefaultDirectory().toString();
		musicFileList = new MusicFileList(documentsPath, gui);
		timer = new Timer(100, null);
	}

	// -----------------------------------------------------------------------
	// Methods
	// -----------------------------------------------------------------------

	/**
	 * Allow user to select a directory for traversal.
	 */
	public void selectDirectory() {

		// Add an action listener to the timer
		timer.addActionListener(this);
		timer.setInitialDelay(100);

		// clear results of any previous traversal
		gui.resetGUI();

		// allow user to select a directory from the file system
		basePath = gui.getAbsoluteDirectoryPath();

		// If a legitimate folder was selected, begin migration
		if (!basePath.equals("cancelled")) {

			// Initialize the MusicFileList errors linked list
			musicFileList.initializeErrors();

			// Start the timer that will display progress
			timer.start();

			// Traverse the selected directory, copy music to the destination,
			// and display the output results
			beginTraversal(basePath);

			// timer.stop();
			musicFileList.clear();
		} else {
			// Display that the action was cancelled
			gui.updateListing("Migration cancelled.");
		}
	}

	/**
	 * Instantiate a Path object from the base path, and begin the enumeration
	 * 
	 * @param basePath
	 *            the absolute path of a directory in the file system.
	 */
	public void beginTraversal(String basePath) {
		// Use a try/catch block to determine if a valid directory was selected
		try {
			// Instantiate a Path object with the provided basePath
			Path path = Paths.get(basePath);

			// Traverse the directory
			enumerateDirectory(path);

		}

		// Catch a NullPointerException if it is thrown
		catch (NullPointerException e) {
			// Display an appropriate message
			JOptionPane.showMessageDialog(null,
					"The specified folder is invalid or does not exist!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Recursive method to enumerate the contents of a directory, and copy all
	 * found music to the destination.
	 *
	 * @param path
	 *            directory to enumerate
	 */
	private void enumerateDirectory(Path path) {

		// Instantiate a SwingWorker that enumerates the directory
		SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {

			// Disable the select folder button, and walk the file tree
			@Override
			protected Void doInBackground() throws Exception {
				gui.disableButton();
				Files.walkFileTree(path, musicFileList);
				return null;
			}

			// Enable the select folder button, report the results, stop the
			// timer, and update the address label with the source folder path
			// that was enumerated.

			@Override
			protected void done() {
				gui.enableButton();
				musicFileList.reportResults();
				timer.stop();
				gui.setAddressLabelText(basePath);
			}
		};

		// Execute the SwingWorker
		mySwingWorker.execute();

	}

	/**
	 * Retrieve the default documents path
	 * 
	 * @return the default documents path
	 */
	public String getDocumentsPath() {
		return documentsPath;
	}

	/**
	 * Action performed via the Timer object to update the GUI with the progress
	 * of the enumeration
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		gui.setAddressLabelText("Scanning... " + musicFileList.getNumSuccess()
				+ " files successfully copied.");
	}
}