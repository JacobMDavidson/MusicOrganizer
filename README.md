# MusicOrganizer

The concept behind the music organizer is simple. It enumerate a directory searching for music files, and copies those music file to /default_documents_directoy/MusicOrganizer/Artist/Album/Song_Title.ext, where ext is the file extension. Whenever a duplicate is found, it is listed as an error in the output, and the file is not copied. With one click of a button, I was able to organize my entire music library using this tool.

## Using the Organizer

The GUI is simple. There’s one button used to browse to the folder that the user would like to enumerate, and an output table that displays the results of the enumeration. Be wary of the folder you select, as this application will enumerate the **entire** directory, copying every music file it finds. This could take quite some time, and require a lot of space depending on the size of your collection. Each music file that is encountered will be copied to /default_documents_directoy/MusicOrganizerOutput/Artist/Album/Song_Title.ext. For OS X, the default documents directory will be /Users/current_user/, and for Windows it is C:\\Users\\current_user\\Documents. Any music files that already exist at the destination location will not be copied, and an error message will be displayed in the output text box at the end of the traversal. This will allow you to easily locate any duplicates in your collection.

I suggest that you use a tool like [MusicBrainz Picard](http://picard.musicbrainz.org) to properly and consistently tag all of your music prior to using this tool.

#### Technical Details

When a folder is selected, the application uses a SimpleFileVisitor to enumerate the directory. The extension for each visited file is checked to determine if it is a mp3, m4a, or m4p file. If it is a music file, the artist, album, and song title are extracted using the [Jaudiotagger library](http://www.jthink.net/jaudiotagger/), and the file is copied to /default_documents_directoy/MusicOrganizer/Artist/Album/Song_Title.ext using the [Commons IO library](http://commons.apache.org/proper/commons-io/). If that song already exists, an error message is added to a running list of errors. Once completed, all error messages are displayed in the output table allowing the user to review the duplicate files. Note, the source files are not deleted during the migration to ensure no files are lost during the process. The user must manually delete the source files if he or she wants.

<u>Features</u>

1. The SimpleFileVisitor is executed in a SwingWorker allowing it to run in the background. It takes a long time to copy large collections of music, and the SwingWorker eliminates the freezing effect of the enumeration.
2. A Timer is used to constantly update the GUI with the progress of the enumeration.

## Limitations

I built this tool to clean up my own music collection which consisted of .mp3, .m4a, and .m4p files only. In its current state, this application will not find music files with any other extensions. Currently, the application copies music to the destination location, leaving the original music file untouched. This was done purposely to ensure no music is lost during the process, but has the drawback of using a lot of disk space and requiring the user to manually review the results deleting the original files only when he or she is comfortable all music was accounted for. Finally, there is no option for selecting the destination folder. It will always copy music to /default_documents_directoy/MusicOrganizerOutput.

Feel free to fork the repo to tweak this tool to your liking!

## References

This application makes use of the [Jaudiotagger library](http://www.jthink.net/jaudiotagger/) to extract tags from each discovered music file, and the [Commons IO library](http://commons.apache.org/proper/commons-io/) to simplify copying the files to the destination location.

Finally, I once again suggest using [MusicBrainz Picard](http://picard.musicbrainz.org) for tagging your music files. The built in AcoustID feature and open source music metadata database work extremely well for my collection.
