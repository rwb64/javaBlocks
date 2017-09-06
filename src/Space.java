/**
 * Space is a filler for a square outside the bounds of the walls to 
 * fill out a rectangular grid.
 * 
 * @author Ralph Brewer
 * cs530
 *
 */
public class Space extends Square {
	public Space(Location loc, BlocksGame g, char ch) {
		super(loc, g, ch);
	}
	
	public String getImageName() {
		return "Space";
	}
	
	public boolean canEnter() {
		return false;
	}
}
