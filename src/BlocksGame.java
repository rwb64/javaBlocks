import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;

/**
 * Originally was where main was when it was an application. Changed to using 
 * threads and applet using the Display class.
 * 
 * @author Ralph
 *
 */
public class BlocksGame{
	public static final boolean VERBOSE = false;
	private final static String alphabet="abcdefghijklmnopqrstuvwxyz";
	
	/**
	 * Maximum allowed number of undo moves. Set to -1 for unlimited.
	 */
	public static final int maximumAllowedUndos = 100;


	private Vector<Move> undoMoveHistory = new Vector<Move>();

	private int totalMoves = 0;
	private Display display;
	private Man man;
	private int vacantSlots = 0;
	
	//list of moves read in from file
	private List<String> moveList;

	//Whether the level is over.
	private boolean levelOver;
	private int level = 0;
	private int replayNum = 0;
	private boolean replayLevel = false;
	private boolean hitNext = false;
	private boolean gameQuit = false;
	private boolean goHome = false;
	
	private GameType gameType;
	
	private Grid squares;
	public Tracking track;
	
	public enum GameType {
		REGULAR_GAME,
		UNLABELED_GAME,
		IRRELEVANT_GAME,
		IMMOVABLE_GAME
	};

	public BlocksGame(Display display)
	{
		this.display = display;
		man = null;
	}
	
	/**
	 * Add move to list of all moves from the level
	 * @param move
	 */
	public void addMove(Move move)
	{
		undoMoveHistory.add(move);
		if (undoMoveHistory.size() > maximumAllowedUndos || maximumAllowedUndos == -1)
		{
			undoMoveHistory.remove(0);
		}
		totalMoves++;
		
		if (BlocksGame.VERBOSE)
		{
			System.out.println("Total moves: " + totalMoves);
		}
	}
	
	/**
	 * Undo move - removed command for this as we want to make sure we 
	 * capture all commands 
	 */
//	public void undoLastMove()
//	{
//		if (undoMoveHistory.size() > 0)
//		{
//			man.doMove(undoMoveHistory.remove(undoMoveHistory.size() - 1));
//		}
//		
//		totalMoves++;
//	}
	
	/**
	 * 
	 * @return number of goals remaining in the level
	 */
	public int vacantSlots()
	{
		return this.vacantSlots;
	}

	/**
	 * Decrease the total of open goals when a goal is covered by a correct block
	 */
	public void decrementSlots() {
		vacantSlots--;
		if (vacantSlots == 0)
		{
			levelOver = true;
			
			display.drawStatusMessage("You completed level " + level++ );
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException ie)
			{
			}
		}
	}
	/**
	 * Open file and parse each move.
	 * @param file - file to read from
	 */
	public void openFileForRead(String file){
				
		gameType = GameType.REGULAR_GAME;
		
		moveList = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		    //Skip the header "X,Y,dir,timestamp,level,condition"
		    br.readLine();
		    
		    String[] line1 = br.readLine().split(",");
		    
		    moveList.add(line1[2]);
		    level = Integer.parseInt(line1[4]);
		    gameType = GameType.valueOf(line1[5]);
		    
		    while ((line = br.readLine()) != null) {
		    	if(line.split(",").length > 2)
		    		moveList.add(line.split(",")[2]);		    	
		    }
		    
		}catch (IOException ioe){
			errorMessage("Exception: " + ioe);
		}
    }
	
	/**
	 * Write all the moves from the completed level
	 * @param dir - directory in which to save file
	 * @param level - level number to save
	 */
	public void openFileForWrite(String dir, int level){
		
		String filename;
		
		filename = dir + "/" + level + (replayLevel ? alphabet.charAt(replayNum - 1) : "") + gameType.toString() + ".csv";
		
        try (PrintWriter out = new PrintWriter(filename)){
            			
            out.write("X,Y,direction,timestamp,level,condition" + System.lineSeparator());
            
            Location loc = track.getStartingLocation();
            
            Object[] moves = track.getMoves();
            
            for(int i = 0; i < track.getCurrentMove(); ++i) {
            	out.write(loc + "," + ((Move)moves[i]).getDirection() + "," + track.getTimeStamp(i) + ",");
            	out.write(level + "," + gameType.toString() + System.lineSeparator());
            	loc = loc.adjacentLocation(((Move)moves[i]).getDirection());
        	}
            out.write(loc + System.lineSeparator());
            out.flush();
            out.close();
            
        } catch (IOException ioe) {
        	errorMessage( "Exception: " + ioe); 
        }
    }
	
	/**
	 * Write out to a file where all the blocks are at a given move
	 * @param dir - directory to write to 
	 * @param level - level that we are playing
	 */
	public void openFileWriteBlocks(String dir, int level){
		
		String filename;
		
		filename = dir + "/blocks" + level + (replayLevel ? alphabet.charAt(replayNum - 1) : "") + gameType.toString() + ".csv";
		
		Location[][] obj = track.getGrid();
		
        try (PrintWriter out = new PrintWriter(filename)){
            
            for(int i = 0; i < track.getNumBoxes(); ++i) {
             	if(gameType == GameType.IRRELEVANT_GAME && i == track.getNumBoxes() - 1)
            		out.write("irrelevantBoxX, irrelevantBoxY, irrelevantBoxMoved,");
             	else
             		out.write("box" + i + "x,box" + i + "y,box" + i + "moved,");
            }
            out.write("timestamp,level,condition" + System.lineSeparator());
            
            for (int move = 0; move <= track.getCurrentMove(); move++)
			{
            	for (int box = 0; box < track.getNumBoxes(); box++)
            	{
					out.write(obj[move][box].toString() + ",");
					if(move < track.getCurrentMove())
						out.write((obj[move][box] == obj[move+1][box]) ? "0," : "1,");
					else
						out.write("0,");
				}
            	out.write((move < track.getCurrentMove() ? track.getTimeStamp(move) : "" ) + "," + level + "," + gameType.toString() + System.lineSeparator());
			}
			

        } catch (IOException ioe) {
        	errorMessage( "Exception: " + ioe); 
        }
    }

	public void drawAtLocation(String name,char ch, Location loc) {
		display.drawAtLocation(name, ch, loc);
	}

	public boolean inBounds(Location location) {
		return squares.inBounds(location);
	}

	public void incrementSlots() {
		vacantSlots++;
	}
	
