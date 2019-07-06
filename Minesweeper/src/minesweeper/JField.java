package minesweeper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;

/**
 * Models a single field on the board.
 *
 * @author Filip Nemec
 */
public class JField extends JButton {
	
	/** Used for serialization. */
	private static final long serialVersionUID = 7542505804946504685L;
	
	/** Flag indicating whether this field is a mine. */
	public final boolean isMine;
	
	/** Flag indicating whether this field is currently flagged. */
	private boolean isFlagged;
	
	/** Flag indicating whether this field has been discovered. */
	private boolean isDiscovered;
	
	/** The row that this field is on. */
	public final int row;
	
	/** The column that this field is on. */
	public final int column;

	/** Reference to the mine field this field is on. */
	private JMineField mineField;
	
	/** The color of the undiscovered field. */
	private static final Color UNDISCOVERED = new Color(54, 171, 255);
	
	/** The color of the field with no neighbour mines. */
	private static final Color ZERO_NEIGHBOUR_MINES = new Color(32, 199, 32);
	
	/** The color of the field with any neighbour being a mine. */
	private static final Color ANY_NEIGHBOUR_IS_A_MINE = new Color(255, 241, 41);
	
	/**
	 * Creates a new field on the given board.
	 *
	 * @param mineField the mine-field this field is a part of
	 * @param isMine flag indicating whether this field is a mine
	 * @param row the row of this field
	 * @param column the column of this field
	 */
	public JField(JMineField mineField, boolean isMine, int row, int column) {
		this.mineField = mineField;
		this.isMine = isMine;
		this.row = row;
		this.column = column;
		
		setMargin(new Insets(0, 0, 0, 0));
		setFont(new Font("Arial", Font.PLAIN, 20));
		setBackground(UNDISCOVERED);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!isEnabled()) return;
				
				if(e.getButton() == MouseEvent.BUTTON1) {
					toggleField();
					
				} else if(e.getButton() == MouseEvent.BUTTON3) {
					toggleFlag();
					
				}
			}
		});
	}
	
	//------------------------------------------------------------
	//					  Discovering fields
	//------------------------------------------------------------
	
	/**
	 * Discovers this field, unless this field has already been
	 * discovered.
	 */
	public void toggleField() {
		if(isDiscovered || isFlagged) return;
		
		if(isMine) {
			explode();
		} else {
			discover();
		}
	}
	
	private void discover() {
		HashSet<JField> visited = new HashSet<>();
		LinkedList<JField> toCheck = new LinkedList<>();
		toCheck.add(this);
		
		while(!toCheck.isEmpty()) {
			JField field = toCheck.removeFirst();
			int mineCount = revealNonMine(field);
			
			if(mineCount == 0) {
				List<JField> neighbours = mineField.getFieldNeighbours(field.row, field.column);
				neighbours.removeIf(neighbour -> neighbour.isDiscovered || neighbour.isFlagged || visited.contains(neighbour));
				
				toCheck.addAll(neighbours);
				visited.addAll(neighbours);
			}
			
			notifyFieldOnDiscover(field);
		}
	}
	
	private void explode() {
		revealMine(true);
		notifyFieldOnExplode();
	}
	
	//------------------------------------------------------------
	//						Flagging fields
	//------------------------------------------------------------
	
	/**
	 * Toggles the flag on or off, based on the situation.
	 */
	public void toggleFlag() {
		if(isDiscovered) return;
		
		if(isFlagged) {
			removeFlag();
		} else {
			placeFlag();
		}
	}
	
	/**
	 * Places the flag on this field, unless this field has already
	 * been discovered.
	 */
	private void placeFlag() {
		isFlagged = true;
		setBackground(Color.lightGray);
		setText("F");
		
		notifyFieldOnFlagPlace();
	}
	
	/**
	 * Removes the flag from this field if this field has been flagged
	 * and has not yet been discovered.
	 */
	private void removeFlag() {
		isFlagged = false;
		setBackground(UNDISCOVERED);
		setText("");
		
		notifyFieldOnFlagRemove();
	}
	
	//------------------------------------------------------------
	//					After the player lost
	//------------------------------------------------------------
	
	/**
	 * Reveals the underlying field - it will either be a mine or not.
	 */
	public void reveal() {
		if(isDiscovered) return;
		
		if(isMine) {
			revealMine(false);
		} else {
			revealNonMine(this);
		}
	}
	
	/**
	 * Reveals the mine by changing the graphical user interface.
	 * 
	 * @param exploding if this is the mine that the player "stepped" on
	 */
	private void revealMine(boolean exploding) {
		isDiscovered = true;
		setEnabled(false);
		setBackground(exploding ? Color.red : Color.black);
		setText("M");
	}
	
	/**
	 * Reveals the non-mine field by changing the graphical user interface
	 * and returns the number of mines found in the neighbouring fields.
	 *
	 * @param field the field to be revealed
	 * @return the number of mines found in the neighbouring fields
	 */
	private int revealNonMine(JField field) {
		field.setEnabled(false);
		field.isDiscovered = true;
		
		int mineCount = mineField.getMineCountAround(field.row, field.column);
		
		if(mineCount == 0) {
			field.setBackground(ZERO_NEIGHBOUR_MINES);
			field.setText("");
		} else {
			field.setBackground(ANY_NEIGHBOUR_IS_A_MINE);
			field.setText(String.valueOf(mineCount));
		}
		
		return mineCount;
	}
	
	//------------------------------------------------------------
	//					Notifying the field
	//------------------------------------------------------------
	
	private void notifyFieldOnDiscover(JField source) {
		mineField.onDiscover(source);
	}
	
	private void notifyFieldOnExplode() {
		mineField.onExplode(this);
	}
	
	private void notifyFieldOnFlagPlace() {
		mineField.onFlagPlace(this);
	}
	
	private void notifyFieldOnFlagRemove() {
		mineField.onFlagRemove(this);
	}
	
	//------------------------------------------------------------
	//						  toString
	//------------------------------------------------------------
	
	@Override
	public String toString() {
		return "(" + column + ", " + row + ", " + isDiscovered + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(column, row);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof JField))
			return false;
		JField other = (JField) obj;
		return column == other.column && row == other.row;
	}
}