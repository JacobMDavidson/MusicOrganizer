package com.jacobmdavidson.MusicOrganizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class GUI extends JFrame implements ComponentListener {

	private static final long serialVersionUID = 1L;

	/** Event handler for button click events. */
	private ButtonHandler buttonHandler;

	/**
	 * Container for displaying Browse button, scanning progress, and address of
	 * selected directory
	 */
	private JPanel addressPanel;

	/**
	 * Text container for displaying scanning progress, and address of selected
	 * directory
	 */
	private JLabel addressLabel;

	/** Button for bringing up file chooser dialog */
	private JButton browseButton;

	/**
	 * Underlying model for the JTable that displays contents of selected
	 * directory
	 */
	private DefaultTableModel tableModel;

	/** Table that display migration results output */
	private JTable outputResultsTable;

	/** Allows outputResultsTable to be scrollable */
	private JScrollPane tablePane;

	/** Object containing non-GUI logic for program */
	private MusicOrganizer model;

	/** Width and height for the window */
	private int windowWidth = 800;
	private int windowHeight = 600;

	// Tracks the preffered table width
	private int tableWidth = 0;

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * Create a new GUI.
	 */
	public GUI() {
		// use small default size for low-res screens
		setSize(windowWidth, windowHeight);

		// set value for title bar of window
		setTitle("Music Organizer");

		// allows the program to exit when the window is closed
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		// Component listener to listen for resize events
		addComponentListener(this);

		// create window components
		initGUI();

		// make sure everything is visible
		validate();
		repaint();
		setVisible(true);
	}

	// -----------------------------------------------------------------------
	// Methods
	// -----------------------------------------------------------------------

	/**
	 * Create all the components to be displayed in the main window.
	 */
	private void initGUI() {
		// use standard BorderLayout for the window itself
		setLayout(new BorderLayout());

		// event handler for button clicks
		buttonHandler = new ButtonHandler();

		// create text label for displaying scanning progress and selected
		// directory
		addressLabel = new JLabel();

		// create Select Folder button
		browseButton = new JButton("Select Folder");
		browseButton.addActionListener(buttonHandler);

		// create panel for showing Browse button, scanning progress, and value
		// for the selected directory
		addressPanel = new JPanel();

		// ensure components are laid out from left to right
		addressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// add components to addressPanel
		addressPanel.add(browseButton);
		addressPanel.add(addressLabel);

		// create the table for displaying the results output
		createOutputTable();

		// make sure table is scrollable
		tablePane = new JScrollPane(outputResultsTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// add components to main window
		add(addressPanel, BorderLayout.NORTH);
		add(tablePane, BorderLayout.CENTER);
	}

	/**
	 * Create the table for displaying the migration output for the selected
	 * directory.
	 */
	private void createOutputTable() {
		// create underlying model for table that displays results output
		tableModel = new DefaultTableModel();

		// table model has 1 column to display the results output
		tableModel.addColumn("Output");

		// create GUI table component
		outputResultsTable = new JTable(tableModel);

		// Turn off auto resizing of the table
		outputResultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// disallow reordering of table columns
		outputResultsTable.getTableHeader().setReorderingAllowed(false);

		// create a TableCellRenderer for displaying left justified text
		DefaultTableCellRenderer leftJustifiedRenderer = new DefaultTableCellRenderer();
		leftJustifiedRenderer.setHorizontalAlignment(SwingConstants.LEFT);

		// create a TableCellRenderer for displaying right justified text
		DefaultTableCellRenderer rightJustifiedRenderer = new DefaultTableCellRenderer();
		rightJustifiedRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

		// set cell renderers for data cells
		outputResultsTable.getColumn("Output").setCellRenderer(
				leftJustifiedRenderer);

		// create and format headers for column that displays output
		JLabel nameLabel = new JLabel(" Output", SwingConstants.LEFT);
		nameLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

		outputResultsTable.getColumn("Output").setHeaderRenderer(
				new CustomTableCellRenderer());
		outputResultsTable.getColumn("Output").setHeaderValue(nameLabel);

	}

	/**
	 * Register the specified model with this GUI.
	 */
	public void registerModel(MusicOrganizer model) {
		this.model = model;
	}

	/**
	 * Return the absolute path of a directory selected by the user via a
	 * JFileChooser. Returns the string "cancelled" if the action is cancelled.
	 */
	public String getAbsoluteDirectoryPath() {
		// display file chooser dialog
		JFileChooser jfc = new JFileChooser();

		// only display directories (folders)
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// show the dialog
		int chosenOption = jfc.showOpenDialog(this);

		if (chosenOption == JFileChooser.APPROVE_OPTION) {
			// return the selected directory, if one is chosen; otherwise,
			// return null
			if (jfc.getSelectedFile() != null) {
				return jfc.getSelectedFile().getAbsolutePath();
			} else {
				return null;
			}
		} else {
			return "cancelled";
		}
	}

	/**
	 * Set the text of the address label.
	 */
	public void setAddressLabelText(String text) {
		addressLabel.setText(text);
	}

	/**
	 * Update the table with the specified output information. Each String
	 * occupies a single row in the table.
	 */
	public void updateListing(String output) {
		// add information in new row in table
		tableModel.addRow(new String[] { " " + output });

		// Resize the table based on the max row width
		for (int rowNumber = 0; rowNumber < outputResultsTable.getRowCount(); rowNumber++) {
			TableCellRenderer renderer = outputResultsTable.getCellRenderer(
					rowNumber, 0);
			Component comp = outputResultsTable.prepareRenderer(renderer,
					rowNumber, 0);
			tableWidth = Math.max(comp.getPreferredSize().width, tableWidth);
			outputResultsTable.getColumnModel().getColumn(0)
					.setMaxWidth(tableWidth);
			outputResultsTable.getColumnModel().getColumn(0)
					.setPreferredWidth(tableWidth);
		}
	}

	/**
	 * Clear the contents of the previous directory traversal, and clear the
	 * address showing the current directory.
	 */
	public void resetGUI() {
		// clear address
		addressLabel.setText("");

		// remove all rows from table
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}

		// Reset tableWidth to 0
		tableWidth = 0;
	}

	public void disableButton() {
		browseButton.setEnabled(false);
	}

	public void enableButton() {
		browseButton.setEnabled(true);
	}

	// -----------------------------------------------------------------------
	// Inner Classes
	// -----------------------------------------------------------------------

	/**
	 * Inner class for allowing customization of the appearance of a table cell.
	 * This class is used in this class to allow left or right justification of
	 * text in a table header cell, without losing the borders of those cells.
	 */
	class CustomTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return (JComponent) value;
		}
	}

	/**
	 * Inner class to handle button events.
	 */
	class ButtonHandler implements ActionListener {

		// -----------------------------------------------------------------------
		// Methods
		// -----------------------------------------------------------------------

		/**
		 * Respond to a button click event.
		 */
		public void actionPerformed(ActionEvent e) {
			JButton b = (JButton) e.getSource();

			if (b.getText().equals("Select Folder")) {
				// prompt user to select directory
				model.selectDirectory();
			}
		}
	}

	/**
	 * Resize the table's minimum and maximum widths according to the resized
	 * window
	 */
	@Override
	public void componentResized(ComponentEvent e) {

		windowWidth = e.getComponent().getWidth();
		int maxWidth = Math.max(windowWidth, tableWidth);
		outputResultsTable.getColumnModel().getColumn(0)
				.setMinWidth(windowWidth);
		outputResultsTable.getColumnModel().getColumn(0).setMaxWidth(maxWidth);

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

}