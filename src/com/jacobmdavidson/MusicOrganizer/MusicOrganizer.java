/**
 * Name: Jacob Davidson
 * Course: CSC 385-C (CRN: 12331)
 * Assignment: Module 2 Homework - Enumerating a File System Using Recursion
 * File: DirectoryLister.java
 * Date: 09/06/2013
 */

package com.jacobmdavidson.MusicOrganizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.*;

/**
 * DirectoryLister class. This class allows the user to recursively display the
 * contents of a selected directory in the file system.
 */
public class MusicOrganizer implements ActionListener {

	// -----------------------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------------------

	/** GUI used to display results */
	private GUI gui;

	/** base path of directory to be traversed */
	private String basePath;

	/** document path for files to be copied to */
	private String documentsPath;

	/** List of music files */
	private MusicFileList musicFileList;

	private Timer timer;

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * Create a new DirectoryLister that uses the specified GUI.
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
		timer.addActionListener(this);
		timer.setInitialDelay(100);

		// clear results of any previous traversal
		gui.resetGUI();

		// allow user to select a directory from the file system
		basePath = gui.getAbsoluteDirectoryPath();

		timer.start();
		// update the address label on the GUI
		// gui.setAddressLabelText(basePath);

		// traverse the selected directory, and display the contents
		showDirectoryContents(basePath);

		// timer.stop();
		musicFileList.clear();
	}

	/**
	 * Show the directory listing. An error message is displayed if basePath
	 * does not represent a valid directory.
	 * 
	 * @param basePath
	 *            the absolute path of a directory in the file system.
	 */
	public void showDirectoryContents(String basePath) {
		// Use a try/catch block to determine if a valid directory was selected
		try {
			// Instantiate a File object with the provided basePath
			// File f = new File(basePath);
			Path path = Paths.get(basePath);
			// Traverse the directory and display the contents
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
	 * Recursive method to enumerate the contents of a directory.
	 *
	 * @param f
	 *            directory to enumerate
	 */
	private void enumerateDirectory(Path path) {
		SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				gui.disableButton();
				Files.walkFileTree(path, musicFileList);
				return null;
			}

			@Override
			protected void done() {
				gui.enableButton();
				musicFileList.reportResults();
				timer.stop();
				gui.setAddressLabelText(basePath);
			}
		};
		mySwingWorker.execute();

	}

	public String getDocumentsPath() {
		return documentsPath;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		gui.setAddressLabelText("Scanning... " + musicFileList.getNumSuccess()
				+ " files successfully copied.");
	}
}