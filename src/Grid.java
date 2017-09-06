

/* Grid class
 * ----------
 * This class is a wrapper around a 2-dimensional array. It
 * transforms from a "Location" object to the row/col index
 * into the 2-d array. 
 */

public class Grid {
	// a few private instance variables
	private int numRows, numCols;
	private Object grid[][];

	public Grid(int numRows, int numCols) {
		// create new 2-d array, all entries null
		this.numRows = numRows;
		this.numCols = numCols;
		grid = new Object[numRows][numCols];
	}

	/**
	 * Retrieve an element from that location in grid. If no contents set, the
	 * return will be null. If location is out of bounds for this grid, an
	 * exception is thrown (just like on array or vector access)
	 */
	public Object elementAt(Location loc) {
		if (!inBounds(loc)) {
			throw new IndexOutOfBoundsException();
		}

		return grid[loc.getRow()][loc.getCol()];
	}

	/**
	 * Used to check if a location is valid for a given grid.
	 */
	public boolean inBounds(Location loc) {
		return (loc.getRow() >= 0 && loc.getRow() < numRows
				&& loc.getCol() >= 0 && loc.getCol() < numCols);
	}

	/**
	 * Number of columns in the grid.
	 */
	public int numCols() {
		return numCols;
	}

	/**
	 * Number of rows in the grid.
	 */
	public int numRows() {
		return numRows;
	}

	/**
	 * Not a usual grid operation, but one that is useful for our game. It just
	 * randomly chooses row and column values from the valid range for this grid
	 * and return the element at that location. This is handy when you just need
	 * to pick a random square from the playing board, such as when moving the
	 * Jumper around.
	 */
	public Object randomElement() {
		int row = (int) (Math.random() * numRows);
		int col = (int) (Math.random() * numCols);
		return grid[row][col];
	}

	/**
	 * Sets an element at a location in grid, overwriting any previous contents.
	 * If location is out of bounds for this grid, an exception is thrown (just
	 * like on array or vector access)
	 */
	public void setElementAt(Location loc, Object obj) {
		if (!inBounds(loc)) {
			throw new IndexOutOfBoundsException();
		}

		grid[loc.getRow()][loc.getCol()] = obj;
	}
}
