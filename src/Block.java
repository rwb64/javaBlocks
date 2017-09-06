/**
 * Represents the blocks in the 
 * @author Ralph Brewer
 * cs530
 *
 */
public class Block extends Thing {
	
	public Block(Square sq, BlocksGame g, char ch) {
		super(sq, g, ch);
	}
	public Block(Square sq, BlocksGame g, char ch, boolean isHidden) {
		super(sq, g, ch);
		
		setHidden(isHidden);
	}
	
	public String getImageName() {
		return "Box";
	}
}
