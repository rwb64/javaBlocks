/**
 * Immovable object that defines the working area the avatar has to work in
 * @author Ralph Brewer
 * cs530
 *
 */
public class Wall extends Square {
	public Wall(Location loc, BlocksGame g, char ch) {
		super(loc, g, ch);
	}
	
	public String getImageName() {
		return "Wall";
	}
	
	public boolean canEnter() {
		return false;
	}
}
