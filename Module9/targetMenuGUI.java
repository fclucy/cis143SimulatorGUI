package Module9;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class targetMenuGUI extends JFrame {
    private JComboBox<File> fileComboBox;
    private JComboBox<String> submarineComboBox;
    private JButton confirmButton;
    private double selectedDepth = -1;

    public targetMenuGUI() {
    	
        setTitle("Select Target Depth");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

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

    private void loadSubmarineOptions() {
        submarineComboBox.removeAllItems();
        File selectedFile = (File) fileComboBox.getSelectedItem();
        if (selectedFile == null) return;

        try {
            List<String> lines = Files.readAllLines(selectedFile.toPath());
            String averageLine = lines.stream()
                    .filter(line -> line.startsWith("Average"))
                    .findFirst()
                    .orElse(null);

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
            String averageLine = lines.stream()
                    .filter(line -> line.startsWith("Average"))
                    .findFirst()
                    .orElse(null);

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