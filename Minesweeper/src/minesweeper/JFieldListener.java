package minesweeper;

/**
 * Models objects that keep track of the field events.
 *
 * @author Filip Nemec
 */
public interface JFieldListener {

	/**
	 * Triggered once the field has been discovered.
	 *
	 * @param field the field
	 */
	void onDiscover(JField field);
	
	/**
	 * Triggered every time the flag gets placed on the field.
	 *
	 * @param field the field
	 */
	void onFlagPlace(JField field);
	
	/**
	 * Triggered every time the flag gets removed from the field.
	 *
	 * @param field the field
	 */
	void onFlagRemove(JField field);
	
	/**
	 * Triggered if the field was a mine and player clicked on it.
	 *
	 * @param field the field
	 */
	void onExplode(JField field);
}
