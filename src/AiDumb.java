import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Contains all of the methods pertaining to AI behavior
 * 
 * This AI has been made significantly simpler to make it easier to defeat
 * 
 * @author Logan Grier
 * @version 15/01/2015
 */
public class AiDumb extends Player
{
	private class DetermineTileValue
	{
		private int[] tileCoordinates;
		double contructiveTileValue = 0;
		int numberOfEnemyBonuses = 0;

		/**
		 * Determines the value of attacking a given tile. Only has partial
		 * support for 4-D boards (diagonals moving through all four dimensions
		 * are NOT supported)
		 * @param keepTile true if we are keeping the tile, false if we are not
		 *            (are we just doing this to disrupt their bonuses or does
		 *            this provide a more direct benefit)
		 * @param tileCoordinates The coordinates of the tile to evaluate
		 * @return The value of the tile
		 */
		public double main(int[] tileCoordinate, boolean keepTile)
		{
			tileCoordinates = tileCoordinate;
			contructiveTileValue = 0;
			numberOfEnemyBonuses = 0;

			// Go through all bonuses that pass through this tile, and check if
			// any other player controls the entire bonus
			// boolean allEnemyControlled =
			// tiles[0][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller
			// != -1;
			// Makes sure that a bonus controlled entirely by uncontrolled does
			// not count

			// Check the row/column moving through x-space
			int firstController = tiles[0][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller;
			int numberOfAiControlled = 0;
			boolean allEnemyControlled = tiles[0][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller != -1;
			for (int x = 0; x < tiles.length; x++)
			{
				// Counts what portion of the bonus is controlled by this player
				if (tiles[x][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller == playerNumber)
					numberOfAiControlled++;

				// Checks if the entire bonus is controlled by an enemy
				if (allEnemyControlled
						&& tiles[x][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller != firstController)
					allEnemyControlled = false;
			}
			// Update the counter of complete enemy bonuses
			if (allEnemyControlled)
				numberOfEnemyBonuses++;
			// Update the tile value based on the number of tiles controlled by
			// this player
			contructiveTileValue += (numberOfAiControlled + 1)
					* tiles.length;

			// Check the rows/column moving through y-space
			firstController = tiles[tileCoordinates[0]][0][tileCoordinates[2]][tileCoordinates[3]].controller;
			numberOfAiControlled = 0;
			allEnemyControlled = tiles[tileCoordinates[0]][0][tileCoordinates[2]][tileCoordinates[3]].controller != -1;
			for (int y = 0; y < tiles.length; y++)
			{
				// Counts what portion of the bonus is controlled by this player
				if (tiles[tileCoordinates[0]][y][tileCoordinates[2]][tileCoordinates[3]].controller == playerNumber)
					numberOfAiControlled++;

				// Checks if the entire bonus is controlled by an enemy
				if (allEnemyControlled
						&& tiles[tileCoordinates[0]][y][tileCoordinates[2]][tileCoordinates[3]].controller != firstController)
					allEnemyControlled = false;
			}
			// Update the counter of complete enemy bonuses
			if (allEnemyControlled)
				numberOfEnemyBonuses++;
			// Update the tile value based on the number of tiles controlled by
			// this player
			contructiveTileValue += (numberOfAiControlled + 1)
					* tiles.length;

			// Check the rows/column moving through z-space
			if (tiles[0][0].length > 1)
			{
				firstController = tiles[tileCoordinates[0]][tileCoordinates[1]][0][tileCoordinates[3]].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[tileCoordinates[0]][tileCoordinates[1]][0][tileCoordinates[3]].controller != -1;
				for (int z = 0; z < tiles.length; z++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[tileCoordinates[0]][tileCoordinates[1]][z][tileCoordinates[3]].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[tileCoordinates[0]][tileCoordinates[1]][z][tileCoordinates[3]].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;
			}

			// Check the rows/column moving through q-space
			if (tiles[0][0][0].length > 1)
			{
				firstController = tiles[tileCoordinates[0]][tileCoordinates[1]][tileCoordinates[2]][0].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[tileCoordinates[0]][tileCoordinates[1]][tileCoordinates[2]][0].controller != -1;
				for (int q = 0; q < tiles.length; q++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[tileCoordinates[0]][tileCoordinates[1]][tileCoordinates[2]][q].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[tileCoordinates[0]][tileCoordinates[1]][tileCoordinates[2]][q].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;
			}

			// Check for diagonals going through the xy plane
			Tile[][] reloadedTiles = new Tile[tiles.length][tiles.length];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles.length; y++)
					reloadedTiles[x][y] = tiles[x][y][tileCoordinates[2]][tileCoordinates[3]];
			checkFor2dDiagonal(tileCoordinates[0], tileCoordinates[1],
					reloadedTiles);

			// Check any diagonals on the xz and yz planes, and xyz space //
			if (tiles[0][0].length > 1)
			{
				// Check any diagonals on the xz plane
				reloadedTiles = new Tile[tiles.length][tiles.length];
				for (int x = 0; x < tiles.length; x++)
					for (int z = 0; z < tiles.length; z++)
						reloadedTiles[x][z] = tiles[x][tileCoordinates[1]][z][tileCoordinates[3]];
				checkFor2dDiagonal(tileCoordinates[0], tileCoordinates[2],
						reloadedTiles);

				// Check any diagonals on the yz plane
				reloadedTiles = new Tile[tiles.length][tiles.length];
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						reloadedTiles[y][z] = tiles[tileCoordinates[0]][y][z][tileCoordinates[3]];
				checkFor2dDiagonal(tileCoordinates[1], tileCoordinates[2],
						reloadedTiles);

				// Check any diagonals in xyz space
				Tile[][][] reloadedTiles3d = new Tile[tiles.length][tiles.length][tiles.length];
				for (int x = 0; x < tiles.length; x++)
					for (int y = 0; y < tiles.length; y++)
						for (int z = 0; z < tiles.length; z++)
							reloadedTiles3d[x][y][z] = tiles[x][y][z][tileCoordinates[3]];
				checkFor3dDiagonal(reloadedTiles3d);
			}

			// If the board is 4-D check any diagonals that go through q-space
			if (tiles[0][0][0].length > 1)
			{
				// Check any diagonals on the xq plane
				reloadedTiles = new Tile[tiles.length][tiles.length];
				for (int x = 0; x < tiles.length; x++)
					for (int q = 0; q < tiles.length; q++)
						reloadedTiles[x][q] = tiles[x][tileCoordinates[1]][tileCoordinates[2]][q];
				checkFor2dDiagonal(tileCoordinates[0], tileCoordinates[3],
						reloadedTiles);

				// Check any diagonals on the yq plane
				reloadedTiles = new Tile[tiles.length][tiles.length];
				for (int y = 0; y < tiles.length; y++)
					for (int q = 0; q < tiles.length; q++)
						reloadedTiles[y][q] = tiles[tileCoordinates[0]][y][tileCoordinates[2]][q];
				checkFor2dDiagonal(tileCoordinates[1], tileCoordinates[3],
						reloadedTiles);

				// Check any diagonals on the zq plane
				reloadedTiles = new Tile[tiles.length][tiles.length];
				for (int z = 0; z < tiles.length; z++)
					for (int q = 0; q < tiles.length; q++)
						reloadedTiles[z][q] = tiles[tileCoordinates[0]][tileCoordinates[1]][z][q];
				checkFor2dDiagonal(tileCoordinates[2], tileCoordinates[3],
						reloadedTiles);

				// Check any diagonals on the yzq hypersurface
				Tile[][][] reloadedTiles3d = new Tile[tiles.length][tiles.length][tiles.length];
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reloadedTiles3d[y][z][q] = tiles[tileCoordinates[0]][y][z][q];
				checkFor3dDiagonal(reloadedTiles3d);

				// Check any diagonals on the xzq hypersurface
				reloadedTiles3d = new Tile[tiles.length][tiles.length][tiles.length];
				for (int x = 0; x < tiles.length; x++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reloadedTiles3d[x][z][q] = tiles[z][tileCoordinates[1]][z][q];
				checkFor3dDiagonal(reloadedTiles3d);

				// Check any diagonals on the xyq hypersurface
				reloadedTiles3d = new Tile[tiles.length][tiles.length][tiles.length];
				for (int x = 0; x < tiles.length; x++)
					for (int y = 0; y < tiles.length; y++)
						for (int q = 0; q < tiles.length; q++)
							reloadedTiles3d[x][y][q] = tiles[x][y][tileCoordinates[2]][q];
				checkFor3dDiagonal(reloadedTiles3d);

				// Check any diagonals that go through all 4 dimensions
				check4dDiagonal();
			}

			// Make the constructive value equal to the % gain in
			// production/turn
			contructiveTileValue = contructiveTileValue
					/ board.getUnitProduction(playerNumber);

			// Make the destructive value equal to the percentage loss in total
			// enemy production
			double destructiveValue = (numberOfEnemyBonuses * tiles.length);

			// Take into account the loss in unit production by taking away just
			// the tile (1 unit/turn)
			if (board.getTile(tileCoordinate).controller != -1)
				destructiveValue++;

			// Add a random factor to make the AI dumber
			Random randomNumberGenerator = new Random();

			// Only count the constructive value if we plan on keeping this tile
			if (keepTile)
				return (contructiveTileValue + destructiveValue)
						* Math.exp(randomNumberGenerator.nextGaussian() / 10);
			else
				return destructiveValue
						* Math.exp(randomNumberGenerator.nextGaussian() / 10);
		}

		private void checkFor2dDiagonal(int tileCoordinatesX,
				int tileCoordiantesY, Tile[][] tiles)
		{
			int firstController, numberOfAiControlled;
			boolean allEnemyControlled;

			// Diagonals of the form y=x
			if (tileCoordinatesX == tileCoordiantesY)
			{
				firstController = tiles[0][0].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][0].controller != -1;
				for (int xy = 0; xy < tiles.length; xy++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xy][xy].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[xy][xy].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}

			// Diagonals of the form y=-x
			if (tileCoordinatesX + tileCoordiantesY == tiles.length - 1)
			{
				firstController = tiles[0][tiles[0].length - 1].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][tiles[0].length - 1].controller != -1;
				for (int x = 0; x < tiles.length; x++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[x][tiles[0].length - 1 - x].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[x][tiles[0].length - 1 - x].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}
		}

		/**
		 * Determines the value of any bonuses that are 3-d diagonals that the
		 * tile at the given coordinates is a part of
		 * @param tileCoordinates The coordinates of the tile
		 * @param tiles a reloaded array so that the last index is constant
		 *            throughout the diagonal
		 * @return the value added from any 3-d diagonals this tile is a part of
		 */
		private void checkFor3dDiagonal(Tile[][][] tiles)
		{
			int firstController;
			int numberOfAiControlled;
			boolean allEnemyControlled;

			// Diagonals of the form x=y=z
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] == tileCoordinates[2])
			{
				firstController = tiles[0][0][0].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][0][0].controller != -1;
				for (int xyz = 0; xyz < tiles.length; xyz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xyz][xyz][xyz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[xyz][xyz][xyz].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}

			// Diagonals of the form x=y
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] + tileCoordinates[2] == tiles[0][0].length - 1)
			{
				firstController = tiles[0][0][tiles[0][0].length - 1].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][0][tiles[0][0].length - 1].controller != -1;
				for (int xy = 0; xy < tiles.length; xy++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xy][xy][tiles[0][0].length - 1 - xy].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[xy][xy][tiles[0][0].length - 1 - xy].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}

			// Diagonals of the form x=z
			if (tileCoordinates[0] == tileCoordinates[2]
					&& tileCoordinates[0] + tileCoordinates[1] == tiles[0].length - 1)
			{
				firstController = tiles[0][tiles[0][0].length - 1][0].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][tiles[0][0].length - 1][0].controller != -1;
				for (int xz = 0; xz < tiles.length; xz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xz][tiles[0].length - 1 - xz][xz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[xz][tiles[0].length - 1 - xz][xz].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}

			// Diagonals of the form y=z
			if (tileCoordinates[1] == tileCoordinates[2]
					&& tileCoordinates[0] + tileCoordinates[1] == tiles[0].length - 1)
			{
				firstController = tiles[tiles.length - 1][0][0].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[tiles.length - 1][0][0].controller != -1;
				for (int yz = 0; yz < tiles.length; yz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[tiles[0].length - 1 - yz][yz][yz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[tiles[0].length - 1 - yz][yz][yz].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}
		}

		private void check4dDiagonal()
		{
			int firstController;
			int numberOfAiControlled;
			boolean allEnemyControlled;

			// Diagonals of the form x=y=z=q
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] == tileCoordinates[2]
					&& tileCoordinates[0] == tileCoordinates[3])
			{
				firstController = tiles[0][0][0][0].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][0][0][0].controller != -1;
				for (int xyzq = 0; xyzq < tiles.length; xyzq++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xyzq][xyzq][xyzq][xyzq].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[xyzq][xyzq][xyzq][xyzq].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by
				// this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}

			// Diagonals of the form x=y=z, q=length-1-x
			check4dDiagonal3Same(tiles);

			// Diagonals of the form q=y=z, x=length-1-y
			Tile[][][][] reorganized4dMap = new Tile[tiles.length][tiles.length][tiles.length][tiles.length];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reorganized4dMap[y][z][q][x] = tiles[x][y][z][q];
			check4dDiagonal3Same(reorganized4dMap);

			// Diagonals of the form y=z, x=q, y=length-1-x
			check4dDiagonal2Same(reorganized4dMap);

			// Diagonals of the form x=q=z, y=length-1-x
			reorganized4dMap = new Tile[tiles.length][tiles.length][tiles.length][tiles.length];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reorganized4dMap[x][z][q][y] = tiles[x][y][z][q];
			check4dDiagonal3Same(reorganized4dMap);

			// Diagonals of the form x=z, y=q, y=length-1-x
			check4dDiagonal2Same(reorganized4dMap);

			// Diagonals of the form x=q=y, z=length-1-x
			reorganized4dMap = new Tile[tiles.length][tiles.length][tiles.length][tiles.length];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles.length; y++)
					for (int z = 0; z < tiles.length; z++)
						for (int q = 0; q < tiles.length; q++)
							reorganized4dMap[x][y][q][z] = tiles[x][y][z][q];
			check4dDiagonal3Same(reorganized4dMap);

