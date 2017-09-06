/**
 * Goals for the blocks
 * @author Ralph
 *
 */
public class Goal extends Square {
	public Goal(Location loc, BlocksGame g, char ch) {
		super(loc, g, ch);
	}
	
	public String getImageName() {
		return "Goal";
	}
}
