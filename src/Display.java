
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Display is responsible for drawing images, as well as monitoring user input.
 * 
 * @author Ralph Brewer
 * cs530
 */
public class Display extends JApplet {

	static final long serialVersionUID = 1;

	private static final int Margin = 0;
	private static final String FontName = "Helvetica";

	final Font INSTRUCTION_FONT = new Font(FontName, Font.PLAIN, 15);
	final Font PROMPT_FONT = new Font(FontName, Font.PLAIN, 18);
	final Font TIME_FONT = new Font(FontName, Font.BOLD, 54);
	final Font MSG_FONT = new Font(FontName, Font.PLAIN, 12);

	private static final int BlockSize = 40;

	public Display() {
	}

	private BlocksGame game;
	private File directory;
	private File runDirectory;
	private File[] dirFiles;
	protected JList<String> list;
	DefaultListModel<String> listModel;
	public boolean playBool = true;
	private JComboBox<String> comboBox_1;
	private String comboBoxDir;
	private JButton btnWatch;
	private boolean watchEnabled = false;
	private int playSwitch = 0;

	public void setPlaySwitch(int sw) {
		playSwitch = sw;
	}

	public int getPlaySwitch() {
		return playSwitch;
	}

	@Override
	public void init() {

		NamedImage.preloadImages(this);
		game = new BlocksGame(this);

	}

	@Override
	public void start() {

		runDirectory = new File("runDirectory");
		if (!runDirectory.exists()) {
			if (runDirectory.mkdir()) {
				System.out.println(runDirectory + " directory was created.");
			} else {
				System.out.println(runDirectory + " directory already exists.");
			}
		}
		directory = new File(runDirectory, Long.toString(System.currentTimeMillis()));
		if (!directory.exists()) {
			if (directory.mkdir()) {
				System.out.println(directory + " directory was created.");
			} else {
				System.out.println(directory + " directory already exists.");
			}
		}
		(new Thread() {
			public void run() {
				gamePlay();
			}
		}).start();
	}

	/**
	 * Controls the game screens.
	 */
	private void gamePlay() {

		String homeScreen = "For this study you will be asked to complete 9 levels of increasing difficulty. You will\r\n"
				+ "control an avatar through a room by using the arrow keys on your keyboard. The goal is to move\r\n"
				+ "each labeled box to the correctly labeled target location (i.e., box 1 should be pushed to\r\n"
				+ "target location 1). Please note that the numbers are only to link the box with a target location and\r\n"
				+ "are not a suggested order for moving the boxes. Use the button for \"Start Set\" to begin the study.\r\n";
		while (playBool) {

			if (!watchEnabled) {
				homeScreen(homeScreen, "Start Set ");

			} else {
				homeScreen(
						"That completes the 4 scenarios. You now have the opportunity to review your run by selecting the \r\n"
						+ "directory from the drop down box and then choosing a run from the list and click \"Watch\".",
						"Start Set ");
			}

			switch (playSwitch) {

			case 1:

				homeScreen = "In this part of the game, you will be asked to complete 9 levels of increasing difficulty. You\r\n"
						+ "will control an avatar through a room by using the arrow keys on your keyboard. The goal is to\r\n"
						+ "move the correctly labeled box to the correctly labeled target location (i.e., box 1 should be\r\n"
						+ "pushed to target location 1). In this set of levels, you will not know the box label until you push\r\n"
						+ "the box with your avatar. Please note that the numbers are only to link the box with a target\r\n"
						+ "location and are not a suggested order for moving the boxes.";
				
				game.play(9, directory.getPath());
				break;

			case 2:

				homeScreen = "In this part of the game, you will be asked to complete 9 levels of increasing difficulty. You will\r\n"
						+ "control an avatar through a room by using the arrow keys on your keyboard. The goal is to move\r\n"
						+ "the correctly labeled box to the correctly labeled target location (i.e., box 1 should be pushed to\r\n"
						+ "target location 1). In this set of levels, you will not know the box label until you push the box\r\n"
						+ "with your avatar. Please note that the numbers are only to link the box with a target location and\r\n"
						+ "are not a suggested order for moving the boxes. In addition, there is an extra box that does not\r\n"
						+ "have a label. This box is movable but will not have a target location.";

				game.play(9, directory.getPath(), BlocksGame.GameType.UNLABELED_GAME);
				break;

			case 3:

				homeScreen = "In this part of the game, you will be asked to complete 9 levels of increasing difficulty. You will\r\n"
						+ "control an avatar through a room by using the arrow keys on your keyboard. The goal is to move\r\n"
						+ "the correctly labeled box to the correctly labeled target location (i.e., box 1 should be pushed to\r\n"
						+ "target location 1). In this set of levels, you will not know the box label until you push the box\r\n"
						+ "with your avatar. Please note that the numbers are only to link the box with a target location and\r\n"
						+ "are not a suggested order for moving the boxes. In addition, there is an extra box that does not\r\n"
						+ "have a label. This box is immovable. When you push this box with your avatar, it will change\r\n"
						+ "color to let you know that it is an immovable box.";

				game.play(9, directory.getPath(), BlocksGame.GameType.IRRELEVANT_GAME);
				break;

			case 4:

				game.play(9, directory.getPath(), BlocksGame.GameType.IMMOVABLE_GAME);

				watchEnabled = true;

				break;

			case 5:
				game.uploadAndDelete(directory.toPath());
				instructionPrompt("Thank you for participating in our research study.", null);
				break;

			case 6:

				System.out.println(list.getSelectedValue());
				System.out.println(String.valueOf(comboBox_1.getSelectedItem()));
				game.watchRun("runDirectory/" + comboBoxDir + "/" + list.getSelectedValue());
				playSwitch = 0;
				break;

			}
		}
		game.uploadAndDelete(directory.toPath());
		instructionPrompt("Thank you for participating in our research study.", null);
	}

