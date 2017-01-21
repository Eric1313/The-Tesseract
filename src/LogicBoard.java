import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * Sores all of the data and has all of the behaviors related to the mechanics
 * behind the game; basically, this is the core of the non-graphical component
 * of the game
 * @author Logan Grier & Eric Chee
 * @version 17/01/2015
 */
public class LogicBoard
{
	private Tile[][][][] tiles;
	Player[] players;
	private int totalUnitProduction, numPlayersRemaining, noOfPlayers,
			movesPerTurn, noOfComputers;
	private int winner = -1;
	private int currentPlayer = 0;
	private double defensiveBonus;
	private int turnNo = 0;
	private double playerEconomicBonus;
	private int strongestPlayer;

	/**
	 * Creates the object
	 * @param noOfDimentions 2, 3, or 4
	 * @param sideLength How big one side is (all sides are the same length)
	 * @param noOfPlayers How many players will be playing (AI and human
	 *            included)
	 * @param unitOnEmptyTile The number of units on tiles not controlled by any
	 *            player
	 * @param defensiveBonus The strength multiplier given to defenders
	 * @param playerEconomicBonus An multiplier on the number of units that the
	 *            human player produces
	 * @param aiIntelligence The sophistication of the AI (1,2, or 3); higher is
	 *            more sophisticated
	 * @throws TooManyPlayersException When the number of players exceeds the
	 *             number of corners on the board
	 * @throws UnsuportedDimentionException When the number of dimensions is not
	 *             2 or 3
	 * @throws NotEnoughPlayersException If there are less than 2 players
	 */
	public LogicBoard(int noOfDimentions, int sideLength, int noOfHumans,
			int noOfComputers, int unitOnEmptyTile, double defensiveBonus,
			double playerEconomicBonus, int aiIntelligence)
			throws TooManyPlayersException, UnsuportedDimentionException,
			NotEnoughPlayersException
	{
		this.noOfPlayers = noOfHumans + noOfComputers;
		this.noOfComputers = noOfComputers;
		// The board must be 2, 3, or 4 dimensions
		if (noOfDimentions < 2 || noOfDimentions > 4)
			throw new UnsuportedDimentionException(noOfDimentions);

		// Ensure that there are enough players in the game
		if (noOfPlayers < 2)
			throw new NotEnoughPlayersException(noOfPlayers);

		// Each player must start on a corner
		final int MAX_NUM_OF_PLAYERS = (int) Math.pow(2, noOfDimentions);
		if (noOfPlayers > MAX_NUM_OF_PLAYERS)
			throw new TooManyPlayersException(noOfPlayers, MAX_NUM_OF_PLAYERS,
					noOfDimentions);

		// Load board info
		this.defensiveBonus = defensiveBonus;
		this.movesPerTurn = 1;
		this.playerEconomicBonus = playerEconomicBonus;

		// Generate players
		players = new Player[noOfPlayers];

		// Generate computer players
		if (aiIntelligence == 1)
		{
			for (int computer = 0; computer < noOfComputers; computer++)
				players[computer] = new AiDumb(computer, this);
		}
		else if (aiIntelligence == 2)
		{
			for (int computer = 0; computer < noOfComputers; computer++)
				players[computer] = new AiRegular(computer, this);
		}
		if (aiIntelligence == 3)
		{
			for (int computer = 0; computer < noOfComputers; computer++)
				players[computer] = new AiSmart(computer, this);
		}

		// Generate human players
		for (int human = noOfComputers; human < noOfPlayers; human++)
			players[human] = new Player(human);

		// Information about board and internal variables
		for (int player = 0; player < noOfPlayers; player++)
		{
			players[player].noOfTilesControlled = 1;
			players[player].unitProduction = 1;
		}
		totalUnitProduction = noOfPlayers;

		// Generate tiles
		int i3 = 1;
		int i4 = 1;
		if (noOfDimentions == 4)
		{
			i3 = sideLength;
			i4 = sideLength;
		}
		else if (noOfDimentions == 3)
			i3 = sideLength;
		tiles = new Tile[sideLength][sideLength][i3][i4];
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				for (int z = 0; z < tiles[0][0].length; z++)
					for (int q = 0; q < tiles[0][0][0].length; q++)
						tiles[x][y][z][q] = new Tile(unitOnEmptyTile,
								movesPerTurn, x, y, z, q, sideLength,
								sideLength, i3, i4);

		// Set the initial state of the board (who controls what)
		tiles[0][0][tiles[0][0].length - 1][tiles[0][0][0].length - 1].controller = 0; // 0011
		tiles[tiles.length - 1][tiles[0].length - 1][tiles[0][0].length - 1][tiles[0][0][0].length - 1].controller = 1;// 1111
		if (noOfPlayers > 2)
			tiles[0][tiles[0].length - 1][tiles[0][0].length - 1][tiles[0][0][0].length - 1].controller = 2;// 0111
		if (noOfPlayers > 3)
			tiles[tiles.length - 1][0][tiles[0][0].length - 1][tiles[0][0][0].length - 1].controller = 3;// 1011
		if (noOfPlayers > 4)
			tiles[0][0][0][tiles[0][0][0].length - 1].controller = 4;// 0001
		if (noOfPlayers > 5)
			tiles[tiles.length - 1][tiles[0].length - 1][0][tiles[0][0][0].length - 1].controller = 5;// 1101
		if (noOfPlayers > 6)
			tiles[0][tiles[0].length - 1][0][tiles[0][0][0].length - 1].controller = 6;// 0101
		if (noOfPlayers > 7)
			tiles[tiles.length - 1][0][0][tiles[0][0][0].length - 1].controller = 7;// 1001
		if (noOfPlayers > 8)
			tiles[0][0][0][0].controller = 8; // 0000
		if (noOfPlayers > 9)
			tiles[tiles.length - 1][tiles[0].length - 1][tiles[0][0].length - 1][0].controller = 9;// 1110
		if (noOfPlayers > 10)
			tiles[0][tiles[0].length - 1][tiles[0][0].length - 1][0].controller = 10;// 0110
		if (noOfPlayers > 11)
			tiles[tiles.length - 1][0][tiles[0][0].length - 1][0].controller = 11;// 1010
		if (noOfPlayers > 12)
			tiles[0][0][tiles[0][0].length - 1][0].controller = 12;// 0010
		if (noOfPlayers > 13)
			tiles[tiles.length - 1][tiles[0].length - 1][0][0].controller = 13;// 1100
		if (noOfPlayers > 14)
			tiles[0][tiles[0].length - 1][0][0].controller = 14;// 0100
		if (noOfPlayers > 15)
			tiles[tiles.length - 1][0][0][0].controller = 15;// 1000

		updateGame();
	}