//	private void clearRunDir(Object directory){
//		Files.cleanDirectory(directory); 
//	}
//	

	/**
	 * Runs one "Round" of the game starting at chosenLevel
	 * @param numLevels - number of levels to play
	 * @param dir - pathname of directory to save game data in
	 * @param gameType - determines which "round" to play, defaults to regular
	 */
	public void play(int numLevels, String dir, GameType gt) {
		level = 0;
		
		gameType = gt;
		
		while (level < numLevels && !gameQuit) {
			
			levelOver = false;
			vacantSlots = 0;
			readLevelFileForLevel(level);
			while (!levelOver) {
				processSingleCommand(display.getCommandFromUser());
			}
			if(goHome){
				goHome = false;
				display.setPlaySwitch(1);
				break;
			}
			
			if(!gameQuit && !goHome && !hitNext){
				if(!replayLevel){
					int writelevel = level - 1;
					openFileForWrite(dir, writelevel);
					openFileWriteBlocks(dir, writelevel);
					replayNum = 0;
				}else {
					int currentLevel = level;
					openFileForWrite(dir, currentLevel);
					openFileWriteBlocks(dir, currentLevel);
					replayLevel = false;

				}
			}
			hitNext = false;
		} if(gameQuit)
			display.playBool = false;
	}
	
	public void play(int chosenLevel, String dir) {
		play(chosenLevel, dir, GameType.REGULAR_GAME);
	}
	/**
	 * Reruns a level based on the moves from a file written from a user.
	 * @param levelFile - filename of the level to view
	 */
	public void watchRun(String levelFile) {

		levelOver = false;
		vacantSlots = 0;
		
		openFileForRead(levelFile);
		readLevelFileForLevel(level);
		
		while (!levelOver) {
			
			display.addToTrail(man.getSquare().getLocation());
						
			for(int i = 0; i < moveList.size(); i++){
				//processSingleCommand(display.getCommandFromUser());
				
				man.move(new Move(Integer.valueOf(moveList.get(i)), false));
				
				display.addToTrail(man.getSquare().getLocation());

				try
				{
					Thread.sleep(250);
				}
				catch (InterruptedException ie)
				{
					errorMessage("Exception: " + ie);
				}
			}
			levelOver = true;
		}
	}

	/**
	 * Process a command
	 * @param cmd - determined by keys or button pushes
	 */
	private void processSingleCommand(Command cmd)
	{
		switch (cmd.getType())
		{
			case Command.Quit:
				if(JOptionPane.showConfirmDialog(display, "Are you sure you want to quit?", "Confirm Quit", JOptionPane.YES_NO_OPTION)
						== JOptionPane.YES_OPTION) {
					quit();
				}
				return;
			case Command.Next:
				levelOver = true;
				level++;
				hitNext = true;
				break;
			case Command.Replay:
				levelOver = true;
				replayNum++;
				replayLevel = true;
				break;
			case Command.Home:
				levelOver = true;
				goHome = true;
				break;
			case Command.Directional:
				if(track != null)
					track.setNextMove(cmd.getMove());
				man.move(cmd.getMove());
				if(track != null)
					track.copyLocationsNextMove();
				break;
//			case Command.Undo:
//				undoLastMove();
//				break;
			case Command.Error:
				break;
			default:
				break;
		}
	}
	
	//quit playing
	public void quit() {
		levelOver = true;
		gameQuit = true;
		display.setPlaySwitch(6);
	}
	
	/**
	 * Reads the configuration file. The files are assumed to be stored in a
	 * subdirectory "Levels" of the current directory. The level filenames should
	 * be "Level0.data", "Level1.data" and so on. The first two lines of the
	 * file identify how many rows and columns are in the particular level. The
	 * third line is the number of boxes with associated goals.
	 */
	private void readLevelFileForLevel(int level)
	{

		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("Levels/Level" + level + ".data");
			BufferedReader in = new BufferedReader(new InputStreamReader(is));)
		{			
			
			int numRows = Integer.valueOf(in.readLine().trim()).intValue();
			int numCols = Integer.valueOf(in.readLine().trim()).intValue();
			int numBoxes =  Integer.valueOf(in.readLine().trim()).intValue();
			
			track = new Tracking(numBoxes + (gameType == GameType.IRRELEVANT_GAME ? 1 : 0));
			
			squares = new Grid(numRows, numCols);
			display.configureForSize(numRows, numCols);
			display.drawStatusMessage("Loading level " + level + "...");
			for (int row = numRows - 1; row >= 0; row--)
			{
				for (int col = 0; col < numCols; col++)
				{
					readOneSquare(new Location(row, col), (char) in.read());
				}

				// Skip over newline at end of row
				in.readLine();
			}
			
			for (int row = 0; row < numRows; row++) {
				for ( int col = 0; col < numCols; col++) {
					
					Square sq = squareAt(new Location(row, col));
					
					switch (gameType) {
					case UNLABELED_GAME:
						
						if(col >= numCols / 2) {
							swapSquares(sq, squareAt(new Location(row, numCols - col - 1)));
						}
						

						if(sq.getContents() instanceof Block) {
							sq.getContents().setHidden(true);
						}
					case REGULAR_GAME:
						if(sq instanceof Cement) {
							sq = new Square(sq.getLocation(), this, '\0');
							squares.setElementAt(sq.getLocation(), sq);
						}
						break;
						

					case IRRELEVANT_GAME:
						
						if(row >= numRows / 2) {
							swapSquares(sq, squareAt(new Location(numRows - row - 1, col)));
						}
						
						if(sq.getContents() instanceof Block) {
							sq.getContents().setHidden(true);
						}
						else if(sq instanceof Cement) {
							sq = new Square(sq.getLocation(), this, '\0');
							sq.addContents(new Block(sq, this, '\0'));
							squares.setElementAt(sq.getLocation(), sq);
							
							//Use numBoxes as an unused valid value for the irrelevant box
							track.setElementAt(sq.getLocation(), numBoxes);
						}
						break;
						
					case IMMOVABLE_GAME:

						if(sq.getContents() instanceof Block) {
							sq.getContents().setHidden(true);
						}
						
						if(row > numRows - col - 1 || (row == numRows - col - 1 && row >= numRows / 2)) {
							
							swapSquares(sq, squareAt(new Location(numRows - row - 1, numCols - col - 1)));
							
							sq = squareAt(new Location(row, col));
							
							if(sq.getContents() instanceof Block) {
								sq.getContents().setHidden(true);
							}
							
						}
						
					default:
						break;
					}
					sq.drawSelf();
				}
			}
			display.drawStatusMessage("Loaded level " + level + "...");
			display.setVisible(true);
			display.grabFocus();
			
			undoMoveHistory.clear();
			totalMoves = 0;
			
			track.setMan(man);
			track.startGame(squares);
		}
		catch (IOException e)
		{
			errorMessage("Error reading file: Levels/Level" + level + ".data, " + e);
			return;
		}
	}

	/**
	 * Given the character just read from the Level file and that location 
	 * that it represents, this method configures that square in the grid
	 * to have the proper contents.
	 */
	private void readOneSquare(Location location, char ch) {
		Square square = null;

		switch (ch) {
		case '#':
			square = new Wall(location, this, '\0');
			break;
		case ' ':
			square = new Square(location, this, '\0');
			break;
		// Boxes
		case '$': //Box without number
			square = new Square(location, this, '\0');
			square.addContents(new Block(square, this, '\0'));
			break;
		// Goals
		case '*': //goal with box already on it
			square = new Goal(location, this, '\0');
			square.addContents(new Block(square, this, '\0'));
			incrementSlots();
			break;
		case '.': // default goal without number
			square = new Goal(location, this, '\0');
			incrementSlots();
			break;
		case '^':
			square = new Cement(location, this, '\0');
			break;
		case '@': 
			square = new Square(location, this, '\0');
			man = new Man(square, this, '\0');
			square.addContents(man);
			break;
		
		case '!':
			square = new Space(location, this, '\0');
			break;
		}
		
		if('0' <= ch && ch <= '9') {
			square = new Goal(location, this, ch);
			incrementSlots();
		}
		else if('a' <= ch && ch <= 'j') {
			square = new Square(location, this, '\0');
			square.addContents(new Block(square, this, (char)(ch - 'a' + '0')));
		}

		if (square == null) {
			errorMessage("problem interpreting character " + ch);
			return;
		}

		squares.setElementAt(location, square);
		square.drawSelf();
	}

	/**
	 * Get the element at a specific location
	 * @param location - x,y coordinates on the board
	 * @return Square for that location
	 */
	public Square squareAt(Location location) {
		return (squares.inBounds(location) ? ((Square) squares
				.elementAt(location)) : null);
	}
	
	/**
	 * Handles swapping something between squares
	 * @param sq1
	 * @param sq2
	 */
	private void swapSquares(Square sq1, Square sq2) {
		Location swapLoc = sq1.getLocation();
		
		squares.setElementAt(sq1.getLocation(), sq2);
		squares.setElementAt(sq2.getLocation(), sq1);
		
		sq1.setLocation(sq2.getLocation());
		sq2.setLocation(swapLoc);
		
		sq1.drawSelf();
		sq2.drawSelf();
	}
	

	
	/**
	 * Zips, uploads the data directory, then deletes it all
	 * @param dir - directory to upload
	 */
	public void uploadAndDelete(Path dir) {
		
		//final String url = "https://www.mastergunner.net/blocks/upload.php";
		final String url = "https://www.cs.drexel.edu/~rwb64/cs530/project/upload.php";
		final String zipFname = dir.getFileName() + ".zip";
		
		//First zip the directory
		try (FileOutputStream os = new FileOutputStream(zipFname);
				ZipOutputStream zos = new ZipOutputStream(os)) {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
				
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                zos.putNextEntry(new ZipEntry(dir.relativize(file).toString()));
	                Files.copy(file, zos);
	                zos.closeEntry();
	                return FileVisitResult.CONTINUE;
	            }

	            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	                zos.putNextEntry(new ZipEntry(dir.relativize(dir).toString() + "/"));
	                zos.closeEntry();
	                return FileVisitResult.CONTINUE;
	            }

			});
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		final String CRLF = "\r\n";
		final String boundary = Long.toHexString(System.currentTimeMillis());
		
		
				
		try {
			URLConnection conn = new URL(url).openConnection();
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			
			try (OutputStream output = conn.getOutputStream();
					PrintWriter writer = new PrintWriter(output)) {
				
				writer.append("--" + boundary).append(CRLF);
			    writer.append("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + zipFname + "\"").append(CRLF);
			    writer.append("Content-Type: application/zip").append(CRLF);
			    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
			    writer.append(CRLF).flush();
			    Files.copy((new File(zipFname)).toPath(), output);
			    output.flush();
			    writer.append(CRLF);
			    writer.append("--" + boundary + "--").append(CRLF).flush();
			     
			}
			
			try(BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String str;
				System.out.println("Upload response: ");
				while((str = in.readLine()) != null)
					System.out.println(str);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			Files.delete(new File(zipFname).toPath());
			
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
				
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                Files.delete(file);
	                return FileVisitResult.CONTINUE;
	            }
			});
			
			Files.delete(dir);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(String.format("Directory \"%s\" deleted and uploaded as \"%s\"", dir.getFileName(), zipFname));
		
	}
	

	/**
	 * Display error message
	 * @param message - what the error is in String 
	 */
	private void errorMessage(String message) {
		JOptionPane.showMessageDialog(display, message, "Error", JOptionPane.ERROR_MESSAGE);
	}


}
