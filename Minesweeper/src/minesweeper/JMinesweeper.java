package minesweeper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Models the <i>Minesweeper</i> graphical user interface.
 *
 * @author Filip Nemec
 */
@SuppressWarnings("serial")
public class JMinesweeper extends JFrame implements JMineFieldListener {
	
	/** Displays the number of used flags and the number of required flags. */
	private JLabel flagsUsedLabel;
	
	/** Displays the number of discovered fields and the number of total fields. */
	private JLabel fieldsDiscoveredLabel;
	
	/**
	 * Constructs and starts a new <i>Minesweeper</i> game.
	 *
	 * @param rows the number of rows in the mine-field
	 * @param columns the number of columns in the mine-field
	 * @param mines the number of mines in the mine-field
	 */
	public JMinesweeper(int rows, int columns, int mines) {
		setTitle("Nature Minesweeper");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int cellSize, width, height;
		
		if(rows > columns) {
			cellSize = (int) ((dim.height / rows) * 0.9);
			width = columns * cellSize;
			height = rows * cellSize;
			
		} else if(rows == columns) {
			width = height = (int) ((dim.height) * 0.9);
			
		} else {
			cellSize = (int) ((dim.width / columns) * 0.8);
			width = columns * cellSize;
			height = rows * cellSize;
		}
		
		setSize(width, height);
		setLocation((dim.width - width) / 2, (dim.height - height) / 2);
		
		
		// GUI
		Container pane = getContentPane();
		
		JPanel infoPanel = new JPanel(new GridLayout(1, 0));
			infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			infoPanel.add(flagsUsedLabel = new JLabel("Flags used: 0 / " + mines, JLabel.CENTER));
			infoPanel.add(fieldsDiscoveredLabel = new JLabel("Fields used: 0 / " + (rows * columns), JLabel.CENTER));
			pane.add(infoPanel, BorderLayout.NORTH);
		
		JMineField field = new JMineField(rows, columns, mines);
			field.addJMineFieldListener(this);
			pane.add(field, BorderLayout.CENTER);
	}
	
	@Override
	public void onMineFieldChange(JMineField field) {
		int fieldsUsed = field.getDiscoveredCount() + field.getFlagCount();
		fieldsDiscoveredLabel.setText("Fields used: " + fieldsUsed + " / " + field.getFieldCount());
		flagsUsedLabel.setText("Flags used: " + field.getFlagCount() + " / " + field.getMineCount());
		
		if(fieldsUsed == field.getFieldCount()) {
			JOptionPane.showMessageDialog(this, "You won!");
		}
	}
	
	@Override
	public void onExplode() {
		JOptionPane.showMessageDialog(this, "Game over!");
	}
}