	/**
	 * Increases the number of units on the target tile by the given quantity
	 * @param tile a 1-D array of length 4 with the coordinates of the tile
	 * @param noOfUnits The number of units to add to the tile
	 * @throws InsufficientUnitsException When a player tries to place more
	 *             units on a tile than are available
	 * @throws NotYourTurnException If the player who's turn it is right now is
	 *             not the controller of the given tile
	 * @throws IllegalMoveException If the player tries to place negative units
	 */
	public void placeUnits(int[] tile, int noOfUnits)
			throws InsufficientUnitsException, NotYourTurnException,
			IllegalMoveException
	{
		// Ensure that the player moving this tile is allowed to do so
		if (tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller != currentPlayer)
			throw new NotYourTurnException(currentPlayer,
					tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller);

		// Ensure that the player isn't trying to place more units than they
		// have
		if (noOfUnits > players[tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller].unitsRemaining)
			throw new InsufficientUnitsException(
					players[tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller].unitsRemaining,
					noOfUnits,
					tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller);

		// Ensure that the player sin't trying to place negative units
		if (noOfUnits < 0)
			throw new IllegalMoveException("Cannot place negative units.");

		// Place units
		players[tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller].unitsRemaining -= noOfUnits;
		tiles[tile[0]][tile[1]][tile[2]][tile[3]].noOfUnits[1] += noOfUnits;

		System.out
				.printf(
						"\n Player %d has placed %d units on tile %d, %d, %d, %d. There are now %d units on this tile.",
						tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller,
						noOfUnits,
						tile[0], tile[1], tile[2], tile[3],
						tiles[tile[0]][tile[1]][tile[2]][tile[3]].noOfUnits[1]); // debug
	}

	/**
	 * Fights a battle between an attacker and the controller of a tile
	 * @param tile Which tile is the battle taking place on
	 * @param attacker The player ID of the attacker
	 * @param noOfAttackerUnits The number of units the attacker is attacking
	 *            with
	 */
	private void battle(Tile tile, int attacker, int noOfAttackerUnits)
	{
		Random randomNumberGenerator = new Random();

		// Calculate defender strength (Units*Defensive Bonus*Log-normally
		// distributed random factor)
		int noOfDefenderUnits = 0;
		for (int unit = 0; unit < tile.noOfUnits.length; unit++)
			noOfDefenderUnits += tile.noOfUnits[unit];
		double defenderStrength = noOfDefenderUnits
				* this.defensiveBonus
				* Math.exp(randomNumberGenerator.nextGaussian() / 5);

		// Calculate attacker strength (Units*Log-normally distributed random
		// factor)
		double attackerStrength = noOfAttackerUnits;
		attackerStrength *= Math.exp(randomNumberGenerator.nextGaussian() / 5);

		// Check who wins as well as casualties, transfer ownership accordingly,
		// update tile counters
		if (attackerStrength > defenderStrength)
		{
			players[attacker].noOfTilesControlled++; // This section of code
														// should
			// come first
			if (tile.controller >= 0)
				players[tile.controller].noOfTilesControlled--;

			double relativeStrength = defenderStrength / attackerStrength;
			double casulaties = noOfAttackerUnits
					* (relativeStrength * relativeStrength);
			tile.controller = attacker;
			tile.noOfUnits[0] = (int) (noOfAttackerUnits - casulaties);
			tile.noOfUnits[1] = 0;

			// Ensure that there is still a unit on this tile
			if (tile.noOfUnits[0] < 1)
				tile.noOfUnits[0] = 1;
		}
		else
		{
			double relativeStrength = attackerStrength / defenderStrength;
			double casulaties = noOfDefenderUnits
					* (relativeStrength * relativeStrength);
			tile.noOfUnits[1] = (int) (noOfDefenderUnits - casulaties);
			tile.noOfUnits[0] = 0;

			// Ensure that there is still a unit on this tile
			if (tile.noOfUnits[1] < 1)
				tile.noOfUnits[1] = 1;
		}

		System.out
				.printf(
						"\n Player %d has attacked tile with %d units. The defender had %d units. The tile is now controlled by %d and has %d units.",
						attacker, noOfAttackerUnits, noOfDefenderUnits,
						tile.controller, tile.noOfUnits[0] + tile.noOfUnits[1]); // debug

		// // If the attacker is human, analyze the battle to help estimate
		// their
		// // aggressiveness factor
		// if (attacker >= noOfComputers)
		// {
		// /*
		// * Source of equations seen below aggressiveness =
		// * board.getDefensiveBonus() * aggressivnessPremium - 1;
		// *
		// * numberOfUnitsRequiredOffenceTarget = (tiles[offensiveMoves
		// * .get(target
		// * ).target[0]][offensiveMoves.get(target).target[1]][offensiveMoves
		// * .get(target).target[2]][offensiveMoves.get(target).target[3]].
		// * noOfUnits[0] +
		// * tiles[offensiveMoves.get(target).target[0]][offensiveMoves
		// * .get(target
		// * ).target[1]][offensiveMoves.get(target).target[2]][offensiveMoves
		// * .get(target).target[3]].noOfUnits[1]) this.aggressiveness;
		// *
		// * This gives us: noOfDefenderUnits*(board.getDefensiveBonus() *
		// * aggressivnessPremium - 1)=noOfAttackerUnits
		// *
		// * algebra happens, and we arrive at:
		// * aggressivnessPremium=board.getDefensiveBonus()*((double)
		// * noOfAttackerUnits/noOfDefenderUnits+1)
		// */
		//
		// double instanceAggPremium = (double) noOfAttackerUnits
		// / (noOfDefenderUnits + this.defensiveBonus);
		// int weight = noOfAttackerUnits;
		//
		// // TODO Something dons't look right here (what is the one's digit on
		// // instanceAggPremium most likely to be )
		// }
	}

