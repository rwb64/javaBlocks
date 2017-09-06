
import java.awt.event.KeyEvent;
/**
 * Handles the key pushes 
 * @author Ralph Brewer
 * cs530
 *
 */
public class Command {
	public static final int Error = -1;
	public static final int Directional = 0;
	public static final int Jump = 1;

	public static final int Quit = 2;
	public static final int Next = 4;
	public static final int Replay = 5;
	public static final int Home = 6;
	private Move move;
	private Location goal;
	private int type;

	Command(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_KP_UP:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_KP_DOWN:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_KP_RIGHT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_KP_LEFT:
		case KeyEvent.VK_LEFT:
			this.type = Directional;
			this.move = new Move(Move.keyCodeToDirection(keyCode), false);
			break;
		case KeyEvent.VK_R:
			this.type = Replay;
			break;
		case KeyEvent.VK_N:
			this.type = Next;
			break;
		// will advance to next level
		case KeyEvent.VK_Q:
			this.type = Quit;
			break;
		case KeyEvent.VK_H:
			this.type = Home;
			break;
		// will quit game
		default:
			this.type = Error;
			break;
		}
	}

	public Location getGoal() {
		return goal;
	};

	public Move getMove() {
		return move;
	};

	public int getType() {
		return type;
	};

}
