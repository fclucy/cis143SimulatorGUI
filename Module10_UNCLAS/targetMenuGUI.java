package Module10_UNCLAS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class targetMenuGUI extends JFrame {
	private JComboBox<File> fileComboBox;
	private JComboBox<String> submarineComboBox;
	private JButton confirmButton;
	private double selectedDepth = -1;

	public targetMenuGUI() {

		//STIG example: https://web.archive.org/web/20250208205102/https://prhome.defense.gov/Portals/52/Documents/MRA_Docs/MPP/OEPM/O6B%20Screen%20Shots%20Access%20DMDC%20DSS%20Ver%202%2026Oct2012.pdf?ver=2018-01-30-213607-083
		showWarningDialog(); // Show terms dialog before proceeding

		setTitle("Select Target Depth");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 400);
		setLayout(new BorderLayout());

		// === Classification Banner ===
		//STIG example: https://web.archive.org/web/20250422132041/https://cyberintelsystems.com/classification-banner/
        JLabel classificationBanner = new JLabel("UNCLASSIFIED", SwingConstants.CENTER);
        classificationBanner.setOpaque(true);
        classificationBanner.setBackground(new Color(0, 128, 0)); // Dark green
        classificationBanner.setForeground(Color.WHITE);
        classificationBanner.setFont(new Font("SansSerif", Font.BOLD, 18));
        classificationBanner.setPreferredSize(new Dimension(getWidth(), 40));
        add(classificationBanner, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Load CSV files
		File[] csvFiles = new File("./log").listFiles((dir, name) -> name.endsWith(".csv"));
		if (csvFiles == null || csvFiles.length == 0) {
			JOptionPane.showMessageDialog(this, "No CSV files found in ./log folder.");
			System.exit(1);
		}

		// File chooser combo box
		fileComboBox = new JComboBox<>(csvFiles);
		fileComboBox.addActionListener(e -> loadSubmarineOptions());
		panel.add(new JLabel("Choose a CSV file:"));
		panel.add(fileComboBox);

		// Submarine depth selector
		submarineComboBox = new JComboBox<>();
		panel.add(new JLabel("Choose a submarine average depth:"));
		panel.add(submarineComboBox);

		// Confirm button
		confirmButton = new JButton("Confirm Selection");
		confirmButton.addActionListener(e -> handleConfirm());

		add(panel, BorderLayout.CENTER);
		add(confirmButton, BorderLayout.SOUTH);

		// Pre-load the first file
		loadSubmarineOptions();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void showWarningDialog() {
		String message = """
				           You are accessing a U.S. Government (USG) Information System (IS) that is provided for USG-authorized use only.

				By using this IS (which includes any device attached to this IS), you consent to the following conditions:

				-The USG routinely intercepts and monitors communications on this IS for purposes including, but not limited to, penetration testing, COMSEC monitoring, network operations and defense, personnel misconduct (PM), law enforcement (LE), and counterintelligence (CI) investigations.

				-At any time, the USG may inspect and seize data stored on this IS.

				-Communications using, or data stored on, this IS are not private, are subject to routine monitoring, interception, and search, and may be disclosed or used for any USG-authorized purpose.

				-This IS includes security measures (e.g., authentication and access controls) to protect USG interests--not for your personal benefit or privacy.

				-Notwithstanding the above, using this IS does not constitute consent to PM, LE or CI investigative searching or monitoring of the content of privileged communications, or work product, related to personal representation or services by attorneys, psychotherapists, or clergy, and their assistants. Such communications and work product are private and confidential. See User Agreement for details.
				        """;

		// The dimension width of 300 pixels was chosen since that conforms with the average mobile screen size, allowing the application to be adapted for mobile use.
		int response = JOptionPane.showOptionDialog(null,
				"<html><body><p style='width: 300px;'>" + message + "</p></body></html>", "Simulation Warning & Terms",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] { "Accept", "Decline" },
				"Accept");

		if (response != JOptionPane.YES_OPTION) {
			System.exit(0); // Exit if the user declines
		}
	}

	private void loadSubmarineOptions() {
		submarineComboBox.removeAllItems();
		File selectedFile = (File) fileComboBox.getSelectedItem();
		if (selectedFile == null)
			return;

		try {
			List<String> lines = Files.readAllLines(selectedFile.toPath());
			String averageLine = lines.stream().filter(line -> line.startsWith("Average")).findFirst().orElse(null);

			if (averageLine == null) {
				JOptionPane.showMessageDialog(this, "No 'Average' row found in the selected file.");
				return;
			}

			String[] values = averageLine.split(",");
			if (values.length < 13) {
				JOptionPane.showMessageDialog(this, "Malformed 'Average' row.");
				return;
			}

			for (int i = 1; i <= 12; i++) {
				submarineComboBox.addItem("Submarine " + i + ": " + values[i]);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
		}
	}

	private void handleConfirm() {
		int selectedIndex = submarineComboBox.getSelectedIndex() + 1;
		File selectedFile = (File) fileComboBox.getSelectedItem();
		if (selectedFile == null || selectedIndex < 1 || selectedIndex > 12) {
			JOptionPane.showMessageDialog(this, "Invalid selection.");
			return;
		}

		try {
			List<String> lines = Files.readAllLines(selectedFile.toPath());
			String averageLine = lines.stream().filter(line -> line.startsWith("Average")).findFirst().orElse(null);

			if (averageLine != null) {
				String[] values = averageLine.split(",");
				selectedDepth = Double.parseDouble(values[selectedIndex]);
				JOptionPane.showMessageDialog(this, "Selected depth: " + selectedDepth);
				dispose(); // Close window
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error parsing selected depth: " + e.getMessage());
		}
	}

	public double getSelectedDepth() {
		return selectedDepth;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new targetMenuGUI());
	}
}