			// Diagonals of the form x=y, z=q, z=length-1-x
			check4dDiagonal2Same(reorganized4dMap);

		}

		/**
		 * Checks for 4D diagonals where 3 coordinates are the same and 1 is
		 * different, the last index is the different coordinate
		 * @param tiles a reorganized map of the board
		 */
		private void check4dDiagonal3Same(Tile[][][][] tiles)
		{
			int firstController;
			int numberOfAiControlled;
			boolean allEnemyControlled;

			// Diagonals where three coordinates are the same and 1 is different
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] == tileCoordinates[2]
					&& tileCoordinates[3] == tiles.length - 1
							- tileCoordinates[0])
			{
				firstController = tiles[0][0][0][tiles.length - 1].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][0][0][tiles.length - 1].controller != -1;
				for (int xyz = 0; xyz < tiles.length; xyz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xyz][xyz][xyz][tiles.length - 1 - xyz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[xyz][xyz][xyz][tiles.length - 1 - xyz].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;

			}
		}

		/**
		 * Checks for 4D diagonals where we have two pairs of identical
		 * coordinates different, the the coordinates should be ordered so that
		 * the first two and last two are the same
		 * @param tiles a reorganized map of the board
		 */
		private void check4dDiagonal2Same(Tile[][][][] tiles)
		{
			int firstController;
			int numberOfAiControlled;
			boolean allEnemyControlled;

			// Diagonals where three coordinates are the same and 1 is different
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[2] == tileCoordinates[3]
					&& tileCoordinates[3] == tiles.length - 1
							- tileCoordinates[0])
			{
				firstController = tiles[0][0][tiles.length - 1][tiles.length - 1].controller;
				numberOfAiControlled = 0;
				allEnemyControlled = tiles[0][0][tiles.length - 1][tiles.length - 1].controller != -1;
				for (int xy = 0; xy < tiles.length; xy++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xy][xy][tiles.length - 1 - xy][tiles.length - 1
							- xy].controller == playerNumber)
						numberOfAiControlled++;

					// Checks if the entire bonus is controlled by an enemy
					if (allEnemyControlled
							&& tiles[xy][xy][tiles.length - 1 - xy][tiles.length
									- 1 - xy].controller != firstController)
						allEnemyControlled = false;
				}
				// Update the counter of complete enemy bonuses
				if (allEnemyControlled)
					numberOfEnemyBonuses++;
				// Update the tile value based on the number of tiles controlled
				// by this player
				contructiveTileValue += (numberOfAiControlled + 1)
						* tiles.length;
			}
		}
	}

	private double aggressiveness;
	private double aggressivnessPremium;
	private LogicBoard board;
	protected Tile[][][][] tiles;
	private ArrayList<int[]> borderTiles;
	private ArrayList<DefensiveMove> defensiveMoves;
	private ArrayList<OffensiveMove> offensiveMoves;

	/**
	 * Constructs the class
	 * @param playerNumber the Ai's reference number (the unique number used to
	 *            tie players and tiles)
	 * @param board The board that the AI is playing on
	 */
	public AiDumb(int playerNumber, LogicBoard board)
	{
		super(playerNumber);
		this.board = board;

		// Initialize variables
		borderTiles = new ArrayList<int[]>();
		defensiveMoves = new ArrayList<DefensiveMove>();
		offensiveMoves = new ArrayList<OffensiveMove>();

		getAiProperties();

		/*
		 * Determine AI "personality". Aggressiveness describes how the AI
		 * weighs attack vs. defense. The AI will NEVER attack if it is weaker
		 * than the tile it is targeting. The AI will also be more aggressive
		 * when there are more AIs (at least one AI wil get lucky)
		 */
		final double NUMBER_OF_PLAYERS_FACTOR = 0.974635445; // (1.2/1.4)^(1/6)
		aggressiveness = board.getDefensiveBonus()
				* aggressivnessPremium
				* Math.pow(NUMBER_OF_PLAYERS_FACTOR, board.getNoComputers() - 1);
		if (aggressiveness < board.getDefensiveBonus())
			aggressiveness = board.getDefensiveBonus();
	}

	/**
	 * Reads in AI properties from a text file, if the text file cannot be
	 * found, the user will receive a warning, and any necessary values will be
	 * set to defaults
	 */
	private void getAiProperties()
	{
		try
		{
			// Read all relevant data from the file
			Scanner readFile = new Scanner(new File("aiProperties.txt"));
			Random randomNumberGenerator = new Random();
			aggressivnessPremium = readFile.nextDouble()
					* Math.exp(randomNumberGenerator.nextGaussian() / 10);
			readFile.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Generate pop-up warning the user that the game files were
			// tampered with

			// Set all properties to default values
			aggressivnessPremium = 1.2;

			e.printStackTrace();
		}
	}

	/**
	 * Updates the array containing a list of all of this AI's border tiles as
	 * well as the list of possible defensive moves
	 */
	private void findBorderTiles()
	{
		// Reset arrays
		borderTiles.clear();
		defensiveMoves.clear();

		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				for (int z = 0; z < tiles[0][0].length; z++)
					for (int q = 0; q < tiles[0][0][0].length; q++)
						if (tiles[x][y][z][q].isBorderTile
								&& tiles[x][y][z][q].controller == playerNumber)
						{
							int[] coordinates = new int[] { x, y, z, q };
							borderTiles.add(coordinates);

							// Check if tile represents a possible defensive
							// move
							if (tiles[x][y][z][q].bordersEnemy)
							{
								DefensiveMove defensiveMove = new DefensiveMove(
										coordinates);
								defensiveMoves.add(defensiveMove);
							}
						}
	}

	/**
	 * Finds the border tile nearest to the given tile
	 * @param x The x coordinate of the given tile
	 * @param y The y coordinate of the given tile
	 * @param z The z coordinate of the given tile
	 * @param q The q coordinate of the given tile
	 * @return The nearest border tile
	 */
	private int[] findNearestBorder(int x, int y, int z, int q)
	{
		// Start by assuming that the closest border tile is the first one in
		// the array of border tiles
		int indexOfClosestBorderTile = 0;
		int closestDistance = Math.abs(borderTiles.get(0)[0] - x)
				+ Math.abs(borderTiles.get(0)[1] - y)
				+ Math.abs(borderTiles.get(0)[2] - z)
				+ Math.abs(borderTiles.get(0)[3] - q);

		// Go through each border tile
		for (int borderTile = 1; borderTile < borderTiles.size(); borderTile++)
		{
			int distanceOfCurrentTile = Math
					.abs(borderTiles.get(borderTile)[0] - x)
					+ Math.abs(borderTiles.get(borderTile)[1] - y)
					+ Math.abs(borderTiles.get(borderTile)[2] - z)
					+ Math.abs(borderTiles.get(borderTile)[3] - q);

			// Update the information foe the closest border tile
			if (distanceOfCurrentTile < closestDistance)
			{
				closestDistance = distanceOfCurrentTile;
				indexOfClosestBorderTile = borderTile;
			}
		}

		return borderTiles.get(indexOfClosestBorderTile);
	}

	/**
	 * @throws NotYourTurnException If this happens, it is a result of
	 *             programmer error Moves all units that are not on a border to
	 *             the nearest border
	 * @throws InsufficientUnitsException If this happens, it is a result of
	 *             programmer error
	 * @throws IllegalMoveException If this happens, it is a result of
	 *             programmer error
	 * @throws
	 */
	private void moveTowardsborder() throws IllegalMoveException,
			InsufficientUnitsException, NotYourTurnException
	{
		// Search through all tiles finding extra units that are not on borders
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				for (int z = 0; z < tiles[0][0].length; z++)
					for (int q = 0; q < tiles[0][0][0].length; q++)
						if (!tiles[x][y][z][q].isBorderTile
								&& tiles[x][y][z][q].controller == playerNumber)
						{
							int noOfUnits = 0;
							// Find total number of units on the tile
							for (int noOfMoves = 1; noOfMoves < tiles[x][y][z][q].noOfUnits.length; noOfMoves++)
								noOfUnits += tiles[x][y][z][q].noOfUnits[noOfMoves];

							// If there are extra units to move, find the
							// nearest tile that is bordered
							if (noOfUnits > 1)
							{
								// Find where we ultimately want to move to
								int[] nearestborder = findNearestBorder(x, y,
										z, q);
								int[] currentTile = { x, y, z, q };

								// Find which move to make and execute
								if (nearestborder[0] > x)
								{
									int[] targetTile = { x + 1, y, z, q };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else if (nearestborder[0] < x)
								{
									int[] targetTile = { x - 1, y, z, q };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else if (nearestborder[1] > y)
								{
									int[] targetTile = { x, y + 1, z, q };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else if (nearestborder[1] < y)
								{
									int[] targetTile = { x, y - 1, z, q };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else if (nearestborder[2] > z)
								{
									int[] targetTile = { x, y, z + 1, q };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else if (nearestborder[2] < z)
								{
									int[] targetTile = { x, y, z - 1, q };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else if (nearestborder[3] > q)
								{
									int[] targetTile = { x, y, z, q + 1 };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else if (nearestborder[3] < q)
								{
									int[] targetTile = { x, y, z, q - 1 };
									board.moveUnits(currentTile, targetTile,
											noOfUnits - 1, 1);
								}
								else
									System.out
											.println("Debug: no move made towards border. From: "
													+ currentTile
													+ " To: "
													+ nearestborder);
							}

						}
	}

	/**
	 * Determines which tiles that the AI can attack this turn (any tile that is
	 * adjacent to an AI-controlled tile but that is not controlled by the AI)
	 */
	private void determinePossibleTargets()
	{
		// Reset possible targets array
		offensiveMoves.clear();

		// Go through all border tiles recording all non-controlled tiles which
		// border them
		for (int borderTile = 0; borderTile < borderTiles.size(); borderTile++)
		{
			// Check if tiles adjacent to this tile in x-space are controlled by
			// another player
			if (borderTiles.get(borderTile)[0] + 1 < tiles.length // Checks
																	// tile
																	// existence
					&& tiles[borderTiles.get(borderTile)[0] + 1][borderTiles
							.get(borderTile)[1]][borderTiles.get(borderTile)[2]][borderTiles
							.get(borderTile)[3]].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0] + 1,
						borderTiles.get(borderTile)[1],
						borderTiles.get(borderTile)[2],
						borderTiles.get(borderTile)[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile)[0] - 1 >= 0 // Checks tile
														// existence
					&& tiles[borderTiles.get(borderTile)[0] - 1][borderTiles
							.get(borderTile)[1]][borderTiles.get(borderTile)[2]][borderTiles
							.get(borderTile)[3]].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0] - 1,
						borderTiles.get(borderTile)[1],
						borderTiles.get(borderTile)[2],
						borderTiles.get(borderTile)[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}

			// Check if tiles adjacent to this tile in y-space are controlled by
			// another player
			if (borderTiles.get(borderTile)[1] + 1 < tiles[0].length // Checks
																		// tile
																		// existence
					&& tiles[borderTiles.get(borderTile)[0]][borderTiles
							.get(borderTile)[1] + 1][borderTiles
							.get(borderTile)[2]][borderTiles
							.get(borderTile)[3]].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0],
						borderTiles.get(borderTile)[1] + 1,
						borderTiles.get(borderTile)[2],
						borderTiles.get(borderTile)[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile)[1] - 1 >= 0 // Checks tile
														// existence
					&& tiles[borderTiles.get(borderTile)[0]][borderTiles
							.get(borderTile)[1] - 1][borderTiles
							.get(borderTile)[2]][borderTiles
							.get(borderTile)[3]].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0],
						borderTiles.get(borderTile)[1] - 1,
						borderTiles.get(borderTile)[2],
						borderTiles.get(borderTile)[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}

			// Check if tiles adjacent to this tile in z-space are controlled by
			// another player
			if (borderTiles.get(borderTile)[2] + 1 < tiles[0][0].length // Checks
																		// tile
																		// existence
					&& tiles[borderTiles.get(borderTile)[0]][borderTiles
							.get(borderTile)[1]][borderTiles.get(borderTile)[2] + 1][borderTiles
							.get(borderTile)[3]].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0],
						borderTiles.get(borderTile)[1],
						borderTiles.get(borderTile)[2] + 1,
						borderTiles.get(borderTile)[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile)[2] - 1 >= 0 // Checks tile
														// existence
					&& tiles[borderTiles.get(borderTile)[0]][borderTiles
							.get(borderTile)[1]][borderTiles.get(borderTile)[2] - 1][borderTiles
							.get(borderTile)[3]].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0],
						borderTiles.get(borderTile)[1],
						borderTiles.get(borderTile)[2] - 1,
						borderTiles.get(borderTile)[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}

			// Check if tiles adjacent to this tile in q-space are controlled by
			// another player
			if (borderTiles.get(borderTile)[3] + 1 < tiles[0][0][0].length // Checks
																			// tile
																			// existence
					&& tiles[borderTiles.get(borderTile)[0]][borderTiles
							.get(borderTile)[1]][borderTiles.get(borderTile)[2]][borderTiles
							.get(borderTile)[3] + 1].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0],
						borderTiles.get(borderTile)[1],
						borderTiles.get(borderTile)[2],
						borderTiles.get(borderTile)[3] + 1 };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile)[3] - 1 >= 0 // Checks tile
														// existence
					&& tiles[borderTiles.get(borderTile)[0]][borderTiles
							.get(borderTile)[1]][borderTiles.get(borderTile)[2]][borderTiles
							.get(borderTile)[3] - 1].controller != this.playerNumber)
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile)[0],
						borderTiles.get(borderTile)[1],
						borderTiles.get(borderTile)[2],
						borderTiles.get(borderTile)[3] - 1 };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile));
				offensiveMoves.add(newMove);
			}
		}

		// Eliminate any duplicate moves found
		eliminateDuplicateMoves();
	}

	/**
	 * Eliminates offensive moves that have the same target so that each target
	 * has exactly one potential move associated with it
	 */
	private void eliminateDuplicateMoves()
	{
		Collections.sort(offensiveMoves);
		for (int move = 0; move < offensiveMoves.size() - 1; move++)
			while (move < offensiveMoves.size() - 1
					&& offensiveMoves.get(move).compareTo(
							offensiveMoves.get(move + 1)) == 0)
				offensiveMoves.remove(move);
	}

	/**
	 * Determines the relative strength of each possible offensive move
	 */
	private void evaluateOffensiveMoves()
	{
		// Determine which tiles the AI can attack this turn
		determinePossibleTargets();

		// Go through each potential target and evaluate the desirability of the
		// attack
		for (int target = 0; target < offensiveMoves.size(); target++)
		{
			DetermineTileValue findTileVlue = new DetermineTileValue();

			// Determine the value generated by the AI if it takes this tile
			double tileValue = findTileVlue
					.main(offensiveMoves.get(target).target, true);

			/*
			 * Determine the number of units the AI would want to use in taking
			 * the tile (# of units on tile * ai aggressiveness)
			 * 
			 * Ai aggressiveness already takes into account the defensive bonus
			 */
			double numberOfUnitsRequiredOffence = (tiles[offensiveMoves
					.get(target).target[0]][offensiveMoves.get(target).target[1]][offensiveMoves
					.get(target).target[2]][offensiveMoves.get(target).target[3]].noOfUnits[0]
					+ tiles[offensiveMoves.get(target).target[0]][offensiveMoves
					.get(target).target[1]][offensiveMoves.get(target).target[2]][offensiveMoves
					.get(target).target[3]].noOfUnits[1])
					* this.aggressiveness;

			// Update offensive moves array
			offensiveMoves.get(target).desirability = tileValue
					/ numberOfUnitsRequiredOffence;
			offensiveMoves.get(target).unitsRequired = (int) Math
					.ceil(numberOfUnitsRequiredOffence);
		}
	}

	/**
	 * Determines the number of enemy units bordering a tile
	 * @param tileCoodinates The tile that the method investigates
	 */
	private int numberOfNeighbouringEnemyUnits(int[] tileCoodinates)
	{
		int numberOfUnits = 0;

		// Go through all neighboring tiles, check if they are AI-controlled,
		// and add them if they are not
		if (tileCoodinates[0] + 1 < tiles.length
				&& tiles[tileCoodinates[0] + 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].controller != super.playerNumber
				&& tiles[tileCoodinates[0] + 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0] + 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[0]
					+ tiles[tileCoodinates[0] + 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[1];
		if (tileCoodinates[1] + 1 < tiles[0].length
				&& tiles[tileCoodinates[0]][tileCoodinates[1] + 1][tileCoodinates[2]][tileCoodinates[3]].controller != super.playerNumber
				&& tiles[tileCoodinates[0]][tileCoodinates[1] + 1][tileCoodinates[2]][tileCoodinates[3]].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0]][tileCoodinates[1] + 1][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[0]
					+ tiles[tileCoodinates[0]][tileCoodinates[1] + 1][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[1];
		if (tileCoodinates[2] + 1 < tiles[0][0].length
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] + 1][tileCoodinates[3]].controller != super.playerNumber
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] + 1][tileCoodinates[3]].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] + 1][tileCoodinates[3]].noOfUnits[0]
					+ tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] + 1][tileCoodinates[3]].noOfUnits[1];
		if (tileCoodinates[3] + 1 < tiles[0][0][0].length
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] + 1].controller != super.playerNumber
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] + 1].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] + 1].noOfUnits[0]
					+ tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] + 1].noOfUnits[1];
		if (tileCoodinates[0] > 0
				&& tiles[tileCoodinates[0] - 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].controller != super.playerNumber
				&& tiles[tileCoodinates[0] - 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0] - 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[0]
					+ tiles[tileCoodinates[0] - 1][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[1];
		if (tileCoodinates[1] > 0
				&& tiles[tileCoodinates[0]][tileCoodinates[1] - 1][tileCoodinates[2]][tileCoodinates[3]].controller != super.playerNumber
				&& tiles[tileCoodinates[0]][tileCoodinates[1] - 1][tileCoodinates[2]][tileCoodinates[3]].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0]][tileCoodinates[1] - 1][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[0]
					+ tiles[tileCoodinates[0]][tileCoodinates[1] - 1][tileCoodinates[2]][tileCoodinates[3]].noOfUnits[1];
		if (tileCoodinates[2] > 0
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] - 1][tileCoodinates[3]].controller != super.playerNumber
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] - 1][tileCoodinates[3]].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] - 1][tileCoodinates[3]].noOfUnits[0]
					+ tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2] - 1][tileCoodinates[3]].noOfUnits[1];
		if (tileCoodinates[3] > 0
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] - 1].controller != super.playerNumber
				&& tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] - 1].controller != -1)
			numberOfUnits += tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] - 1].noOfUnits[0]
					+ tiles[tileCoodinates[0]][tileCoodinates[1]][tileCoodinates[2]][tileCoodinates[3] - 1].noOfUnits[1];

		return numberOfUnits;
	}

	/**
	 * Determines the relative strength of each possible defensive move
	 * 
	 * Note: Using the determineTileValue method for determining
	 * defendingDesirability may result in a small defensive bias due to the
	 * destructiveValue local variable in determineTileValue
	 */
	private void evaluateDefensiveMoves()
	{
		// Go through each possible defensive move and evaluate the strength of
		// the move
		for (int tile = 0; tile < defensiveMoves.size(); tile++)
		{
			int[] coordinates = defensiveMoves.get(tile).coordinates;

			DetermineTileValue findTileVlue = new DetermineTileValue();

			// Determine the value generated by the tile for the AI
			double tileValue = findTileVlue.main(coordinates, true);

			// Determine the number of units necessary to discourage an enemy
			// attack
			defensiveMoves.get(tile).unitsRequired = (int) Math
					.ceil(numberOfNeighbouringEnemyUnits(coordinates)
							/ board.getDefensiveBonus()
							- board.getTile(coordinates).noOfUnits[1]);

			/*
			 * Remove the move if it involves placing negative or 0 units,
			 * otherwise, evaluate it's desirability (placing negative units is
			 * illegal and placing 0 units is pointless)
			 */
			if (defensiveMoves.get(tile).unitsRequired <= 0)
			{
				defensiveMoves.remove(tile);
			}
			else
				defensiveMoves.get(tile).desirability = tileValue
						/ defensiveMoves.get(tile).unitsRequired;
		}
	}

	/**
	 * Makes the best moves available to the AI
	 * @throws InsufficientUnitsException If this happens, it is a result of
	 *             programmer error
	 * @throws IllegalMoveException If this happens, it is a result of
	 *             programmer error
	 * @throws NotYourTurnException If this happens, it is a result of
	 *             programmer error
	 */
	private void makeBestMoves() throws InsufficientUnitsException,
			IllegalMoveException, NotYourTurnException
	{
		// Checks to make sure that there are moves to be made
		if (offensiveMoves.size() > 0 || defensiveMoves.size() > 0)
		{
			int[][] borderTilesArray = new int[borderTiles.size()][4];
			borderTilesArray = borderTiles.toArray(borderTilesArray);

			boolean[] offensiveMoveMade = new boolean[offensiveMoves.size()];

			// Sort offensive and defensive moves by desirability
			Comparator<DefensiveMove> compareByDesirabilityDefense = new Comparator<DefensiveMove>()
			{
				public int compare(DefensiveMove o1, DefensiveMove o2)
				{
					if (o1.desirability > o2.desirability)
						return -1;
					if (o1.desirability < o2.desirability)
						return 1;
					return 0;
				}
			};
			Collections.sort(defensiveMoves, compareByDesirabilityDefense);

			Comparator<OffensiveMove> compareByDesirabilityOffense = new Comparator<OffensiveMove>()
			{
				public int compare(OffensiveMove o1, OffensiveMove o2)
				{
					if (o1.desirability > o2.desirability)
						return -1;
					if (o1.desirability < o2.desirability)
						return 1;
					return 0;
				}
			};

			Collections.sort(offensiveMoves, compareByDesirabilityOffense);

			/*
			 * System.out.println("Before update"); // debug for (int
			 * offensiveMove = 0; offensiveMove < offensiveMoves.size();
			 * offensiveMove++) System.out .printf(
			 * "%n Planning to attack %d %d %d %d with %d units. Desiribility: %f"
			 * , offensiveMoves.get(offensiveMove).target[0],
			 * offensiveMoves.get(offensiveMove).target[1],
			 * offensiveMoves.get(offensiveMove).target[2],
			 * offensiveMoves.get(offensiveMove).target[3],
			 * offensiveMoves.get(offensiveMove).unitsRequired,
			 * offensiveMoves.get(offensiveMove).desirability);
			 * System.out.println();
			 */

			// Update the number of units that need to be placed for an attack
			// so that units already on a tile are no longer needed
			// Sort offensive moves by source tile and make sure that it only
			// counts each source tile once
			Comparator<OffensiveMove> compareBySource = new Comparator<OffensiveMove>()
			{
				public int compare(OffensiveMove o1, OffensiveMove o2)
				{
					if (o1.source[0] > o2.source[0])
						return 1;
					else if (o1.source[0] < o2.source[0])
						return -1;
					else
					if (o1.source[1] > o2.source[1])
						return 1;
					else if (o1.source[1] < o2.source[1])
						return -1;
					else
					if (o1.source[2] > o2.source[2])
						return 1;
					else if (o1.source[2] < o2.source[2])
						return -1;
					else
					if (o1.source[3] > o2.source[3])
						return 1;
					else if (o1.source[3] < o2.source[3])
						return -1;
					else
						return 0;
				}
			};

			Collections.sort(offensiveMoves, compareBySource);

			if (numberOfBorderingEnemies(offensiveMoves
					.get(0).source) <= 1)
			{
				offensiveMoves.get(0).unitsRequired -= board
						.getTile(offensiveMoves.get(0).source).noOfUnits[1];

				// Ensure that the AI dosn't think that it can leave 0 units
				// on the source tile
				// Note: This conditional should always be true but is left
				// in place in case some other segment of code is changed in
				// the future
				if (board
						.getTile(offensiveMoves.get(0).source).noOfUnits[0] == 0)
					offensiveMoves.get(0).unitsRequired++;

				// Ensure that the AI dosn't try to place negative units
				if (offensiveMoves.get(0).unitsRequired < 0)
					offensiveMoves.get(0).unitsRequired = 0;
			}
			for (int offensiveIndex = 1; offensiveIndex < offensiveMoves.size(); offensiveIndex++)
			{
				if (compareBySource.compare(
						offensiveMoves.get(offensiveIndex),
						offensiveMoves.get(offensiveIndex - 1)) != 0)
				{
					if (numberOfBorderingEnemies(offensiveMoves
							.get(offensiveIndex).source) <= 1)
					{
						offensiveMoves.get(offensiveIndex).unitsRequired -= board
								.getTile(offensiveMoves.get(offensiveIndex).source).noOfUnits[1];

						/*
						 * Ensure that the AI dosn't think that it can leave 0
						 * units on the source tile.
						 * 
						 * Note: This conditional should always be true but is
						 * left in place in case some other segment of code is
						 * changed in the future
						 */
						if (board
								.getTile(offensiveMoves.get(offensiveIndex).source).noOfUnits[0] == 0)
							offensiveMoves.get(offensiveIndex).unitsRequired++;

						// Ensure that the AI dosn't try to place negative units
						if (offensiveMoves.get(offensiveIndex).unitsRequired < 0)
							offensiveMoves.get(offensiveIndex).unitsRequired = 0;
					}
					else
					/*
					 * The tile borders one or more enemy tile, let's see if we
					 * can use of of the units on the tile in an attack and
					 * still have enough units to defend the tile from the
					 * remaining enemies
					 */
					{
						// Start with base number of units
						int numberOfUnitsRequiredOption2 = offensiveMoves
								.get(offensiveIndex).unitsRequired
								- board
										.getTile(offensiveMoves
												.get(offensiveIndex).source).noOfUnits[1];

						// Add how many units we would normally want to defend
						// with
						numberOfUnitsRequiredOption2 += (int) Math
								.ceil(numberOfNeighbouringEnemyUnits(offensiveMoves
										.get(offensiveIndex).source)
										/ board.getDefensiveBonus());

						// Subtract the number of units on the target tile (they
						// won't be there for long)
						numberOfUnitsRequiredOption2 -= board
								.getTile(offensiveMoves.get(offensiveIndex).target).noOfUnits[0]
								+ board.getTile(offensiveMoves
										.get(offensiveIndex).target).noOfUnits[1];

						/*
						 * Ensure that the AI dosn't think that it can leave 0
						 * units on the source tile.
						 * 
						 * Note: This conditional should always be true but is
						 * left in place in case some other segment of code is
						 * changed in the future
						 */
						if (board
								.getTile(offensiveMoves.get(offensiveIndex).source).noOfUnits[0] == 0)
							numberOfUnitsRequiredOption2++;

						// Ensure that the AI dosn't try to place negative units
						if (numberOfUnitsRequiredOption2 < 0)
							numberOfUnitsRequiredOption2 = 0;

						/*
						 * If option 2 is a lower value than the number of units
						 * we thought we needed to attack with originally, use
						 * option 2 (if option 2 is a bigger number, we don't
						 * need to worry: elsewhere in the AI we evaluate which
						 * tiles are in the greatest need of defending)
						 */
						if (numberOfUnitsRequiredOption2 < offensiveMoves
								.get(offensiveIndex).unitsRequired)
							offensiveMoves.get(offensiveIndex).unitsRequired = numberOfUnitsRequiredOption2;
					}
				}
			}

			// Sort all moves in order of desirability again
			Collections.sort(offensiveMoves, compareByDesirabilityOffense);

			System.out.println("After update");
			for (int offensiveMove = 0; offensiveMove < offensiveMoves.size(); offensiveMove++)
				System.out.printf(
						"%n Planning to attack %d %d %d %d with %d units. Desiribility: %f"
						, offensiveMoves.get(offensiveMove).target[0],
						offensiveMoves.get(offensiveMove).target[1],
						offensiveMoves.get(offensiveMove).target[2],
						offensiveMoves.get(offensiveMove).target[3],
						offensiveMoves.get(offensiveMove).unitsRequired,
						offensiveMoves.get(offensiveMove).desirability);
			System.out.println();

			// Go through all possible unit placements and make them in order of
			// their desirability
			int defensiveIndex = 0;
			int offensiveIndex = 0;
			while (defensiveIndex < defensiveMoves.size()
					&& offensiveIndex < offensiveMoves.size()
					&& board.getUnitsRemaining(playerNumber) > 0)
			{
				/*
				 * Find the next best offensive move that the AI has enough
				 * units to make while (offensiveIndex < offensiveMoves.size()
				 * && offensiveMoves.get(offensiveIndex).unitsRequired > board
				 * .getUnitsRemaining(playerNumber)) offensiveIndex++;
				 */

				if (offensiveIndex < offensiveMoves.size())
				{
					/*
					 * Make the best remaining offensive move if it is better
					 * than the best remaining defensive move, and the AI still
					 * has enough units to make the move
					 */

					if (defensiveMoves.get(defensiveIndex).desirability < offensiveMoves
							.get(offensiveIndex).desirability)
					{
						/*
						 * Check if the Ai has enough units to execute this move
						 * this turn. If it dosn't, still place some units, but
						 * on the expectation of attacking on some future turn
						 */
						if (offensiveMoves.get(offensiveIndex).unitsRequired <= board
								.getUnitsRemaining(playerNumber))
						{
							board.placeUnits(
									offensiveMoves.get(offensiveIndex).source,
									offensiveMoves.get(offensiveIndex).unitsRequired);
							offensiveMoveMade[offensiveIndex] = true;
						}
						else
						{
							board.placeUnits(
									offensiveMoves.get(offensiveIndex).source,
									board.getUnitsRemaining(playerNumber));

							// There is no offensiveMoveMade[offensiveIndex] =
							// true; here because we don't want the AI to try to
							// move these units
						}
						offensiveIndex++;
					}

					// Make next best defensive move
					else
					{
						// Make sure that the AI dosn't try to place more units
						// that it has available
						if (defensiveMoves.get(defensiveIndex).unitsRequired > board
								.getUnitsRemaining(playerNumber))
							board.placeUnits(
									defensiveMoves.get(defensiveIndex).coordinates,
									board.getUnitsRemaining(playerNumber));
						else
							board.placeUnits(
									defensiveMoves.get(defensiveIndex).coordinates,
									defensiveMoves.get(defensiveIndex).unitsRequired);
						defensiveIndex++;
					}
				}
			}

			// Use any remaining units for defense
			while (defensiveIndex < defensiveMoves.size()
					&& board.getUnitsRemaining(playerNumber) > 0)
			{
				// Make sure that the AI dosn't try to place more units than
				// it has available
				if (defensiveMoves.get(defensiveIndex).unitsRequired > board
						.getUnitsRemaining(playerNumber))
					board.placeUnits(
							defensiveMoves.get(defensiveIndex).coordinates,
							board.getUnitsRemaining(playerNumber));
				else
					board.placeUnits(
							defensiveMoves.get(defensiveIndex).coordinates,
							defensiveMoves.get(defensiveIndex).unitsRequired);
				defensiveIndex++;
			}

			// Find the next best offensive move that the AI has enough units to
			// make
			while (offensiveIndex < offensiveMoves.size()
					&& offensiveMoves.get(offensiveIndex).unitsRequired > board
							.getUnitsRemaining(playerNumber))
				offensiveIndex++;

			// Use any remaining units for offense
			while (offensiveIndex < offensiveMoves.size())
			{
				board.placeUnits(offensiveMoves.get(offensiveIndex).source,
						offensiveMoves.get(offensiveIndex).unitsRequired);
				offensiveMoveMade[offensiveIndex] = true;
				offensiveIndex++;

				// Find the next best offensive move that the AI has enough
				// units to make
				while (offensiveIndex < offensiveMoves.size()
						&& offensiveMoves.get(offensiveIndex).unitsRequired > board
								.getUnitsRemaining(playerNumber))
					offensiveIndex++;
			}

			/*
			 * If there are still overflow units (all possible offensive moves
			 * have been made; all defensive moves have been made) put remaining
			 * units on the tile with the most desirable move
			 */
			if (defensiveMoves.size() == 0) // Checks if there are any possible
											// defensive moves (avoids
											// ArrayIndexOutOfBoundsException)
				board.placeUnits(offensiveMoves.get(0).source,
						board.getUnitsRemaining(playerNumber));
			else
			{
				if (defensiveMoves.get(0).desirability < offensiveMoves.get(0).desirability)
					// Note: no "offensiveMoveMade[offensiveIndex] = true;" is
					// necessary since we don't necessarily have enough units to
					// make the attack
					board.placeUnits(offensiveMoves.get(0).source,
							board.getUnitsRemaining(playerNumber));
				else
					board.placeUnits(defensiveMoves.get(0).coordinates,
							board.getUnitsRemaining(playerNumber));
			}

			/*
			 * System.out.println("Before units are moved"); for (int
			 * offensiveMove = 0; offensiveMove < offensiveMoves.size();
			 * offensiveMove++) System.out .printf(
			 * "%n Planning to attack %d %d %d %d with %d units. Desiribility: %f Will move: %b"
			 * , offensiveMoves.get(offensiveMove).target[0],
			 * offensiveMoves.get(offensiveMove).target[1],
			 * offensiveMoves.get(offensiveMove).target[2],
			 * offensiveMoves.get(offensiveMove).target[3],
			 * offensiveMoves.get(offensiveMove).unitsRequired,
			 * offensiveMoves.get(offensiveMove).desirability,
			 * offensiveMoveMade[offensiveMove]); System.out.println();
			 */

			/*
			 * Make moves (goes in ascending order of desirability because it
			 * will try to use defensive units in attacks if there are no
			 * bordering enemies other than the tile being attacked, this
			 * condition is more likely after less desirable moves have been
			 * made, and we want the extra units on the most desirable moves)
			 * 
			 * Note: moves that required 0 additional units to be placed are
			 * made in descending order of desirability because 0 additional
			 * units can only be a result of bordering no enemy tiles (only
			 * uncontrolled tiles and own tiles)
			 * 
			 * All offensive moves are checked to ensure that there are enough
			 * units on the tile to make the move
			 */
			for (int move = offensiveMoves.size() - 1; move >= 0; move--)
			{
				int numberOfUnitsAvailable = 0;

				if (board.getTile(offensiveMoves.get(move).source).noOfUnits[0] == 0)
					numberOfUnitsAvailable = board.getTile(offensiveMoves
							.get(move).source).noOfUnits[1] - 1;
				else
					numberOfUnitsAvailable = board.getTile(offensiveMoves
							.get(move).source).noOfUnits[1];

				if (offensiveMoveMade[move]
						&& offensiveMoves.get(move).unitsRequired != 0
						&& numberOfUnitsAvailable >= offensiveMoves
								.get(move).unitsRequired)
				// Use all available units if the target tile is the only tile
				// bordering the source tileF
				{
					int numberOfUnitsToMove = 0;
					if (numberOfBorderingEnemies(offensiveMoves.get(move).source) < 2)
						// Ensure that at least one unit is left on the source
						// tile
						if (board.getTile(offensiveMoves.get(move).source).noOfUnits[0] > 0)
							numberOfUnitsToMove = board.getTile(offensiveMoves
									.get(move).source).noOfUnits[1];
						else
							numberOfUnitsToMove = board.getTile(offensiveMoves
									.get(move).source).noOfUnits[1] - 1;
					else
						numberOfUnitsToMove = offensiveMoves.get(move).unitsRequired;
					board.moveUnits(offensiveMoves.get(move).source,
							offensiveMoves.get(move).target,
							numberOfUnitsToMove, 1);
				}
			}

			// Make moves with 0 additional units required in descending order
			// of desirability
			for (int move = 0; move < offensiveMoves.size(); move++)
			{
				if (offensiveMoveMade[move]
						&& offensiveMoves.get(move).unitsRequired == 0
						&& board.getTile(offensiveMoves.get(move).source).noOfUnits[1] >= offensiveMoves
								.get(move).unitsRequired)
				{
					// Ensure that at least one unit is left on the source tile
					int numberOfUnitsToMove = 0;
					if (board.getTile(offensiveMoves.get(move).source).noOfUnits[0] > 0)
						numberOfUnitsToMove = board.getTile(offensiveMoves
								.get(move).source).noOfUnits[1];
					else
						numberOfUnitsToMove = board.getTile(offensiveMoves
								.get(move).source).noOfUnits[1] - 1;

					// Ensures that the AI still has enough units to make the
					// move
					if (numberOfUnitsToMove >= offensiveMoves.get(move).unitsRequired)
						board.moveUnits(offensiveMoves.get(move).source,
								offensiveMoves.get(move).target,
								numberOfUnitsToMove, 1);
				}
			}
		}
	}

	/**
	 * 
	 * @param tile The tile who's borders we are checking
	 * @return the number of enemies bordering the given tile
	 */
	private int numberOfBorderingEnemies(int[] tile)
	{
		int numberOfBorderingEnemies = 0;

		// Determine the number of bordering tiles
		if (tile[0] + 1 < tiles.length
				&& tiles[tile[0] + 1][tile[1]][tile[2]][tile[3]].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0] + 1][tile[1]][tile[2]][tile[3]].controller != -1)
			numberOfBorderingEnemies++;
		if (tile[1] + 1 < tiles[0].length
				&& tiles[tile[0]][tile[1] + 1][tile[2]][tile[3]].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0]][tile[1] + 1][tile[2]][tile[3]].controller != -1)
			numberOfBorderingEnemies++;
		if (tile[2] + 1 < tiles[0][0].length
				&& tiles[tile[0]][tile[1]][tile[2] + 1][tile[3]].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0]][tile[1]][tile[2] + 1][tile[3]].controller != -1)
			numberOfBorderingEnemies++;
		if (tile[3] + 1 < tiles[0][0][0].length
				&& tiles[tile[0]][tile[1]][tile[2]][tile[3] + 1].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0]][tile[1]][tile[2]][tile[3] + 1].controller != -1)
			numberOfBorderingEnemies++;
		if (tile[0] > 0
				&& tiles[tile[0] - 1][tile[1]][tile[2]][tile[3]].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0] - 1][tile[1]][tile[2]][tile[3]].controller != -1)
			numberOfBorderingEnemies++;
		if (tile[1] > 0
				&& tiles[tile[0]][tile[1] - 1][tile[2]][tile[3]].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0]][tile[1] - 1][tile[2]][tile[3]].controller != -1)
			numberOfBorderingEnemies++;
		if (tile[2] > 0
				&& tiles[tile[0]][tile[1]][tile[2] - 1][tile[3]].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0]][tile[1]][tile[2] - 1][tile[3]].controller != -1)
			numberOfBorderingEnemies++;
		if (tile[3] > 0
				&& tiles[tile[0]][tile[1]][tile[2]][tile[3] - 1].controller != tiles[tile[0]][tile[1]][tile[2]][tile[3]].controller
				&& tiles[tile[0]][tile[1]][tile[2]][tile[3] - 1].controller != -1)
			numberOfBorderingEnemies++;

		return numberOfBorderingEnemies;
	}

	/**
	 * Decides what the AI should do this turn
	 */
	public void makeNextMove()
	{
		// Note: try/catch blocks in this method are necessary to prevent
		// compiler errors, these exceptions should never actually be thrown

		// Get a map of the board
		this.tiles = board.getTiles();

		// Find which tiles are this AI's border tiles
		findBorderTiles();

		// Move any units that are not on the border towards the border
		if (borderTiles.size() > 0)
			try
			{
				moveTowardsborder();
			}
			catch (IllegalMoveException | InsufficientUnitsException
					| NotYourTurnException e)
			{
				e.printStackTrace();
			}

		/*
		 * Determine which tiles can be attacked and evaluate the desirability
		 * of attacking each tile
		 */
		evaluateOffensiveMoves();

		/*
		 * Determine which tiles are border tiles and evaluate the desirability
		 * of improving their defenses
		 */
		evaluateDefensiveMoves();

		// Display all offensive and defensive moves debug
		/*
		 * System.out.println("Before makeBestMoves()"); for (int offensiveMove
		 * = 0; offensiveMove < offensiveMoves.size(); offensiveMove++)
		 * System.out .printf(
		 * "%n Planning to attack %d %d %d %d with %d units. Desiribility: %f",
		 * offensiveMoves.get(offensiveMove).target[0],
		 * offensiveMoves.get(offensiveMove).target[1],
		 * offensiveMoves.get(offensiveMove).target[2],
		 * offensiveMoves.get(offensiveMove).target[3],
		 * offensiveMoves.get(offensiveMove).unitsRequired,
		 * offensiveMoves.get(offensiveMove).desirability);
		 * System.out.println();
		 */

		// Make the best moves as determined above
		try
		{
			makeBestMoves();
		}
		catch (InsufficientUnitsException | IllegalMoveException
				| NotYourTurnException e)
		{
			e.printStackTrace();
		}

		// System.out.printf("\n %d %d", offensiveMoves.size(), //debug
		// defensiveMoves.size());
	}

	/**
	 * 
	 * @return A double representing the degree of confidence that the Ai needs
	 *         to have to make an attack
	 */
	public double getAggressivnessPremium()
	{
		return aggressivnessPremium;
	}

	public void guessEnemyMoves()
	{

	}
}
