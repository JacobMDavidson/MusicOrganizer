package com.jacobmdavidson.MusicOrganizer;

public class Driver {

	public static void main(String[] args) {
		GUI gui = new GUI();
		MusicOrganizer musicOrganizer = new MusicOrganizer(gui);
		gui.registerModel(musicOrganizer);
	}

}