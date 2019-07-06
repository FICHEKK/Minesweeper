package minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Models the game set-up window.
 *
 * @author Filip Nemec
 */
@SuppressWarnings("serial")
public class JSetup extends JFrame {
	
	/** The text-field for inputting the number of rows. */
	private JTextField tfRows;
	
	/** The text-field for inputting the number of columns. */
	private JTextField tfColumns;
	
	/** The text-field for inputting the number of mines. */
	private JTextField tfMines;
	
	/**
	 * Constructs a new <i>Minesweeper</i> setup window.
	 */
	public JSetup() {
		setTitle("Minesweeper Setup");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		Container pane = getContentPane();
		
		JLabel title = new JLabel("Setup", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 24));
		title.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		pane.add(title, BorderLayout.NORTH);
		
		JPanel setupPanel = new JPanel(new GridLayout(0, 2));
		setupPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		setupPanel.add(new JLabel("Number of rows:"));
		setupPanel.add(tfRows = new JTextField());
		setupPanel.add(new JLabel("Number of columns:"));
		setupPanel.add(tfColumns = new JTextField());
		setupPanel.add(new JLabel("Number of mines:"));
		setupPanel.add(tfMines = new JTextField());
		
		pane.add(setupPanel, BorderLayout.CENTER);
		
		play.putValue(Action.NAME, "Play!");
		JButton playButton = new JButton(play);
		playButton.setBackground(Color.orange);
		playButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pane.add(playButton, BorderLayout.SOUTH);
		
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
	}
	
	/**
	 * Validates the user's setup and starts the new game
	 * if the setup was valid.
	 */
	private Action play = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int rows = Integer.parseInt(tfRows.getText());
				int columns = Integer.parseInt(tfColumns.getText());
				int mines = Integer.parseInt(tfMines.getText());
				
				if(rows < 4 || rows > 32) {
					JOptionPane.showMessageDialog(JSetup.this, "Number of rows must be from 4 to 32!");
					return;
				}
				
				if(columns < 4 || columns > 32) {
					JOptionPane.showMessageDialog(JSetup.this, "Number of columns must be from 4 to 32!");
					return;
				}
				
				if(mines >= rows * columns) {
					JOptionPane.showMessageDialog(JSetup.this, "Number of mines must be lesser than the number of fields!\r\n" +
												  "For your current configuration, maximum number of mines is " + (rows * columns - 1) + ".");
					return;
				}
				
				new JMinesweeper(rows, columns, mines).setVisible(true);
				
			} catch(NumberFormatException ex) {
				JOptionPane.showMessageDialog(JSetup.this, "Invalid input!");
				
			}
		}
	};
	
	/**
	 * Program starts from here.
	 *
	 * @param args none are used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new JSetup().setVisible(true);
		});	
	}
}
