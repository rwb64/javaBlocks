/**
 * Square that looks like a block until it is pushed and then 
 * turns a color to show it is immovable.
 * @author Ralph Brewer
 * cs530
 *
 */
public class Cement extends Square {
	
	private String imgName;
	private boolean hidden;
	
	Cement(Location loc, BlocksGame g, char ch) {
		
		super(loc, g, ch);
		imgName = "Box";
		hidden = true;
	}
	
	public String getImageName() {
		return imgName;
	}
	
	public void setImageName(String name){
		imgName = name;
	}
	
	public boolean canEnter() {
		return canPush(0);
	}
	
	public boolean canPush(int dir) {
		if(hidden) {
			setImageName("Cement");
			hidden = false;
			drawSelf();
		}
		return false;
	}
	
}