	//inner class GridCanvas which creates the game playing surface
	public class GridCanvas extends JPanel {
		static final long serialVersionUID = 1;

		private int numRows, numCols, blockSize;
		private List<BlockImage> images = new CopyOnWriteArrayList<>();
		private List<Point> trail;

		private class BlockImage {
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + ((r == null) ? 0 : r.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				BlockImage other = (BlockImage) obj;
				if (!getOuterType().equals(other.getOuterType()))
					return false;
				if (r == null) {
					if (other.r != null)
						return false;
				} else if (!r.equals(other.r))
					return false;
				return true;
			}

			public Image img;
			public char ch;
			public Rectangle r;

			BlockImage(Rectangle rect, Image i, char c) {
				r = rect;
				img = i;
				ch = c;
			}

			public boolean hasChar() {
				return ch > 0;
			}

			private GridCanvas getOuterType() {
				return GridCanvas.this;
			}
		}

		public GridCanvas(int size) {
			setBackground(Color.white);
			setFont(new Font("SansSerif", Font.PLAIN, 12));
			blockSize = size;
		}

		/**
		 * Makes sure that a location is in bounds for this Canvas,
		 * throws exception if not within bounds
		 */
		private boolean badLocation(Location loc) {
			return (loc.getRow() < 0 || loc.getRow() >= numRows || loc.getCol() < 0 || loc.getCol() >= numCols);
		}

		private void checkLocation(Location loc) {
			if (badLocation(loc)) {
				throw new IndexOutOfBoundsException(
						"Grid Canvas asked to draw at location " + loc + " which is outside grid boundaries.");
			}
		}

		//based on size form config file
		public void configureForSize(int nRows, int nCols) {
			numRows = nRows;
			numCols = nCols;
			setSize(blockSize * numCols, blockSize * numRows);
			images.clear();
			repaint();
		}

		//numbers for blocks and goals
		private void drawCenteredString(Graphics g, String s, Rectangle r) {
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Color.black);
			g.drawString(s, r.x + (r.width - fm.stringWidth(s)) / 2, r.y + (r.height + fm.getHeight()) / 2);
		}

		//trail of avatar as it moves through the replay
		public void addToTrail(Location loc) {
			if (trail == null)
				trail = new CopyOnWriteArrayList<>();

			Rectangle r = rectForLocation(loc.getRow(), loc.getCol());

			trail.add(new Point(r.x + (r.width / 2), r.y + (r.height / 2)));
			repaint();
		}

		/**
		 * Draws and image and its number to a specific location.
		 * @param imageFileName - image to write to screen
		 * @param ch - character to add to image
		 * @param loc - location to write to in the grid
		 */
		public void drawImageAndLetterAtLocation(String imageFileName, char ch, Location loc) {
			// Make sure location is valid
			checkLocation(loc);

			// Draw image at location
			drawLocation(loc, NamedImage.findImageNamed(imageFileName), ch);
		}

