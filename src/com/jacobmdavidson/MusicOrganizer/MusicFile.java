package com.jacobmdavidson.MusicOrganizer;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class MusicFile {

	/** The source music file */
	private File musicFile;

	/** The destination music file */
	private File destinationFile;

	/** The Tags of the source music file */
	private Tag musicFileTag;

	/** Error flag set to true if an error occured during the copy */
	private boolean error = false;

	/** The file extension type for the music file */
	private String fileExtension;

	/** The absolute path of the source music file */
	private String absolutePath;

	/** The artist of the source music file */
	private String artist;

	/** The album title of the source music file */
	private String albumTitle;

	/** The song title of the source music file */
	private String songTitle;

	/** The path to the default documents folder */
	private String documentsPath;

	/** Error message, if an error occured */
	private String errorMessage = "";

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * Create a new MusicFile objecy
	 * 
	 * @param musicFile
	 *            - source music file
	 * @param documentsPath
	 *            - default documents folder for the user
	 */
	public MusicFile(File musicFile, String documentsPath) {

		// Set the music file and documents path
		this.musicFile = musicFile;
		this.documentsPath = documentsPath;

		// Determine the absolute path for the source music file
		absolutePath = musicFile.getAbsolutePath();

		// Retreive the file extension from the absolute path
		fileExtension = FilenameUtils.getExtension(absolutePath);

		// If this is a valid music file, retrieve the Tag, and copy the file to
		// the destination
		if (fileExtension.equals("mp3") || fileExtension.equals("m4a")
				|| fileExtension.equals("m4p")) {
			try {
				AudioFile audioF = AudioFileIO.read(this.musicFile);
				musicFileTag = audioF.getTag();

				// Set the artist, album, and song titles
				error = setAttributes();

				// Set the destination file
				if (!error)
					constructDestination();
			} catch (Exception e) {
				error = true;
				errorMessage = "unidentified error.";
			}

			// Else, this is not a valid music file, set the error flag and
			// error message
		} else {
			errorMessage = "not a valid music file.";
			error = true;
		}

	}

	/**
	 * Set the artist, album, and song title attributes
	 */
	private boolean setAttributes() {

		// Try to set by album artist first
		artist = cleanString(musicFileTag.getFirst(FieldKey.ALBUM_ARTIST));

		// If artist is null, set via the artist tag
		if (artist.isEmpty() || artist == null) {
			artist = cleanString(musicFileTag.getFirst(FieldKey.ARTIST));
		}

		// Set the album title and song title from the tags
		albumTitle = cleanString(musicFileTag.getFirst(FieldKey.ALBUM));
		songTitle = cleanString(musicFileTag.getFirst(FieldKey.TITLE));

		// If any of these fields are null, return an error message
		if (artist == null || albumTitle == null || songTitle == null) {
			errorMessage = "does not have valid tags for migration.";
			return true;
		}
		return false;
	}

	/**
	 * Remove invalid path characters from the music file tags
	 * 
	 * @param myString
	 * @return
	 */
	private String cleanString(String myString) {
		String cleanedString = myString.replace("\"", "'");
		cleanedString = cleanedString.replaceAll("[<>:\\\\/|?*.]", "");
		return cleanedString;
	}

	/**
	 * Build the destination file
	 */
	private void constructDestination() {
		String destinationPath = documentsPath + File.separator
				+ "MusicOrganizerOutput" + File.separator + artist
				+ File.separator + albumTitle + File.separator + songTitle
				+ "." + fileExtension;
		
		// Instantiate the destination File
		destinationFile = new File(destinationPath);
	}

	/**
	 * Return the error message or successful details of the migration
	 */
	public String toString() {
		String message = "";
		if (error) {
			message = "Error: " + this.absolutePath + " " + errorMessage;
		} else {
			message = "Source file: " + this.absolutePath
					+ ". Destination file: "
					+ destinationFile.getAbsolutePath();
		}
		return message;
	}

	/**
	 * Copy the music file to the desination
	 * 
	 * @return true if successful
	 */
	public boolean migrateFile() {
		if (error) {
			// The file should not be copied, there was an error
			return false;
		} else {
			try {
				if (!destinationFile.exists()) {
					FileUtils.copyFile(musicFile, destinationFile);
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				// Error copying file, set the error flag and return false
				error = true;
				errorMessage = "duplicate file. Cannot migrate " + musicFile
						+ ".";
				return false;
			}
			return true;
		}

	}

	/**
	 * Retrieve the error flag
	 * 
	 * @return true if there was an error
	 */
	public boolean isError() {
		return this.error;
	}

}
