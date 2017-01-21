import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;

/**
 * The game panel creates an instance of the visual and logical board
 * @author Eric Chee
 * @version 1/17/2015
 */
public class TheTesserectPanel extends JPanel implements MouseListener,
		MouseMotionListener
{
	// Game states
	private enum GameState {
		MAINMENU, INSTRUCTIONS1, INSTRUCTIONS2, PREGAMEMENU, INAGAME, CREDITS, PAUSEMENU
	};

	GameState gameState;

	// In-game variables
	private static Matrix3D aMatrix, tMatrix;
	private static VisualBoard visualBoard;
	private static LogicBoard logicBoard;
	private static Tile[][][] tiles;
	private String hoverStats = "";
	private int hoverX, hoverY = 0;
	private boolean movePossible = false;
	int selectedCube = 0;
	int lastCube = 0;
	private int prevX, prevY;

	// Visual options
	private final Color[] COLOURS = { Color.RED, Color.GREEN,
			new Color(75, 0, 130), new Color(255, 102, 0), Color.YELLOW,
			Color.MAGENTA, Color.PINK };
	private static Color[] usedColours;
	private static Color playerColor = Color.BLUE;
	private static String[] aiProduction;
	private final String[] NAMES = { "Atlas", "Dalh", "Tedior", "Maliwan",
			"Hyperion", "Eridian", "Anshin" };

	// Board constructor options
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static int boardSize = 3;
	private static float tileSpacing = 3f;
	private static float size = 60f;
	private static int noComputers = 7;
	private static int difficulty = 2;

	// Images
	private Image MainMenu, Instructions1, Instructions2, Credits,
			PreGameOptions, NewGameHighlight, InstructionsHighlight,
			CreditsHighlight, ExitHighlight,
			PauseMenu, NextTurnAvailable, NextTurnNotAvailable, PlaceUnits,
			PlaceUnitsNA, MoveUnits, MoveUnitsPossible, MoveUnitsNA, Easy,
			EasySelected, Normal, NormalSelected, Hard, HardSelected;
	private Image[] AiButton;
	private Image[] AiButtonSelected;
	private Image[] BoardButton;
	private Image[] BoardButtonSelected;

	/**
	 * Constructs a new ConnectFourBoard object
	 */
	public TheTesserectPanel()
	{
		// Load up images
		MainMenu = new ImageIcon("Images//Main Menu.jpg").getImage();
		NewGameHighlight = new ImageIcon("Images//NewGameHighlight.png")
				.getImage();
		InstructionsHighlight = new ImageIcon(
				"Images//InstructionsHighlight.png").getImage();
		CreditsHighlight = new ImageIcon("Images//CreditsHighlight.png")
				.getImage();
		ExitHighlight = new ImageIcon("Images//ExitHighlight.png").getImage();
		Instructions1 = new ImageIcon("Images//Instructions 1.jpg").getImage();
		Instructions2 = new ImageIcon("Images//Instructions 2.jpg").getImage();
		Credits = new ImageIcon("Images//Credits.jpg").getImage();
		PauseMenu = new ImageIcon("Images//Pause Menu.jpg").getImage();
		NextTurnAvailable = new ImageIcon("Images//NextTurnAvailable.jpg")
				.getImage();
		NextTurnNotAvailable = new ImageIcon("Images//NextTurnNotAvailable.jpg")
				.getImage();
		PreGameOptions = new ImageIcon("Images//Game Setup.jpg").getImage();
		AiButton = new Image[7];
		AiButtonSelected = new Image[7];
		for (int image = 0; image < AiButton.length; image++)
		{
			String fileName = ("Images//" + Integer.toString(image + 1) + "AiUnselected.jpg");
			AiButton[image] = new ImageIcon(fileName).getImage();

			fileName = ("Images//" + Integer.toString(image + 1) + "Ai.jpg");
			AiButtonSelected[image] = new ImageIcon(fileName).getImage();
		}
		BoardButton = new Image[4];
		BoardButtonSelected = new Image[4];
		BoardButton[0] = new ImageIcon("Images//3x3x3Unselected.jpg")
				.getImage();
		BoardButton[1] = new ImageIcon("Images//5x5x5Unselected.jpg")
				.getImage();
		BoardButton[2] = new ImageIcon("Images//7x7x7Unselected.jpg")
				.getImage();
		BoardButton[3] = new ImageIcon("Images//9x9x9Unselected.jpg")
				.getImage();
		BoardButtonSelected[0] = new ImageIcon("Images//3x3x3.jpg").getImage();
		BoardButtonSelected[1] = new ImageIcon("Images//5x5x5.jpg").getImage();
		BoardButtonSelected[2] = new ImageIcon("Images//7x7x7.jpg").getImage();
		BoardButtonSelected[3] = new ImageIcon("Images//9x9x9.jpg").getImage();

		PlaceUnits = new ImageIcon("Images//PlaceUnits.jpg").getImage();
		PlaceUnitsNA = new ImageIcon("Images//PlaceUnitsUnavalible.jpg")
				.getImage();
		MoveUnits = new ImageIcon("Images//MoveUnits.jpg").getImage();
		MoveUnitsNA = new ImageIcon("Images//MoveUnitsUnavalible.jpg")
				.getImage();
		MoveUnitsPossible = new ImageIcon("Images//MoveUnitsPossible.jpg")
				.getImage();

		Easy = new ImageIcon("Images//Easy.jpg").getImage();
		EasySelected = new ImageIcon("Images//EasySelected.jpg").getImage();
		Normal = new ImageIcon("Images//Normal.jpg").getImage();
		NormalSelected = new ImageIcon("Images//NormalSelected.jpg").getImage();
		Hard = new ImageIcon("Images//Hard.jpg").getImage();
		HardSelected = new ImageIcon("Images//HardSelected.jpg").getImage();

		// Sets up the board area, loads in piece images and starts a new game
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(new Color(17, 17, 17));

		// Add mouse listeners and Key Listeners to the game board
		addMouseListener(this);
		addMouseMotionListener(this);
		aMatrix = new Matrix3D();
		tMatrix = new Matrix3D();

		// Gets focus for the key listener
		this.setFocusable(true);
		this.addKeyListener(new KeyHandler());
		this.requestFocusInWindow();
		// Goes to the main menu
		gameState = GameState.MAINMENU;

	}

	/**
	 * @override JSlider
	 * @param optionPane pane to place slider
	 * @param min smallest value
	 * @param max largest value
	 */
	static JSlider getSlider(final JOptionPane optionPane, int min, int max)
	{
		// Changes the spacing based on the range of numbers
		int tickSpacing = 1;
		// Creates a new slider
		JSlider slider = new JSlider(min, max);
		if (max - min > 500)
			tickSpacing = 100;
		else if (max - min > 300)
			tickSpacing = 50;
		else if (max - min > 150)
			tickSpacing = 20;
		else if (max - min > 75)
			tickSpacing = 10;
		else if (max - min > 15)
			tickSpacing = 5;
		slider.setMajorTickSpacing(tickSpacing);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setValue(0);
		// Updates the value of the slider
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent)
			{
				JSlider theSlider = (JSlider) changeEvent.getSource();
				if (!theSlider.getValueIsAdjusting())
				{
					optionPane.setInputValue(new Integer(theSlider.getValue()));
				}
			}
		};
		slider.addChangeListener(changeListener);
		return slider;
	}

	/**
	 * Starts a new game
	 */
	public void newGame()
	{
		// Sets player colours
		usedColours = new Color[noComputers + 1];
		for (int player = 0; player < noComputers; player++)
		{
			usedColours[player] = COLOURS[player];
		}
		usedColours[noComputers] = playerColor;

		// Makes a new logic board
		try
		{
			logicBoard = new LogicBoard(3, boardSize, 1, noComputers, 5, 1.1,
					1,
					difficulty);
		}
		catch (TooManyPlayersException | UnsuportedDimentionException
				| NotEnoughPlayersException e)
		{
			e.printStackTrace();
		}

		// Makes a new 3d board
		visualBoard = new VisualBoard(new Point(WIDTH / 2, HEIGHT / 2),
				Color.RED,
				boardSize, tileSpacing);

		// Sets up strings to display enemy unit production
		aiProduction = new String[noComputers];
		for (int computer = 0; computer < noComputers; computer++)
		{

			String tempStr = (NAMES[computer] + ": " + logicBoard
					.getUnitProduction(computer));
			aiProduction[computer] = tempStr;

		}
		// Sets starting viewing position
		tMatrix.unit();
		tMatrix.xrot(160);
		tMatrix.yrot(-135);
		aMatrix.mult(tMatrix);

		// Maps the new board with the first turn played
		endTurn();
		tiles = (logicBoard.get3DMap(0));
		repaint();

	}

	/**
	 * Changes the board size
	 * 
	 * @param newSize The size of the board desired (3, 5, 7 or 9)
	 */
	public void changeBoardSize(int newSize)
	{
		if (newSize == 3)
		{
			boardSize = 3;
			tileSpacing = 3f;
			size = 60f;
		}
		else if (newSize == 5)
		{
			boardSize = 5;
			tileSpacing = 4f;
			size = 35f;
		}
		else if (newSize == 7)
		{
			boardSize = 7;
			tileSpacing = 4f;
			size = 25f;
		}
		else if (newSize == 9)
		{
			boardSize = 9;
			tileSpacing = 4f;
			size = 20f;
		}
		newGame();
	}

	/**
	 * Changes the number of Ai and starts new game
	 * 
	 * @param noAi Desired number of Ai
	 */
	public void changeNoAi(int noAi)
	{
		noComputers = noAi;
		newGame();
	}

	/**
	 * Checks if a player wins and propts the player if they want to play again
	 */
	private void checkForWinner()
	{
		logicBoard.checkForWinner();
		// If there is a winner then have a play again pop up window
		if (logicBoard.getWinner() != -1)
		{
			JDialog.setDefaultLookAndFeelDecorated(true);
			int response = JOptionPane.showConfirmDialog(null,
					"Do You Want To Play Again?", "Game Over!",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.NO_OPTION)
			{
				gameState = GameState.MAINMENU;
				repaint();
			}
			else if (response == JOptionPane.YES_OPTION)
			{
				newGame();
				repaint();
			}
			else if (response == JOptionPane.CLOSED_OPTION)
			{
				gameState = GameState.MAINMENU;
				repaint();
			}
		}
	}

	/**
	 * Ends the player's turn
	 */
	public void endTurn()
	{

		checkForWinner();// Check if a player wins
		// Calls the logic board to do the Ai's turn and refreshes game
		// statistics
		logicBoard.nextRound();
		tiles = (logicBoard.get3DMap(0));
		logicBoard.updateGame();
		logicBoard.players[logicBoard.getCurrentPlayer()].unitsRemaining = logicBoard.players[logicBoard
				.getCurrentPlayer()].unitProduction;
		// Updates strings for Ai unit production
		aiProduction = new String[noComputers];
		for (int computer = 0; computer < noComputers; computer++)
		{
			String tempStr = (NAMES[computer] + ": " + logicBoard
					.getUnitProduction(computer));
			aiProduction[computer] = tempStr;
		}
		reFresh();
		repaint();
	}

	/**
	 * Updates the the colour of all the cubes
	 */
	public void reFresh()
	{
		for (int x = 0; x < tiles.length; x++)
		{
			for (int y = 0; y < tiles.length; y++)
			{
				for (int z = 0; z < tiles.length; z++)
				{
					setOwner(z + y * boardSize + x * boardSize * boardSize,
							tiles[x][y][z].controller);
				}
			}
		}
	}

	/**
	 * Sets the owner of the tile in the cube
	 * 
	 * @param cubeNo The corresponding cube number
	 * @param owner the owner to change it to
	 */
	public void setOwner(int cubeNo, int owner)
	{
		for (int player = 0; player < noComputers + 1; player++)
		{
			if (owner == player)
			{
				visualBoard.colours[cubeNo] = usedColours[player];
			}
		}
	}

	/**
	 * Checks if a move is possible from this tile
	 * @param tile Tile to check
	 */
	public void checkmoveStatus(Tile tile)
	{
		if (tile.controller == logicBoard.getCurrentPlayer())
		{
			movePossible = true;
			repaint();
		}
		else
		{
			movePossible = false;
			repaint();
		}
	}

	/**
	 * Repaint the board's drawing panel
	 * 
	 * @param g The Graphics context
	 */
	@SuppressWarnings("deprecation")
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Chooses what to draw based on game state
		if (gameState == GameState.MAINMENU)
		{
			g.drawImage(MainMenu, 0, 0, null);
			if (hoverX > 0 && hoverX < 900 && hoverY > 340 && hoverY < 430)
			{
				g.drawImage(NewGameHighlight, 60, 340, null);

			}
			// Instructions button
			else if (hoverX > 0 && hoverX < 868 && hoverY > 475 && hoverY < 565)
			{
				g.drawImage(InstructionsHighlight, 60, 475, null);

			}
			// Credits button
			else if (hoverX > 0 && hoverX < 836 && hoverY > 610 && hoverY < 700)
			{
				g.drawImage(CreditsHighlight, 60, 610, null);
			}
			// Exit button
			else if (hoverX > 0 && hoverX < 800 && hoverY > 745 && hoverY < 835)
			{
				g.drawImage(ExitHighlight, 60, 745, null);
			}
		}
		else if (gameState == GameState.INSTRUCTIONS1)
		{
			g.drawImage(Instructions1, 0, 0, null);
		}
		else if (gameState == GameState.INSTRUCTIONS2)
		{
			g.drawImage(Instructions2, 0, 0, null);
		}
		else if (gameState == GameState.PREGAMEMENU)
		{
			g.drawImage(PreGameOptions, 0, 0, null);
			g.setColor(new Color(0, 191, 243));
			g.setFont(new Font("Myriad Pro", 1, 40));
			g.drawString("No Of Computers", 50, 300);
			g.drawString("Board Size", 50, 550);
			g.drawString("Difficulty", 50, 800);

			// Buttons to choose no Ai
			for (int button = 0; button < AiButton.length; button++)
			{
				if (noComputers - 1 == button)
				{
					g.drawImage(AiButtonSelected[button], 50 + (100 * button),
							350, null);
				}
				else
				{
					g.drawImage(AiButton[button], 50 + (100 * button), 350,
							null);
				}
			}

			// Buttons to choose board size
			for (int button = 0; button < BoardButton.length; button++)
			{
				if (boardSize == 3 + (button * 2))
				{
					g.drawImage(BoardButtonSelected[button],
							50 + (250 * button), 600, null);
				}
				else
				{
					g.drawImage(BoardButton[button], 50 + (250 * button), 600,
							null);
				}

				// Buttons for difficulty
				g.drawImage(Easy, 50, 850, null);
				g.drawImage(Normal, 300, 850, null);
				g.drawImage(Hard, 550, 850, null);

				if (difficulty == 1)
				{
					g.drawImage(EasySelected, 50, 850, null);
				}
				else if (difficulty == 2)
				{
					g.drawImage(NormalSelected, 300, 850, null);

				}
				else if (difficulty == 3)
				{
					g.drawImage(HardSelected, 550, 850, null);

				}

			}

		}
		else if (gameState == GameState.CREDITS)
		{
			g.drawImage(Credits, 0, 0, null);
		}
		else if (gameState == GameState.PAUSEMENU)
		{
			g.drawImage(PauseMenu, 0, 0, null);
		}
		else if (gameState == GameState.INAGAME)
		{
			// Re-renders the cube with new view
			if (visualBoard != null)
			{
				visualBoard.mat.unit();
				visualBoard.mat.mult(aMatrix);
				visualBoard.mat.scale(size, -size, 16 * size / size().width);
				visualBoard.transformed = false;
				visualBoard.paint(g);

			}

			g.setColor(new Color(255, 102, 0));
			g.setFont(new Font("Myriad Pro", 1, 50));

			// Draws appropriate in game prompt and the next turn button
			if (logicBoard.getUnitsRemaining(logicBoard.getCurrentPlayer()) <= 0
					|| (logicBoard.getNoOfTilesControlled(logicBoard
							.getCurrentPlayer()) < 1))
			{
				g.drawImage(NextTurnAvailable, WIDTH - 400, HEIGHT - 200, null);
				if (logicBoard.getNoOfTilesControlled(logicBoard
						.getCurrentPlayer()) < 1)
					g.drawString("You Lost!", 500, 100);
				else
				{
					if (movePossible)
					{
						g.drawString(
								"Select An Adjacent Tile To Move Or Atack",
								550, 100);
					}
					else
						g.drawString(
								"Select One Of Your Tiles To Move Or Attack",
								550, 100);
				}

			}
			else
			{
				g.drawImage(NextTurnNotAvailable, WIDTH - 400, HEIGHT - 200,
						null);
				g.drawString("Double Click A Blue Tile To Place Your Units",
						550, 100);
			}

			g.setColor(new Color(0, 191, 243));
			g.drawString("Turn Status:", WIDTH - 350, 250);
			// Status indicators
			if (logicBoard.getUnitsRemaining(logicBoard.getCurrentPlayer()) <= 0)
			{
				g.drawImage(PlaceUnitsNA, WIDTH - 300, 300, null);
			}
			else
			{
				g.drawImage(PlaceUnits, WIDTH - 300, 300, null);

			}
			if (logicBoard.getUnitsRemaining(logicBoard.getCurrentPlayer()) <= 0
					&& (logicBoard.getNoOfTilesControlled(logicBoard
							.getCurrentPlayer()) > -1))
			{
				if (movePossible)
				{
					g.drawImage(MoveUnits, WIDTH - 300, 400, null);
				}
				else
				{
					g.drawImage(MoveUnitsPossible, WIDTH - 300, 400, null);
				}
			}
			else
			{
				g.drawImage(MoveUnitsNA, WIDTH - 300, 400, null);
			}

			g.setColor(new Color(57, 181, 74));

			// Displays tile statistics and turn statistics
			String turnCounter = ("Turn: " + logicBoard.getNoTurns());
			String unitProduction = ("UnitProduction: " + logicBoard
					.getUnitProduction(logicBoard.getCurrentPlayer()));
			String unitsLeft = ("Units Remaining: " + logicBoard
					.getUnitsRemaining(logicBoard.getCurrentPlayer()));
			g.drawString(unitProduction, 100, HEIGHT - 50);
			g.drawString(unitsLeft, 650, HEIGHT - 50);

			g.setFont(new Font("Myriad Pro", 1, 100));
			g.setColor(new Color(0, 191, 243));
			g.drawString(turnCounter, 100, 100);

			// Enemy unit production
			g.setColor(new Color(237, 28, 36));
			g.setFont(new Font("Myriad Pro", 1, 30));
			g.drawString("Computer Unit Production:", 50, 250);

			for (int computer = 0; computer < noComputers; computer++)
			{
				g.setColor(usedColours[computer]);
				g.drawString(aiProduction[computer], 50, 290 + (35 * computer));
			}

			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Myriad Pro", 1, 50));
			g.drawString(hoverStats, hoverX, hoverY);

		}

	}// paint component method

	// Mouse events you can listen for since this JPanel is a MouseListener
	/**
	 * Responds to a mousePressed event
	 * 
	 * @param event information about the mouse pressed event
	 */
	public void mousePressed(MouseEvent event)
	{
		prevX = event.getX();
		prevY = event.getY();

		// Appropriate action based on game state
		if (gameState == GameState.MAINMENU)
		{
			// New game button
			if (prevX > 0 && prevX < 900 && prevY > 340 && prevY < 430)
			{
				newGame();
				gameState = GameState.PREGAMEMENU;
				repaint();

			}
			// Instructions button
			else if (prevX > 0 && prevX < 868 && prevY > 475 && prevY < 565)
			{
				gameState = GameState.INSTRUCTIONS1;
				repaint();
			}
			// Credits button
			else if (prevX > 0 && prevX < 836 && prevY > 610 && prevY < 700)
			{
				gameState = GameState.CREDITS;
				repaint();
			}
			// Exit button
			else if (prevX > 0 && prevX < 800 && prevY > 745 && prevY < 835)
			{
				this.setVisible(false);
				System.exit(0);
			}

		}
		else if (gameState == GameState.INSTRUCTIONS1)
		{
			// Next button
			if (prevX > WIDTH - 289 && prevX < WIDTH - 91
					&& prevY > HEIGHT - 122 && prevY < HEIGHT - 46)
			{
				gameState = GameState.INSTRUCTIONS2;
				repaint();
			}
		}
		else if (gameState == GameState.INSTRUCTIONS2)
		{
			// Done button
			if (prevX > WIDTH - 289 && prevX < WIDTH - 91
					&& prevY > HEIGHT - 122 && prevY < HEIGHT - 46)
			{
				gameState = GameState.MAINMENU;
				repaint();
			}
		}
		else if (gameState == GameState.CREDITS)
		{
			// Done button
			if (prevX > WIDTH - 289 && prevX < WIDTH - 91
					&& prevY > HEIGHT - 122 && prevY < HEIGHT - 46)
			{
				gameState = GameState.MAINMENU;
				repaint();
			}
		}
		else if (gameState == GameState.PREGAMEMENU)
		{
			// Play button
			if (prevX > WIDTH - 289 && prevX < WIDTH - 91
					&& prevY > HEIGHT - 122 && prevY < HEIGHT - 46)
			{
				gameState = GameState.INAGAME;
				repaint();
			}
			// Buttons to change board size
			for (int button = 0; button < BoardButton.length; button++)
			{
				if (prevX > 50 + (250 * button) && prevX < 250 + (250 * button)
						&& prevY > 600 && prevY < 675)
				{
					changeBoardSize(3 + (2 * button));

				}

			}
			// Buttons for number of Ai
			for (int button = 0; button < AiButton.length; button++)
			{
				if (prevX > 50 + (100 * button) && prevX < 125 + (100 * button)
						&& prevY > 350 && prevY < 425)
				{
					changeNoAi(button + 1);
				}

			}

			// Buttons for difficulty
			for (int button = 0; button < 3; button++)
			{
				if (prevX > 50 + (250 * button) && prevX < 250 + (250 * button)
						&& prevY > 850 && prevY < 925)
				{
					difficulty = button + 1;
					repaint();
				}
			}

		}
		else if (gameState == GameState.PAUSEMENU)
		{
			if (prevX > 660 && prevX < 1260 && prevY > 350 && prevY < 470)
			{
				newGame();
				gameState = GameState.INAGAME;
			}
			else if (prevX > 660 && prevX < 1260 && prevY > 500
					&& prevY < 620)
			{
				gameState = GameState.MAINMENU;
				repaint();
			}
			else if (prevX > 660 && prevX < 1260 && prevY > 650
					&& prevY < 770)
			{
				gameState = GameState.INAGAME;
				repaint();
			}
		}
		else if (gameState == GameState.INAGAME)
		{

			// If the next turn button is clicked
			if (((prevX > WIDTH - 400 && prevX < WIDTH - 100
					&& prevY > HEIGHT - 200 && prevY < HEIGHT - 100) && (logicBoard
					.getUnitsRemaining(logicBoard.getCurrentPlayer()) <= 0))
					|| ((prevX > WIDTH - 400 && prevX < WIDTH - 100
							&& prevY > HEIGHT - 200 && prevY < HEIGHT - 100) && (logicBoard
							.getNoOfTilesControlled(logicBoard
									.getCurrentPlayer()) < 1)))
			{
				endTurn();

			}
			reFresh();
		}
		requestFocusInWindow();
	}// Mouse pressed

	public void mouseClicked(MouseEvent event)
	{
		// Appropriate action based on game state
		if (gameState == GameState.MAINMENU)
		{

		}
		if (gameState == GameState.INAGAME)
		{

			int nx = 0;
			int ny = 0;
			int nz = 0;
			// If a cube is clicked
			if (visualBoard.checkClicked(prevX, prevY) > -1)
			{
				{
					lastCube = selectedCube;
					selectedCube = visualBoard.leftClickCube(prevX, prevY);

					// If no cube was previously selected then both cubes are
					// the new cube
					if (lastCube == -1)
					{
						lastCube = selectedCube;
					}

					// Gets the x,y,z of the old cube
					tiles = (logicBoard.get3DMap(0));
					int currentCube = selectedCube;
					nz = currentCube % boardSize;
					currentCube /= boardSize;
					ny = currentCube % boardSize;
					currentCube /= boardSize;
					nx = currentCube % boardSize;

					repaint();

					// Get the x,y,z of the previously selected cube
					int oldCube = lastCube;
					int z = oldCube % boardSize;
					oldCube /= boardSize;
					int y = oldCube % boardSize;
					oldCube /= boardSize;
					int x = oldCube % boardSize;

					int[] sourceTile = { x, y, z, 0 };
					int[] targetTile = { nx, ny, nz, 0 };

					checkmoveStatus(tiles[nx][ny][nz]);
					if (selectedCube != -1)
					{
						// Move units / attack
						if ((lastCube != selectedCube)
								&& (tiles[x][y][z].controller == logicBoard
										.getCurrentPlayer())
								&& (logicBoard.checkAdjacent(sourceTile,
										targetTile))
								&& (logicBoard.getUnitsRemaining(logicBoard
										.getCurrentPlayer()) <= 0))
						{

							// Uses a JOptionPane with a slider to select how
							// many
							// units
							// they want to move
							int noUnitsToMove = 0;
							JFrame parent = new JFrame();
							JOptionPane optionPane = new JOptionPane();
							JSlider slider = getSlider(optionPane, 0,
									tiles[x][y][z].noOfUnits[1] - 1);
							optionPane
									.setMessage(new Object[] {
											"Select how many units you would like to move: ",
											slider });
							optionPane
									.setMessageType(JOptionPane.QUESTION_MESSAGE);
							JDialog dialog = optionPane.createDialog(parent,
									"Move Units");
							dialog.setDefaultCloseOperation(
									JDialog.DO_NOTHING_ON_CLOSE);
							dialog.setVisible(true);
							if (optionPane.getInputValue() != JOptionPane.UNINITIALIZED_VALUE)
							{
								noUnitsToMove = (int) optionPane
										.getInputValue();
							}

							try
							{
								logicBoard.moveUnits(sourceTile, targetTile,
										noUnitsToMove, 1);
							}
							catch (IllegalMoveException
									| InsufficientUnitsException
									| NotYourTurnException e)
							{
								e.printStackTrace();
							}

							// Updates unit production
							logicBoard.updateUnitProduction();

							// Updates strings for Ai unit production
							aiProduction = new String[noComputers];
							for (int computer = 0; computer < noComputers; computer++)
							{
								String tempStr = (NAMES[computer] + ": " + logicBoard
										.getUnitProduction(computer));
								aiProduction[computer] = tempStr;
							}
							// Deselect cube
							selectedCube = -1;
							lastCube = -1;
							movePossible = false;
							visualBoard.unSelectCube();
							reFresh();
							repaint();
						}
						// Add units
						else if (lastCube == selectedCube
								&& tiles[nx][ny][nz].controller == logicBoard
										.getCurrentPlayer()
								&& logicBoard.getUnitsRemaining(logicBoard
										.getCurrentPlayer()) > 0)
						{
							int noUnitsToAdd = 0;
							JFrame parent = new JFrame();

							JOptionPane optionPane = new JOptionPane();
							JSlider slider = getSlider(optionPane, 0,
									logicBoard.getUnitsRemaining(logicBoard
											.getCurrentPlayer()));
							optionPane
									.setMessage(new Object[] {
											"Select how many units you would like to add: ",
											slider });
							optionPane
									.setMessageType(JOptionPane.QUESTION_MESSAGE);

							JDialog dialog = optionPane.createDialog(parent,
									"Add Units");
							dialog.setDefaultCloseOperation(
									JDialog.DO_NOTHING_ON_CLOSE);
							dialog.setVisible(true);
							if (optionPane.getInputValue() != JOptionPane.UNINITIALIZED_VALUE)
							{
								noUnitsToAdd = (int) optionPane.getInputValue();
							}

							try
							{
								logicBoard.placeUnits(sourceTile, noUnitsToAdd);
							}
							catch (InsufficientUnitsException
									| NotYourTurnException
									| IllegalMoveException e)
							{
								e.printStackTrace();
							}
							// Deselect cube

							selectedCube = -1;
							lastCube = -1;
							movePossible = false;
							visualBoard.unSelectCube();
							reFresh();
							repaint();
						}

					}
				}
			}
			else
			{
				selectedCube = 0;
				lastCube = 0;
				movePossible = false;
				visualBoard.unSelectCube();
				reFresh();
				repaint();
			}
		}
	}//Mouse Clicked

	@SuppressWarnings("deprecation")
	public void mouseDragged(MouseEvent event)
	{
		if (gameState == GameState.INAGAME)
		{
			// Rotates the cube
			int x = event.getX();
			int y = event.getY();
			tMatrix.unit();
			float xtheta = (prevY - y) * 360.0f / size().width;
			float ytheta = (x - prevX) * 360.0f / size().height;
			tMatrix.xrot(xtheta);
			tMatrix.yrot(ytheta);
			aMatrix.mult(tMatrix);
			prevX = x;
			prevY = y;
			hoverStats = "";
			repaint();
		}

	}//Mouse Dragged

	public void mouseMoved(MouseEvent event)
	{
		hoverX = event.getX();
		hoverY = event.getY();
		if (gameState == GameState.MAINMENU)
		{
			repaint();
		}
		else if (gameState == GameState.INAGAME)
		{
			// Because the board dosen't exist for a split second when resetting I used a try catch to prevent errors
			try
			{
				// Shows the number of units on each tile when mouse hovers over
				// it
				if (visualBoard.checkClicked(hoverX, hoverY) != -1)
				{
					int currentCube = visualBoard.checkClicked(hoverX, hoverY);
					int nzz = currentCube % boardSize;
					currentCube /= boardSize;
					int nyy = currentCube % boardSize;
					currentCube /= boardSize;
					int nxx = currentCube % boardSize;

					int noOfUnits = tiles[nxx][nyy][nzz].noOfUnits[0]
							+ tiles[nxx][nyy][nzz].noOfUnits[1];
					hoverStats = (Integer.toString(noOfUnits) + " Units");
					repaint();
				}
				else
				{
					hoverStats = "";
					repaint();
				}
			}
			catch (NullPointerException e)
			{
				hoverStats = "";
				repaint();
			}
		}
	}//Mouse Moved

	// Extra methods needed since this game board is a MouseListener

	public void mouseEntered(MouseEvent event)
	{
	}

	public void mouseExited(MouseEvent event)
	{
	}

	public void mouseReleased(MouseEvent event)
	{
	}

	// Handles key presses
	private class KeyHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent event)
		{
			//Appropriate action based on game state
			if (gameState == GameState.INAGAME)
			{

				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					gameState = GameState.PAUSEMENU;
					repaint();
				}
			}
			else if (gameState == GameState.PAUSEMENU)
			{

				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					gameState = GameState.INAGAME;
					repaint();
				}
			}
			repaint();
		}
	}
}