		/**
		 * Actually paints the image from drawImageAndLetterAtLocation t screen
		 * @param loc - location to draw
		 * @param ni - image
		 * @param letter - character to draw
		 */
		private void drawLocation(Location loc, NamedImage ni, char letter) {
			Rectangle r = rectForLocation(loc.getRow(), loc.getCol());

			images.add(new BlockImage(r, ni.image, letter));

			repaint(r);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(blockSize * numCols, blockSize * numRows);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (BlockImage bi : images) {
				g.drawImage(bi.img, bi.r.x, bi.r.y, bi.r.width, bi.r.height, this);
				if (bi.hasChar())
					drawCenteredString(g, bi.ch + "", bi.r);
			}
			if (trail != null && trail.size() > 1) {
				Point p1 = trail.get(0);
				g.setColor(Color.GREEN);
				for (Point p2 : trail) {
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
					p1 = p2;
				}
			}
		}

		private Rectangle rectForLocation(int row, int col) {
			return new Rectangle(col * blockSize, (numRows - row - 1) * blockSize, blockSize, blockSize);
		}

		@Override
		public void update(Graphics g) {
			paint(g);
		}
	}

	// static nested class NamedImage - gets images from resources for display on the grid
	static class NamedImage {
		private static Vector<NamedImage> allImages = new Vector<NamedImage>();
		private static MediaTracker mt;
		private static String things[] = { "Man", "Box" };
		private static String squares[] = { "Empty", "Wall", "Goal" };
		private static JApplet app;

		static public NamedImage findImageNamed(String name) {
			return findImageNamed(name, false);
		}

		//get the images based on their name from Images dir
		static public NamedImage findImageNamed(String name, boolean isBackgroundImage) {
			NamedImage key = new NamedImage(name);
			int foundIndex = allImages.indexOf(key);

			// Search cache for this name
			if (foundIndex != -1) {
				return allImages.elementAt(foundIndex);
			}
			// Return shared version
			else {
				key.image = app.getImage(app.getClass().getClassLoader().getResource("Images/" + name + ".gif"));

				// Create image from file
				mt.addImage(key.image, 0);

				// Add to Media Tracker
				try {
					mt.waitForID(0);
				} catch (InterruptedException ie) {
				}

				allImages.addElement(key);

				// Add to list of all images
				key.isBackgroundImage = isBackgroundImage;
				return key;
			}
		}

		//get all the images and preload them
		static public void preloadImages(JApplet target) {
			app = target;
			mt = new MediaTracker(target);

			for (int i = 0; i < things.length; i++) {
				findImageNamed(things[i]);
			}

			for (int i = 0; i < squares.length; i++) {
				findImageNamed(squares[i], true);
			}
		}

		public String name;

		public Image image;

		public boolean isBackgroundImage;

		private NamedImage(String n) {
			name = n;
		}

		@Override
		public boolean equals(Object o) {
			return ((o instanceof NamedImage) && name.equals(((NamedImage) o).name));
		}
	}

	private Label msgField;

	private GridCanvas gridCanvas;

	private Vector<Command> cmds = new Vector<Command>();

	//adding key commands
	public synchronized void addCommand(KeyEvent ke) {
		Command cmd = new Command(ke.getKeyCode());
		cmds.addElement(cmd);

		notify();
	}

	//configure the grid based on config file
	public void configureForSize(int numRows, int numCols) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				configureBlocks(numRows, numCols);
				gridCanvas.configureForSize(numRows, numCols);
				repaint();
				revalidate();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configures the blocks in the applet.
	 * @param numRows
	 * @param numCols
	 */
	private void configureBlocks(int numRows, int numCols) {
		getContentPane().removeAll();

		getContentPane().setLayout(new BorderLayout(Margin, Margin));

		JTextPane textarea = new JTextPane();
		textarea.setEditable(false);
		textarea.setContentType("text/html");
		textarea.setFont(INSTRUCTION_FONT);
		textarea.setText(
				"<center>Move by using the <b>arrow keys</b>.<br /> Press <b>R</b> to restart this level.<br />"
						+ "Press <b>N</b> to skip to the next level.<br />  Press <b>Q</b> to quit.</center>");

		JPanel bp = new JPanel();
		bp.setLayout(new GridLayout(2, 1, 10, 0));
		bp.add(textarea);

		bp.add(msgField = new Label("New game", Label.CENTER));
		msgField.setFont(MSG_FONT);

		JPanel panel = new JPanel();
		gridCanvas = new GridCanvas(BlockSize);
		panel.add(gridCanvas);

		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(bp, BorderLayout.SOUTH);

		gridCanvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				Display.this.addCommand(ke);
			}
		});
	}

	//handles just instructions
	public void instructionPrompt(String instructions, String label) {

		final Semaphore clicked = new Semaphore(0);

		try {

			SwingUtilities.invokeAndWait(() -> {
				getContentPane().removeAll();

				getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				JTextPane textarea = new JTextPane();
				textarea.setEditable(false);
				textarea.setContentType("text/html");
				textarea.setText("<html><style> center { font: " + PROMPT_FONT.getSize() + " " + PROMPT_FONT.getFamily()
						+ "}</style><center>" + instructions + "</center></html>");

				getContentPane().add(textarea);

				if (label != null) {
					JButton button = new JButton(label);
					button.addActionListener(new AbstractAction() {
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							clicked.release();
						}
					});

					getContentPane().add(button);
				}

				repaint();
				revalidate();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}

		try {
			clicked.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//the screen that we work from at start and between levels
	public void homeScreen(String instructions, String label) {

		final Semaphore clicked = new Semaphore(0);

		try {
			listModel = new DefaultListModel<String>();
			list = new JList<String>();

			SwingUtilities.invokeAndWait(() -> {
				getContentPane().removeAll();

				getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				JTextPane textarea = new JTextPane();
				textarea.setEditable(false);
				textarea.setContentType("text/html");
				textarea.setText("<html><style> center { font: " + PROMPT_FONT.getSize() + " " + PROMPT_FONT.getFamily()
						+ "}</style><center>" + instructions + "</center></html>");

				getContentPane().add(textarea);

				JPanel panel_1 = new JPanel();
				panel_1.setBounds(96, 5, 258, 372);
				getContentPane().add(panel_1);
				GridBagLayout gbl_panel_1 = new GridBagLayout();
				gbl_panel_1.columnWidths = new int[] { 150 };
				gbl_panel_1.rowHeights = new int[] { 14, 0, 0, 20, 23, 0, 0, 0, 0, 0, 0, 10 };
				gbl_panel_1.columnWeights = new double[] { 0.0 };
				gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
						Double.MIN_VALUE };
				panel_1.setLayout(gbl_panel_1);

				JSeparator separator = new JSeparator();
				GridBagConstraints gbc_separator = new GridBagConstraints();
				gbc_separator.insets = new Insets(0, 0, 5, 0);
				gbc_separator.gridx = 0;
				gbc_separator.gridy = 5;
				panel_1.add(separator, gbc_separator);

				JLabel lblChooseAName = new JLabel("Choose a directory");
				GridBagConstraints gbc_lblChooseAName = new GridBagConstraints();
				gbc_lblChooseAName.insets = new Insets(0, 0, 5, 0);
				gbc_lblChooseAName.anchor = GridBagConstraints.WEST;
				gbc_lblChooseAName.gridx = 0;
				gbc_lblChooseAName.gridy = 6;
				panel_1.add(lblChooseAName, gbc_lblChooseAName);

				comboBox_1 = new JComboBox<String>();
				GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
				gbc_comboBox_1.insets = new Insets(0, 0, 5, 0);
				gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox_1.gridx = 0;
				gbc_comboBox_1.gridy = 7;

				File[] dir = getRunDirectories(directory.toPath());
				for (File file : dir) {
					// if(file.isFile()){
					comboBox_1.addItem(file.getName());
					// }
				}

				panel_1.add(comboBox_1, gbc_comboBox_1);

				comboBox_1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						comboBoxDir = (String) comboBox_1.getSelectedItem();
						if (comboBoxDir != null) {
							getDirFiles(comboBoxDir);
							System.out.println(comboBoxDir);
						} else {
							System.out.println("not found");
						}
					}
				});

				getDirFiles(String.valueOf(comboBox_1.getSelectedItem()));

				comboBox_1.setSelectedIndex(0);

				if (dirFiles != null) {
					for (File file : dirFiles) {
						if (file.isFile()) {
							if (!file.getName().startsWith("blocks")
									&& (file.getName().endsWith("REGULAR_GAME.csv")
											|| file.getName().endsWith("IRRELEVANT_GAME.csv")
											|| file.getName().endsWith("UNLABELED_GAME.csv")
											|| file.getName().endsWith("IMMOVABLE_GAME.csv"))
									&& Character.isDigit(file.getName().charAt(0))) {
								listModel.addElement(file.getName());
								System.out.println(file.getName());
							}
						}
					}
				}

				list.setModel(listModel);

				JScrollPane scrollPane = new JScrollPane(list);
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				gbc_scrollPane.gridx = 0;
				gbc_scrollPane.gridy = 8;
				scrollPane.setPreferredSize(new Dimension(200, 100));
				scrollPane.setViewportView(list);

				panel_1.add(scrollPane, gbc_scrollPane);

				btnWatch = new JButton("Watch");
				GridBagConstraints gbc_btnWatch = new GridBagConstraints();
				gbc_btnWatch.anchor = GridBagConstraints.WEST;
				gbc_btnWatch.insets = new Insets(0, 0, 5, 0);
				gbc_btnWatch.gridx = 0;
				gbc_btnWatch.gridy = 9;

				btnWatch.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						// get the name from the list box

						// start the watch using that file
						playSwitch = 6;
						clicked.release();

					}
				});

				panel_1.add(btnWatch, gbc_btnWatch);

				JButton btnEnd = new JButton("Quit");
				GridBagConstraints gbc_btnEnd = new GridBagConstraints();
				gbc_btnEnd.anchor = GridBagConstraints.WEST;
				gbc_btnEnd.insets = new Insets(0, 0, 5, 0);
				gbc_btnEnd.gridx = 1;
				gbc_btnEnd.gridy = 9;

				btnEnd.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						// get the name from the list box

						// start the watch using that file
						playSwitch = 5;
						clicked.release();

					}
				});

				panel_1.add(btnEnd, gbc_btnEnd);

				if (label != null) {
					JButton button = new JButton(label);
					button.addActionListener(new AbstractAction() {
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							playSwitch++;
							System.out.println("Playswitch " + playSwitch);
							clicked.release();
						}
					});

					if (!watchEnabled) {
						btnWatch.setEnabled(false);
						button.setEnabled(true);
					} else {
						btnWatch.setEnabled(true);
						button.setEnabled(false);
					}

					getContentPane().add(button);
				}

				repaint();
				revalidate();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}

		try {
			clicked.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//get all the directories from the run directory to add to combo box
	public File[] getRunDirectories(Path dir) {
		System.out.println(dir);
		try {

			File[] directories = new File("runDirectory").listFiles(File::isDirectory);

			return directories;

			// removes current run files and directories
			// Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			//
			// public FileVisitResult visitFile(Path file, BasicFileAttributes
			// attrs) throws IOException {
			// // Files.delete(file);
			//
			// //file
			// System.out.println(file);
			//
			// //add files to combo box
			// return FileVisitResult.CONTINUE;
			// }
			// });

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	//list all the files in a given directory
	public void getDirFiles(String dir) {
		System.out.println(dir);
		try {

			File[] dirFiles = new File("runDirectory/" + dir).listFiles();

			listModel.removeAllElements();

			if (dirFiles != null) {
				for (File file : dirFiles) {
					if (file.isFile()) {
						if (!file.getName().startsWith("blocks")
								&& (file.getName().endsWith("REGULAR_GAME.csv")
										|| file.getName().endsWith("IRRELEVANT_GAME.csv")
										|| file.getName().endsWith("UNLABELED_GAME.csv")
										|| file.getName().endsWith("IMMOVABLE_GAME.csv"))
								&& Character.isDigit(file.getName().charAt(0))) {
							listModel.addElement(file.getName());
							System.out.println(file.getName());
						}
					}
				}
			}

			list.setModel(listModel);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doDrawStatusMessage(String msg) {
		if (msgField != null)
			msgField.setText(msg);
	}

	public void drawAtLocation(String name, char ch, Location loc) {
		gridCanvas.drawImageAndLetterAtLocation(name, ch, loc);
	}

	public void drawStatusMessage(String msg) {
		doDrawStatusMessage(msg);
	}

	public synchronized Command getCommandFromUser() {
		while (cmds.size() == 0) {
			// while vector of commands is empty
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e.toString());
			}
			// wait for notify
		}

		Command cmd = cmds.elementAt(0);

		// Pull first command out of queue
		cmds.removeElementAt(0);

		return cmd;
	}

	//keeps the focus on certain part of the screen
	public boolean grabFocus() {
		gridCanvas.requestFocus();
		return gridCanvas.hasFocus();
	}

	//trail keeps track of route the avatar 
	public void addToTrail(Location location) {
		gridCanvas.addToTrail(location);
	}

}
