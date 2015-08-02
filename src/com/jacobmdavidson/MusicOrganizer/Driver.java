package com.jacobmdavidson.MusicOrganizer;

public class Driver {

	// Load the GUI and register the model
	public static void main(String[] args) {
		GUI gui = new GUI();
		MusicOrganizer musicOrganizer = new MusicOrganizer(gui);
		gui.registerModel(musicOrganizer);
	}

}