	/**
	 * Moves units between two adjacent tiles
	 * @param oldTile a 1-D array of length 4 with the coordinates of the tile
	 *            the units are moving from
	 * @param newTile a 1-D array of length 4 with the coordinates of the tile
	 *            the units are moving to
	 * @param noOfUnits The number of units to be moved
	 * @param noOfMovesLeft Specifies which units on the tile to move
	 * @throws IllegalMoveException If the player tries to move more than one
	 *             move at a time, or not at all
	 * @throws InsufficientUnitsException When the player tries to move more
	 *             units than exist on a tile or when they try to move negative
	 *             units
	 * @throws NotYourTurnException If the controller of the source tile is not
	 *             the player who's turn it is
	 */
	public void moveUnits(int[] oldTile, int[] newTile, int noOfUnits,
			int noOfMovesLeft) throws IllegalMoveException,
			InsufficientUnitsException, NotYourTurnException
	{
		// Check for various exceptions
		int noOfMoves = 0;
		for (int coordinate = 0; coordinate < oldTile.length; coordinate++)
			if (oldTile[coordinate] != newTile[coordinate])
				noOfMoves++;
		if (noOfMoves > 1)
			throw new IllegalMoveException(
					"Can't move more than 1 space at a time");
		else if (noOfMoves == 0)
			throw new IllegalMoveException("No move made");
		if (noOfUnits > tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].noOfUnits[noOfMovesLeft])
			throw new InsufficientUnitsException(
					tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].noOfUnits[noOfMovesLeft],
					noOfUnits,
					tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].controller);
		if (noOfMovesLeft < 1)
			throw new IllegalMoveException("Units must have moves remaining");
		if (tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].controller != currentPlayer)
			throw new NotYourTurnException(
					currentPlayer,
					tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].controller);
		if (noOfUnits < 0)
		{
			System.exit(0); // debug
			throw new InsufficientUnitsException(
					tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].noOfUnits[noOfMovesLeft],
					noOfUnits,
					tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].controller);
		}

		// Move units from starting tile
		tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].noOfUnits[noOfMovesLeft] -= noOfUnits;

		System.out
				.printf(
						"\n Player %d has moved %d units from tile %d, %d, %d, %d to tile %d, %d, %d, %d",
						tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].controller,
						noOfUnits, oldTile[0], oldTile[1], oldTile[2],
						oldTile[3], newTile[0], newTile[1], newTile[2],
						newTile[3]); // debug

		// Starts a battle if two tiles have different controllers
		if (tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].controller != tiles[newTile[0]][newTile[1]][newTile[2]][newTile[3]].controller)
			battle(tiles[newTile[0]][newTile[1]][newTile[2]][newTile[3]],
					tiles[oldTile[0]][oldTile[1]][oldTile[2]][oldTile[3]].controller,
					noOfUnits);
		else
			tiles[newTile[0]][newTile[1]][newTile[2]][newTile[3]].noOfUnits[0] += noOfUnits;
	}

	/**
	 * Goes through the number of tiles controlled by each player counting all
	 * of the non-0 values
	 * @return the number of players with at least one tile controlled
	 */
	public void noOfPlayersRemaining()
	{
		numPlayersRemaining = 0;
		for (int player = 0; player < players.length; player++)
			if (players[player].noOfTilesControlled > 0)
				numPlayersRemaining++;
	}

	/**
	 * Checks to see who controls a given Q-Line and updates unit production DO
	 * NOT CALL THIS METHOD IF YOU HAVE NOT RESET unitProduction
	 * @param x The x location of the column being checked
	 * @param z The z location of the column being checked
	 * @param q The q location of the column being checked
	 * @return who controls the column, -1 if no single player controls the
	 *         column
	 */
	private int checkQLine(int x, int y, int z)
	{
		int columnController = tiles[x][y][z][0].controller;

		// Check if the first tile is vacant
		if (columnController == -1)
			return -1;

		// Go through all tiles in the column and check whether they are
		// controlled by the same player
		for (int q = 1; q < tiles[0][0][0].length; q++)
		{
			if (columnController != tiles[x][y][z][q].controller)
				return -1;
		}
		players[columnController].unitProduction += tiles[0][0][0].length;
		return columnController - 1; // -1 adjusts for the unit production array
										// having not including vacant tiles
										// (while the controller variable does)
	}

	/**
	 * Checks to see who controls a given Z-line and updates unit production DO
	 * NOT CALL THIS METHOD IF YOU HAVE NOT RESET unitProduction
	 * @param x The x location of the column being checked
	 * @param y The z location of the column being checked
	 * @param q The q location of the column being checked
	 * @return who controls the column, -1 if no single player controls the
	 *         column
	 */
	private int checkZLine(int x, int y, int q)
	{
		int columnController = tiles[x][y][0][q].controller;

		// Check if the first tile is vacant
		if (columnController == -1)
			return -1;

		// Go through all tiles in the column and check whether they are
		// controlled by the same player
		for (int z = 1; z < tiles[0][0].length; z++)
		{
			if (columnController != tiles[x][y][z][q].controller)
				return -1;
		}
		players[columnController].unitProduction += tiles[0][0].length;
		return columnController - 1; // -1 adjusts for the unit production array
										// having not including vacant tiles
										// (while the controller variable does)
	}

	/**
	 * Checks to see who controls a given row and updates unit production DO NOT
	 * CALL THIS METHOD IF YOU HAVE NOT RESET unitProduction
	 * @param x The x location of the column being checked
	 * @param z The z location of the column being checked
	 * @param q The q location of the column being checked
	 * @return who controls the column, -1 if no single player controls the
	 *         column
	 */
	private int checkRow(int y, int z, int q)
	{
		int columnController = tiles[0][y][z][q].controller;

		// Check if the first tile is vacant
		if (columnController == -1)
			return -1;

		// Go through all tiles in the column and check whether they are
		// controlled by the same player
		for (int x = 1; x < tiles.length; x++)
		{
			if (columnController != tiles[x][y][z][q].controller)
				return -1;
		}
		players[columnController].unitProduction += tiles.length;
		return columnController - 1; // -1 adjusts for the unit production array
										// having not including vacant tiles
										// (while the controller variable does)
	}

	/**
	 * Checks to see who controls a given column and updates unit production DO
	 * NOT CALL THIS METHOD IF YOU HAVE NOT RESET unitProduction
	 * @param x The x location of the column being checked
	 * @param z The z location of the column being checked
	 * @param q The q location of the column being checked
	 * @return who controls the column, -1 if no single player controls the
	 *         column
	 */
	private int checkColumn(int x, int z, int q)
	{
		int columnController = tiles[x][0][z][q].controller;

		// Check if the first tile is vacant
		if (columnController == -1)
			return -1;

		// Go through all tiles in the column and check whether they are
		// controlled by the same player
		for (int y = 1; y < tiles[0].length; y++)
		{
			if (columnController != tiles[x][y][z][q].controller)
				return -1;
		}
		players[columnController].unitProduction += tiles[0].length;
		return columnController - 1; // -1 adjusts for the unit production array
										// having not including vacant tiles
										// (while the controller variable does)
	}

	/**
	 * Updates unit production on straight lines for all players
	 */
	private void updateUnitProductionStraight()
	{
		// Go through all possible columns and check if they are controlled by a
		// single player
		for (int x = 0; x < tiles.length; x++)
			for (int z = 0; z < tiles[0][0].length; z++)
				for (int q = 0; q < tiles[0][0][0].length; q++)
					checkColumn(x, z, q);

		// Go through all possible rows and check if they are controlled by a
		// single player
		for (int y = 0; y < tiles[0].length; y++)
			for (int z = 0; z < tiles[0][0].length; z++)
				for (int q = 0; q < tiles[0][0][0].length; q++)
					checkRow(y, z, q);

		// Go through all possible z-lines and check if they are controlled by a
		// single player (if applicable)
		if (tiles[0][0].length > 1)
		{
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles[0].length; y++)
					for (int q = 0; q < tiles[0][0][0].length; q++)
						checkZLine(x, y, q);
		}

		// Go through all possible q-lines and check if they are controlled by a
		// single player (if applicable)
		if (tiles[0][0][0].length > 1)
		{
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles[0].length; y++)
					for (int z = 0; z < tiles[0][0].length; z++)
						checkQLine(x, y, z);
		}
	}

	/**
	 * Checks 2D diagonal lines for bonus units
	 * @param planeDimention1 Refers to plane we are on
	 * @param planeDimention2 Refers to which type of plane we are on (this
	 *            number should be greater than planeDimention1)
	 */
	private void check2dDiagonal(int planeDimention1, int planeDimention2)
	{
		if (planeDimention1 == 0)// Cross is in the x* plane
		{
			if (planeDimention2 == 1)// Cross is in the xy plane
			{
				for (int z = 0; z < tiles[0][0].length; z++)
				{
					for (int q = 0; q < tiles[0][0][0].length; q++)
					{
						// Start at top right corner of diagonal
						int columnController = tiles[0][0][z][q].controller;

						boolean sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[i][i][z][q].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;

						// Start at top left corner of diagonal
						columnController = tiles[tiles.length - 1][0][z][q].controller;

						sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[tiles.length - i - 1][i][z][q].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;
					}
				}
			}
			if (planeDimention2 == 2)// Cross is in the xz plane
			{
				for (int y = 0; y < tiles[0].length; y++)
				{
					for (int q = 0; q < tiles[0][0][0].length; q++)
					{
						// Start at top right corner of diagonal
						int columnController = tiles[0][y][tiles.length - 1][q].controller;

						boolean sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[i][y][tiles.length
									- 1 - i][q].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;

						// Start at top left corner of diagonal
						columnController = tiles[tiles.length - 1][y][tiles.length - 1][q].controller;

						sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[tiles.length - i - 1][y][tiles.length
									- 1 - i][q].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;
					}
				}
			}
			if (planeDimention2 == 3) // The cross exists in a xq plane
			{
				for (int y = 0; y < tiles[0].length; y++)
				{
					for (int z = 0; z < tiles[0][0][0].length; z++)
					{
						// Start at top right corner of diagonal
						int columnController = tiles[0][y][z][0].controller;

						boolean sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[i][y][z][i].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;

						// Start at top left corner of diagonal
						columnController = tiles[tiles.length - 1][y][z][0].controller;

						sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[tiles.length - i - 1][y][z][i].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;
					}
				}
			}
		}
		if (planeDimention1 == 1) // Case is in the y* plane
		{
			if (planeDimention2 == 2) // Case is in the yz plane
			{
				for (int x = 0; x < tiles[0][0][0].length; x++)
				{
					for (int q = 0; q < tiles[0][0][0].length; q++)
					{
						// Start at top right corner of diagonal
						int columnController = tiles[x][0][tiles.length - 1][q].controller;

						boolean sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal
							// has multiple controllers
							if (columnController != tiles[x][i][tiles.length
									- 1
									- i][q].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;

						// Start at top left corner of diagonal
						columnController = tiles[x][0][0][q].controller;

						sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[x][i][i][q].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;
					}
				}
			}
			if (planeDimention2 == 3) // Case is in the yq plane
			{
				for (int x = 0; x < tiles[0][0][0].length; x++)
				{
					for (int z = 0; z < tiles[0][0][0].length; z++)
					{
						// Start at top right corner of diagonal
						int columnController = tiles[x][tiles.length - 1][z][0].controller;

						boolean sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal
							// has multiple controllers
							if (columnController != tiles[x][tiles.length - 1
									- i][z][i].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;

						// Start at top left corner of diagonal
						columnController = tiles[x][0][z][0].controller;

						sameController = true;
						// Work through the diagonal
						for (int i = 1; i < tiles.length; i++)
						{
							// Exit loop if the first tile is vacant or the
							// diagonal has multiple controllers
							if (columnController != tiles[x][i][z][i].controller
									|| columnController == -1)
							{
								sameController = false;
								break;
							}
						}
						// If all of the tiles have the same controller,
						// distribute the bonus
						if (sameController)
							players[columnController].unitProduction += tiles.length;
					}
				}
			}
		}
		if (planeDimention1 == 2) // Case is in the zq plane
		{
			for (int x = 0; x < tiles[0][0][0].length; x++)
			{
				for (int y = 0; y < tiles[0][0][0].length; y++)
				{
					// Start at top right corner of diagonal
					int columnController = tiles[x][y][0][0].controller;

					boolean sameController = true;
					// Work through the diagonal
					for (int i = 1; i < tiles.length; i++)
					{
						// Exit loop if the first tile is vacant or the
						// diagonal
						// has multiple controllers
						if (columnController != tiles[x][y][i][i].controller
								|| columnController == -1)
						{
							sameController = false;
							break;
						}
					}
					// If all of the tiles have the same controller,
					// distribute the bonus
					if (sameController)
						players[columnController].unitProduction += tiles.length;

					// Start at top left corner of diagonal
					columnController = tiles[x][y][tiles.length - 1][0].controller;

					sameController = true;
					// Work through the diagonal
					for (int i = 1; i < tiles.length; i++)
					{
						// Exit loop if the first tile is vacant or the
						// diagonal has multiple controllers
						if (columnController != tiles[x][y][tiles.length - 1
								- i][i].controller || columnController == -1)
						{
							sameController = false;
							break;
						}
					}
					// If all of the tiles have the same controller,
					// distribute the bonus
					if (sameController)
						players[columnController].unitProduction += tiles.length;
				}
			}
		}
	}

	/**
	 * Updates unit production on all diagonals that go through only 2
	 * dimensions
	 */
	private void updateUnitProduction2dDiagonal()
	{
		// Go through all possible 2-d diagonals moving the center being checked
		// through z-space
		for (int z = 0; z < tiles[0][0].length; z++)
			check2dDiagonal(0, 1);

		// If the board is 3-D, check diagonals on the xz and yz planes
		if (tiles[0][0].length > 1)
		{
			check2dDiagonal(0, 2);
			check2dDiagonal(1, 2);
		}

		// If the board is 4-D, check diagonals on the xq and yq and zq planes
		if (tiles[0][0][0].length > 1)
		{
			check2dDiagonal(0, 3);
			check2dDiagonal(1, 3);
			check2dDiagonal(2, 3);
		}
	}

	/**
	 * Checks 3d lines for bonus units
	 * @param tiles a reloaded array where the last dimension is the dimension
	 *            that remains constant for each diagonal
	 */
	private void check3dDiagonal(Tile[][][][] tiles)
	{
		for (int q = 0; q < tiles[0][0][0].length; q++)
		{
			// Start at top, right, near corner of diagonal
			int columnController = tiles[0][0][0][q].controller;

			boolean sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal
				// has multiple controllers
				if (columnController != tiles[i][i][i][q].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top right far corner of diagonal
			columnController = tiles[0][0][tiles.length - 1][q].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[i][i][tiles.length - 1 - i][q].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top left near corner of diagonal
			columnController = tiles[0][tiles.length - 1][0][q].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[i][tiles.length - 1 - i][i][q].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top left far corner of diagonal
			columnController = tiles[0][tiles.length - 1][tiles.length - 1][q].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[i][tiles.length - 1 - i][tiles.length
						- 1 - i][q].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;
		}
	}

	/**
	 * Updates unit production on all diagonals that go through only 3
	 * dimensions
	 */
	private void updateUnitProduction3dDiagonal()
	{
		// If the board is 3-D, check diagonals in the xyz hypersurface
		if (tiles[0][0].length > 1)
			check3dDiagonal(tiles);

		// If the board is 4-D, check the diagonals on the yzq, xzq, and zyq
		// cubes
		if (tiles[0][0][0].length > 1)
		{
			// Reload array so that the last dimension is x
			Tile[][][][] reloadedArray = new Tile[tiles.length][tiles.length][tiles.length][tiles.length];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reloadedArray[y][z][q][x] = tiles[x][y][z][q];
			check3dDiagonal(reloadedArray);

			// Reload array so that the last dimension is y
			reloadedArray = new Tile[tiles.length][tiles.length][tiles.length][tiles.length];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reloadedArray[x][z][q][y] = tiles[x][y][z][q];
			check3dDiagonal(reloadedArray);

			// Reload array so that the last dimension is z
			reloadedArray = new Tile[tiles.length][tiles.length][tiles.length][tiles.length];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reloadedArray[x][y][q][z] = tiles[x][y][z][q];
			check3dDiagonal(reloadedArray);
		}
	}

	/**
	 * Updates the unit production for all diagonals that go through 5
	 * dimensions
	 */
	private void updateUnitProduction4dDiagonal()
	{
		if (tiles[0][0][0].length > 1)
		{
			// Start at top, right, near corner of diagonal
			int columnController = tiles[0][0][0][0].controller;

			boolean sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal
				// has multiple controllers
				if (columnController != tiles[i][i][i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top right far corner of diagonal
			columnController = tiles[0][0][tiles.length - 1][0].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[i][i][tiles.length - 1 - i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top left near corner of diagonal
			columnController = tiles[0][tiles.length - 1][0][0].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[i][tiles.length - 1 - i][i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top left far corner of diagonal
			columnController = tiles[0][tiles.length - 1][tiles.length - 1][0].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[i][tiles.length - 1 - i][tiles.length
						- 1 - i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top, right, near corner of diagonal
			columnController = tiles[tiles.length - 1][0][0][0].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal
				// has multiple controllers
				if (columnController != tiles[tiles.length - 1 - i][i][i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top right far corner of diagonal
			columnController = tiles[tiles.length - 1][0][tiles.length - 1][0].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[tiles.length - 1 - i][i][tiles.length
						- 1 - i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top left near corner of diagonal
			columnController = tiles[tiles.length - 1][tiles.length - 1][0][0].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[tiles.length - 1 - i][tiles.length
						- 1 - i][i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;

			// Start at top left far corner of diagonal
			columnController = tiles[tiles.length - 1][tiles.length - 1][tiles.length - 1][0].controller;

			sameController = true;
			// Work through the diagonal
			for (int i = 1; i < tiles.length; i++)
			{
				// Exit loop if the first tile is vacant or the
				// diagonal has multiple controllers
				if (columnController != tiles[tiles.length - 1 - i][tiles.length
						- 1 - i][tiles.length - 1 - i][0].controller
						|| columnController == -1)
				{
					sameController = false;
					break;
				}
			}
			// If all of the tiles have the same controller,
			// distribute the bonus
			if (sameController)
				players[columnController].unitProduction += tiles.length;
		}
	}

	/**
	 * Updates unit production on diagonals for all players
	 */
	private void updateUnitProductionDiagonal()
	{
		// Check 2-dimensional diagonals
		updateUnitProduction2dDiagonal();

		// Check 3-dimensional diagonals
		updateUnitProduction3dDiagonal();

		// Check 4-dimensional diagonals
		updateUnitProduction4dDiagonal();
	}

	/**
	 * Updates unit production for all players
	 */
	public void updateUnitProduction()
	{
		// First set production to number of tiles controlled
		for (int player = 0; player < players.length; player++)
			players[player].unitProduction = players[player].noOfTilesControlled;

		// Add bonuses for controlling lines
		updateUnitProductionStraight();
		updateUnitProductionDiagonal();

		// Determine the total production of all players
		for (int player = 0; player < players.length; player++)
		{
			totalUnitProduction += players[player].unitProduction;

			// Give the player the human bonus if they are human
			if (player >= noOfComputers)
				totalUnitProduction = (int) Math.ceil(totalUnitProduction
						* playerEconomicBonus);
		}

		// Determine who the strongest player is
		strongestPlayer = 0;
		for (int player = 1; player < players.length; player++)
			if (players[player].unitProduction > players[strongestPlayer].unitProduction)
				strongestPlayer = player;

	}

	/**
	 * 
	 * @param player the player who we are finding the remaining units of
	 * @return The remaining units that the given player can place this turn
	 */
	public int getUnitsRemaining(int player)
	{
		return players[player].unitsRemaining;
	}

	/**
	 * @param player the player who's production we are finding
	 * @return the number of units that a given player creates in a turn
	 */
	public int getUnitProduction(int player)
	{
		return players[player].unitProduction;
	}

	/**
	 * @return The total production of all players
	 */
	public int getTotalProduction()
	{
		return totalUnitProduction;
	}

	/**
	 * @param player The player who's tiles we are counting
	 * @return The number of tiles that the specified player controls
	 */
	public int getNoOfTilesControlled(int player)
	{
		return players[player].noOfTilesControlled;
	}

	/**
	 * Finds all tiles owned by the given player which are adjacent to an
	 * opposing player's tiles
	 */
	private void determineBoarderTiles()
	{
		// Searches for all tiles which are controlled by the AI and checks
		// whether or not any of the adjacent tiles are controlled by another
		// player. The coordinates of all boarder tiles are stored in a list.
		// Differentiates between border tiles and tiles that border an enemy:
		// all bordersEnemy tiles are border tiles but not all isBorderTile
		// tiles are bordersEnemy tiles
		//
		// The comparison with the tiles.length prevents array out of bounds
		// errors
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				for (int z = 0; z < tiles[0][0].length; z++)
					for (int q = 0; q < tiles[0][0][0].length; q++)
					{
						tiles[x][y][z][q].isBorderTile = (x + 1 < tiles.length
								&& tiles[x + 1][y][z][q].controller != tiles[x][y][z][q].controller)
								|| (y + 1 < tiles[0].length
								&& tiles[x][y + 1][z][q].controller != tiles[x][y][z][q].controller)
								|| (z + 1 < tiles[0][0].length
								&& tiles[x][y][z + 1][q].controller != tiles[x][y][z][q].controller)
								|| (q + 1 < tiles[0][0][0].length
								&& tiles[x][y][z][q + 1].controller != tiles[x][y][z][q].controller)
								|| (x > 0
								&& tiles[x - 1][y][z][q].controller != tiles[x][y][z][q].controller)
								|| (y > 0
								&& tiles[x][y - 1][z][q].controller != tiles[x][y][z][q].controller)
								|| (z > 0
								&& tiles[x][y][z - 1][q].controller != tiles[x][y][z][q].controller)
								|| (q > 0
								&& tiles[x][y][z][q - 1].controller != tiles[x][y][z][q].controller);
						tiles[x][y][z][q].bordersEnemy = (x + 1 < tiles.length
								&& tiles[x + 1][y][z][q].controller != tiles[x][y][z][q].controller && tiles[x + 1][y][z][q].controller != -1)
								|| (y + 1 < tiles[0].length
										&& tiles[x][y + 1][z][q].controller != tiles[x][y][z][q].controller && tiles[x][y + 1][z][q].controller != -1)
								|| (z + 1 < tiles[0][0].length
										&& tiles[x][y][z + 1][q].controller != tiles[x][y][z][q].controller && tiles[x][y][z + 1][q].controller != -1)
								|| (q + 1 < tiles[0][0][0].length
										&& tiles[x][y][z][q + 1].controller != tiles[x][y][z][q].controller && tiles[x][y][z][q + 1].controller != -1)
								|| (x > 0
										&& tiles[x - 1][y][z][q].controller != tiles[x][y][z][q].controller && tiles[x - 1][y][z][q].controller != -1)
								|| (y > 0
										&& tiles[x][y - 1][z][q].controller != tiles[x][y][z][q].controller && tiles[x][y - 1][z][q].controller != -1)
								|| (z > 0
										&& tiles[x][y][z - 1][q].controller != tiles[x][y][z][q].controller && tiles[x][y][z - 1][q].controller != -1)
								|| (q > 0
										&& tiles[x][y][z][q - 1].controller != tiles[x][y][z][q].controller && tiles[x][y][z][q - 1].controller != -1);
					}
	}

	/**
	 * Updates the board and resets counters
	 * 
	 * The person calling this method needs to do a couple of things: - Deal
	 * with human player moves if (players[currentPlayer].noOfTilesControlled >
	 * 0) - updateGame before each human player moves - checkForWinner after all
	 * players have made their turn - if (winner != -1) endOfGameAnalysis();
	 */
	public void nextRound()
	{
		updateGame();
		resetCounters(); // This should come after the game has been updated
		turnNo++;

		// Have each player make their move
		for (currentPlayer = 0; currentPlayer < noOfComputers; currentPlayer++)
		{
			updateGame();
			players[currentPlayer].unitsRemaining = players[currentPlayer].unitProduction;
			if (players[currentPlayer].noOfTilesControlled > 0)
				players[currentPlayer].makeNextMove();
		}
	}

	/**
	 * Analysis the results of the game for future AIs, if there is not file to
	 * read data from, will create a new data file
	 * 
	 */
	public void endOfGameAnalysis()
	{
		try
		{
			// Look at winner aggressiveness factor and add that to the running
			// average
			double winnerAggressivnessPremium = (players[winner])
					.getAggressivnessPremium();

			Scanner readFile = new Scanner(new File("aiProperties.txt"));
			double averageAggressivness = readFile.nextDouble();
			int numberOfSamplesInAverage = readFile.nextInt();
			double newAverageAggressivness = (averageAggressivness
					* numberOfSamplesInAverage + winnerAggressivnessPremium)
					/ (numberOfSamplesInAverage + 1);

			// Write new data to file
			PrintWriter writer = new PrintWriter("aiProperties.txt");
			writer.println(newAverageAggressivness);
			// writer.println(numberOfSamplesInAverage + 1);
			writer.println(100);

			writer.close();
			readFile.close();

			System.out.println(winnerAggressivnessPremium);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block: should create a new data file
			e.printStackTrace();
		}

	}

	/**
	 * Determines the game's winner, if there is any
	 */
	public void checkForWinner()
	{
		if (numPlayersRemaining == 1)
			for (int player = 0; winner == -1; player++)
				if (players[player].noOfTilesControlled > 0)
					winner = player;
	}

	/**
	 * 
	 * @return The winner of the game, or -1 if the game has no winner
	 */
	public int getWinner()
	{
		return winner;
	}

	/**
	 * Updates information about the board
	 */
	public void updateGame()
	{
		determineBoarderTiles();
		updateUnitProduction();
		noOfPlayersRemaining();
	}

	/**
	 * Resets unit production and replenishes movement points
	 */
	private void resetCounters()
	{
		// Reset unit production
		for (int player = 0; player < players.length; player++)
			players[player].unitsRemaining = players[player].unitProduction;

		// Replenish movement points
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				for (int z = 0; z < tiles[0][0].length; z++)
					for (int q = 0; q < tiles[0][0][0].length; q++)
					{
						tiles[x][y][z][q].noOfUnits[1] += tiles[x][y][z][q].noOfUnits[0];
						tiles[x][y][z][q].noOfUnits[0] = 0;
					}
	}

	/**
	 * 
	 * @return The number of players who have not yet been eliminated
	 */
	public int getNumPlayersRemaining()
	{
		return numPlayersRemaining;
	}

	/**
	 * @return A map of the board
	 */
	public Tile[][][][] getTiles()
	{
		return tiles;
	}

	/**
	 * 
	 * @param tileLocation The array indices of the tile
	 * @return The specified tile
	 */
	public Tile getTile(int[] tileLocation)
	{
		return tiles[tileLocation[0]][tileLocation[1]][tileLocation[2]][tileLocation[3]];
	}

	/**
	 * @param q Which 3d space to return
	 * @return The a map of the specified area in 3d space of the game's 4d map
	 */
	public Tile[][][] get3DMap(int q)
	{
		Tile[][][] map = new Tile[tiles.length][tiles[0].length][tiles[0][0].length];
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				for (int z = 0; z < tiles[0][0].length; z++)
					map[x][y][z] = tiles[x][y][z][q];
		return map;
	}

	/**
	 * @param z Refers to the z-dimension of the desired plane
	 * @param q Refers to the q-dimension of the desired plane
	 * @return The a map of the specified area in 3d space of the game's 4d map
	 */
	public Tile[][] get2DMap(int z, int q)
	{
		Tile[][] map = new Tile[tiles.length][tiles[0].length];
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				map[x][y] = tiles[x][y][z][q];
		return map;
	}

	/**
	 * 
	 * @return the defensive bonus
	 */
	public double getDefensiveBonus()
	{
		return defensiveBonus;
	}

	/**
	 * 
	 * @return the moves per turn
	 */
	public int getMovesPerTurn()
	{
		return movesPerTurn;
	}

	/**
	 * 
	 * @return the number of players at the start of the game
	 */
	public int getNoOfPlayers()
	{
		return noOfPlayers;
	}

	/**
	 * 
	 * @return the number of ai's
	 */
	public int getNoComputers()
	{
		return noOfComputers;
	}

	/**
	 * 
	 * @return the currentPlayer
	 */
	public int getCurrentPlayer()
	{
		return currentPlayer;
	}

	/**
	 * 
	 * @param player the player to check units reaming
	 * @return the units left to be placed
	 */
	public int unitsRemaing(int player)
	{
		return players[player].unitsRemaining;
	}

	/**
	 * Checks if 2 tiles are adjacent
	 * @param tile1 coordinates of first tile
	 * @param tile2 coordinates of second tile
	 * @return
	 */
	public boolean checkAdjacent(int[] tile1, int[] tile2)
	{
		int x = 0;
		int y = 1;
		int z = 2;
		int q = 3;

		if ((tile1[x] + 1 == tile2[x] && tile1[y] == tile2[y]
				&& tile1[z] == tile2[z] && tile1[q] == tile2[q]) ||

				(tile1[x] - 1 == tile2[x] && tile1[y] == tile2[y]
						&& tile1[z] == tile2[z] && tile1[q] == tile2[q]) ||

				(tile1[x] == tile2[x] && tile1[y] + 1 == tile2[y]
						&& tile1[z] == tile2[z] && tile1[q] == tile2[q]) ||

				(tile1[x] == tile2[x] && tile1[y] - 1 == tile2[y]
						&& tile1[z] == tile2[z] && tile1[q] == tile2[q]) ||

				(tile1[x] == tile2[x] && tile1[y] == tile2[y]
						&& tile1[z] + 1 == tile2[z] && tile1[q] == tile2[q]) ||

				(tile1[x] == tile2[x] && tile1[y] == tile2[y]
						&& tile1[z] - 1 == tile2[z] && tile1[q] == tile2[q]) ||

				(tile1[x] == tile2[x] && tile1[y] == tile2[y]
						&& tile1[z] == tile2[z] && tile1[q] + 1 == tile2[q]) ||

				(tile1[x] == tile2[x] && tile1[y] == tile2[y]
						&& tile1[z] == tile2[z] && tile1[q] - 1 == tile2[q]))
		{
			return true;
		}

		return false;

	}

	/**
	 * Gets the turn number
	 * @return the turn number
	 */
	public int getNoTurns()
	{
		return turnNo;
	}

	/**
	 * Note: does not refresh game info; only as up-to-date as the most recent
	 * game update (most likely the most recent player's turn)
	 * @return The index of the strongest player
	 */
	public int getStrongestPlayer()
	{
		return strongestPlayer;
	}

}
