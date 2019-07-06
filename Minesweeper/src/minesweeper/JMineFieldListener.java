package minesweeper;

/**
 * Models objects that listen to the changes on the
 * {@code JMineField}.
 *
 * @author Filip Nemec
 */
public interface JMineFieldListener {
	
	/**
	 * Invoked every time the {@code JMineField} changes.
	 *
	 * @param field the <i>Subject</i> - the field
	 */
	void onMineFieldChange(JMineField field);
	
	/**
	 * Invoked once the player steps on the mine.
	 */
	void onExplode();
}
