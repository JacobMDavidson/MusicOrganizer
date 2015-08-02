package com.jacobmdavidson.MusicOrganizer;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class MusicFile {

	private File musicFile;
	private File destinationFile;
	private Tag musicFileTag;
	private boolean error = false;

	private String fileExtension;
	private String absolutePath;
	private String artist;
	private String albumTitle;
	private String songTitle;
	private String documentsPath;
	private String errorMessage = "";

	public MusicFile(File musicFile, String documentsPath) {
		this.musicFile = musicFile;
		this.documentsPath = documentsPath;
		absolutePath = musicFile.getAbsolutePath();
		fileExtension = FilenameUtils.getExtension(absolutePath);

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
		if (artist == null) {
			artist = cleanString(musicFileTag.getFirst(FieldKey.ARTIST));
		}

		// Set the album title and song title from the tags
		albumTitle = cleanString(musicFileTag.getFirst(FieldKey.ALBUM));
		songTitle = cleanString(musicFileTag.getFirst(FieldKey.TITLE));

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

	private void constructDestination() {
		String destinationPath = documentsPath + File.separator
				+ "MusicOrganizerOutput" + File.separator + artist
				+ File.separator + albumTitle + File.separator + songTitle
				+ "." + fileExtension;
		// Destination File
		destinationFile = new File(destinationPath);
	}

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

	public boolean isError() {
		return this.error;
	}

}
