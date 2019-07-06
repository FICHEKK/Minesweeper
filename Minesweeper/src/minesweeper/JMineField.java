package minesweeper;

import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

/**
 * A component that models the mine-field.
 *
 * @author Filip Nemec
 */
public class JMineField extends JComponent implements JFieldListener {
	
	/** Used for serialization. */
	private static final long serialVersionUID = 9001083447800006171L;

	/** The fields of this board. */
	private JField[][] fields;
	
	/** The number of rows. */
	public final int ROWS;
	
	/** The number of columns. */
	public final int COLUMNS;
	
	/** The total number of fields. */
	public final int FIELD_COUNT;
	
	/** The number of mines on this board. */
	private int mineCount;
	
	/** The number of flags on the board. */
	private int flagCount;
	
	/** The number of discovered fields on this board. */
	private int discoveredCount;
	
	/** A list of all the listeners. */
	private List<JMineFieldListener> listeners = new LinkedList<>();
	
	/**
	 * Constructs a new board.
	 *
	 * @param rows the number of rows
	 * @param columns the number of columns
	 * @param mineCount the number of mines
	 */
	public JMineField(int rows, int columns, int mineCount) {
		this.ROWS = rows;
		this.COLUMNS = columns;
		this.FIELD_COUNT = ROWS * COLUMNS;
		
		this.fields = new JField[rows][columns];
		this.mineCount = mineCount;
		
		createFields();
	}

	private void createFields() {
		this.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		this.setLayout(new GridLayout(ROWS, COLUMNS));
		
		boolean[][] mines = generateMines();
		
		for(int r = 0; r < ROWS; r++) {
			for(int c = 0; c < COLUMNS; c++) {
				this.add( fields[r][c] = new JField(this, mines[r][c], r , c) );
			}
		}
	}
	
	private boolean[][] generateMines() {
		boolean[][] mines = new boolean[ROWS][COLUMNS];
		
		for(int i = 0; i < mineCount; i++) {
			int row = i / COLUMNS;
			int column = i % COLUMNS;
			mines[row][column] = true;
		}
		
		shuffleMines(mines);
		
		return mines;
	}
	
	/**
	 * Randomly shuffles mine positions.
	 *
	 * @param mines the mines to be shuffled
	 */
	private void shuffleMines(boolean[][] mines) {
	    Random random = new Random();

	    for (int i = mines.length - 1; i > 0; i--) {
	        for (int j = mines[i].length - 1; j > 0; j--) {
	            int m = random.nextInt(i + 1);
	            int n = random.nextInt(j + 1);

	            boolean temp = mines[i][j];
	            mines[i][j] = mines[m][n];
	            mines[m][n] = temp;
	        }
	    }
	}
	
	//---------------------------------------------------------
	//				  		Public API
	//---------------------------------------------------------
	
	/**
	 * Returns the number of mines around the field on the given
	 * {@code row} and {@code column}.
	 *
	 * @param row the row of the field
	 * @param column the column of the field
	 * @return the number of mines around the specified field 
	 */
	public int getMineCountAround(int row, int column) {
		int mineCount = 0;
		
		for(int y = -1; y <= 1; y++) {
			for(int x = -1; x <= 1; x++) {
				int r = row + y;
				int c = column + x;
				
				if(r < 0 || r >= ROWS) continue;
				if(c < 0 || c >= COLUMNS) continue;
				
				if(fields[r][c].isMine) mineCount++;
			}
		}
			
		return mineCount;
	}
	
	/**
	 * Returns all of the neighbour fields around the given field.
	 *
	 * @param row the specified field's row
	 * @param column the specified field's column
	 * @return a list of all the neighbour fields around the given field
	 */
	public List<JField> getFieldNeighbours(int row, int column) {
		List<JField> neighbours = new LinkedList<>();
		
		for(int y = -1; y <= 1; y++) {
			for(int x = -1; x <= 1; x++) {
				if(x == 0 && y == 0) continue;
				
				int r = row + y;
				int c = column + x;
				
				if(r < 0 || r >= ROWS) continue;
				if(c < 0 || c >= COLUMNS) continue;
				
				neighbours.add(fields[r][c]);
			}
		}
		
		return neighbours;
	}
	
	/**
	 * Returns the field at the given {@code row} and {@code column}.
	 *
	 * @param row the row of the field
	 * @param column the column of the field
	 * @return the field at the given {@code row} and {@code column}
	 */
	public JField getField(int row, int column) {
		return fields[row][column];
	}
	
	//---------------------------------------------------------
	//				  		  Getters
	//---------------------------------------------------------
	
	/**
	 * @return the number of mines on this field
	 */
	public int getMineCount() {
		return mineCount;
	}
	
	/**
	 * @return the number of flags on this field
	 */
	public int getFlagCount() {
		return flagCount;
	}
	
	/**
	 * @return the number of discovered fields on this field
	 */
	public int getDiscoveredCount() {
		return discoveredCount;
	}
	
	/**
	 * @return the total number of fields on this field
	 */
	public int getFieldCount() {
		return FIELD_COUNT;
	}
	
	//---------------------------------------------------------
	//				  Field listener methods
	//---------------------------------------------------------

	@Override
	public void onDiscover(JField source) {
		++discoveredCount;
		notifyListenersMineFieldChanged();
	}

	@Override
	public void onFlagPlace(JField source) {
		++flagCount;
		notifyListenersMineFieldChanged();
	}

	@Override
	public void onFlagRemove(JField source) {
		--flagCount;
		notifyListenersMineFieldChanged();
	}

	@Override
	public void onExplode(JField source) {
		for(int r = 0; r < ROWS; r++) {
			for(int c = 0; c < COLUMNS; c++) {
				fields[r][c].reveal();
			}
		}
		
		notifyListenersOnExplode();
	}
	
	//---------------------------------------------------------
	//				  	Listener registration
	//---------------------------------------------------------
	
	/**
	 * Adds a new listener to this mine-field.
	 *
	 * @param l the listener to be added
	 */
	public void addJMineFieldListener(JMineFieldListener l) {
		listeners.add(l);
	}
	
	/**
	 * Removes the given listener from this mine-field.
	 *
	 * @param l the listener to be removed
	 */
	public void removeJMineFieldListener(JMineFieldListener l) {
		listeners.remove(l);
	}
	
	private void notifyListenersMineFieldChanged() {
		listeners.forEach(l -> l.onMineFieldChange(this));
	}
	
	private void notifyListenersOnExplode() {
		listeners.forEach(l -> l.onExplode());
	}
}
