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
 * @author Logan Grier
 * @version 18/01/2015
 */
public class AiSmart extends Player
{
	private class DetermineTileValue
	{
		private int[] tileCoordinates;
		double contructiveTileValue = 0;
		int numberOfEnemyBonuses = 0;
		int[] almostControlled;

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
			almostControlled = new int[board.getNoOfPlayers()];
			for (int index = 0; index < almostControlled.length; index++)
				almostControlled[index] = 0;

			// Go through all bonuses that pass through this tile, and check if
			// any other player controls the entire bonus

			/*
			 * boolean allEnemyControlled =
			 * tiles[0][tileCoordinates[1]][tileCoordinates
			 * [2]][tileCoordinates[3]].controller != -1;
			 * 
			 * Makes sure that a bonus controlled entirely by uncontrolled does
			 * not count
			 */

			/*
			 * contructiveTileValue += tiles.length / (double)(1 + (tiles.length
			 * - 1 - numberOfAiControlled) * noOfDimensions);
			 * 
			 * Is the general form of the constructive tile value adder
			 * (basically, the thing in the denominator is the number of tiles
			 * we would need to take to take to get the bonus if we controlled
			 * no tiles adjacent to the bonus)
			 * 
			 * This line is also in a conditional to ensure that the computer is
			 * never dividing by 0 (this would only otherwise happen for
			 * defensive moves and in these cases, the value of the defensive
			 * move is equivalent to the value of taking the last tile in a
			 * bonus)
			 */

			// Check the row/column moving through x-space
			Tile[] reloadedTiles1D = new Tile[tiles.length];
			for (int x = 0; x < tiles.length; x++)
				reloadedTiles1D[x] = tiles[x][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]];
			checkFor1dBonus(reloadedTiles1D);

			// Check the rows/column moving through y-space
			reloadedTiles1D = new Tile[tiles.length];
			for (int y = 0; y < tiles.length; y++)
				reloadedTiles1D[y] = tiles[tileCoordinates[0]][y][tileCoordinates[2]][tileCoordinates[3]];
			checkFor1dBonus(reloadedTiles1D);

			// Check the rows/column moving through z-space
			if (tiles[0][0].length > 1)
			{
				reloadedTiles1D = new Tile[tiles.length];
				for (int z = 0; z < tiles.length; z++)
					reloadedTiles1D[z] = tiles[tileCoordinates[0]][tileCoordinates[1]][z][tileCoordinates[3]];
				checkFor1dBonus(reloadedTiles1D);
			}

			// Check the rows/column moving through q-space
			if (tiles[0][0][0].length > 1)
			{
				reloadedTiles1D = new Tile[tiles.length];
				for (int q = 0; q < tiles.length; q++)
					reloadedTiles1D[q] = tiles[tileCoordinates[0]][tileCoordinates[1]][tileCoordinates[2]][q];
				checkFor1dBonus(reloadedTiles1D);
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
			double destructiveValue = 0;
			if (board.getTile(tileCoordinate).controller >= 0)
				destructiveValue = numberOfEnemyBonuses
						* tiles.length
						* targetAttackPreference[board.getTile(tileCoordinate).controller];

			// Preemptively, what is the the value of preventing this tile from
			// falling into enemy hands
			double denialOfAccessValue = deterimineDenialOfAccessValue();

			// Take into account the loss in unit production by taking away just
			// the tile (1 unit/turn)
			if (board.getTile(tileCoordinate).controller != -1)
				destructiveValue++;

			// Only count the constructive value if we plan on keeping this tile
			if (keepTile)
				return contructiveTileValue + destructiveValue
						+ denialOfAccessValue;
			else
				return destructiveValue;
		}

		/**
		 * 
		 * @return The value generated by preventing other players from
		 *         completing their bonuses
		 */
		private double deterimineDenialOfAccessValue()
		{
			// Find which player has the most gain from getting the target tile
			// (what is the highest value in almostControlled?)
			int playerMostDisruptedBonuses = 0;
			for (int index = 1; index < almostControlled.length; index++)
			{
				if (almostControlled[index] > almostControlled[playerMostDisruptedBonuses])
					playerMostDisruptedBonuses = index;
			}

			return almostControlled[playerMostDisruptedBonuses] * tiles.length;
		}

		private void checkFor1dBonus(Tile[] tiles)
		{
			// Check the row/column moving through x-space
			/*
			 * Determine who the primary enemy controller of this space is
			 * (ultimately, we only want to check if somebody controls 1 less
			 * than the full bonus)
			 */
			int enemyController;
			int firstController = tiles[0].controller;
			int secondController = tiles[1].controller;
			if (firstController == -1 && secondController == -1)
			{
				enemyController = -1; // Signal that denial of access isn't
										// relevant
			}
			else
			{
				if (firstController == -1)
					enemyController = secondController;
				else
					enemyController = firstController;
			}

			int numberOfAiControlled = 0;
			int numberEnemyControlled = 0;
			for (int x = 0; x < tiles.length; x++)
			{
				// Counts what portion of the bonus is controlled by this player
				if (tiles[x].controller == playerNumber)
					numberOfAiControlled++;

				// Checks what portion of the bonus is controlled by a single
				// enemy
				if (enemyController != -1
						&& tiles[x].controller == enemyController)
					numberEnemyControlled++;
			}
			// Update the counter of complete enemy bonuses
			if (numberEnemyControlled == tiles.length)
				numberOfEnemyBonuses++;
			else if (numberEnemyControlled == tiles.length - 1)
				almostControlled[enemyController]++;

			// Update the tile value based on the number of tiles controlled by
			// this player
			if (numberOfAiControlled == tiles.length)
				contructiveTileValue += tiles.length;
			else
			{
				double valueAdded = tiles.length
						/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 2);
				valueAdded *= valueAdded;

				contructiveTileValue += valueAdded;
			}
		}

		private void checkFor2dDiagonal(int tileCoordinatesX,
				int tileCoordiantesY, Tile[][] tiles)
		{
			int firstController, secondController, enemyController, numberOfAiControlled;

			int numberEnemyControlled = 0;

			// Diagonals of the form y=x
			if (tileCoordinatesX == tileCoordiantesY)
			{
				firstController = tiles[0][0].controller;
				secondController = tiles[1][1].controller; 
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}

				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int xy = 0; xy < tiles.length; xy++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xy][xy].controller == playerNumber)
						numberOfAiControlled++;
					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[xy][xy].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 2);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
			}

			// Diagonals of the form y=-x
			if (tileCoordinatesX + tileCoordiantesY == tiles.length - 1)
			{
				firstController = tiles[0][tiles[0].length - 1].controller;
				secondController = tiles[1][tiles[0].length - 2].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int x = 0; x < tiles.length; x++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[x][tiles[0].length - 1 - x].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[x][tiles[0].length - 1 - x].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 2);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
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
			int firstController, secondController, enemyController;
			int numberOfAiControlled;
			int numberEnemyControlled = 0;

			// Diagonals of the form x=y=z
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] == tileCoordinates[2])
			{
				firstController = tiles[0][0][0].controller;
				secondController = tiles[1][1][1].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int xyz = 0; xyz < tiles.length; xyz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xyz][xyz][xyz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[xyz][xyz][xyz].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 3);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
			}

			// Diagonals of the form x=y
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] + tileCoordinates[2] == tiles[0][0].length - 1)
			{
				firstController = tiles[0][0][tiles[0][0].length - 1].controller;
				secondController = tiles[1][1][tiles[0][0].length - 2].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int xy = 0; xy < tiles.length; xy++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xy][xy][tiles[0][0].length - 1 - xy].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[xy][xy][tiles[0][0].length - 1 - xy].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 3);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
			}

			// Diagonals of the form x=z
			if (tileCoordinates[0] == tileCoordinates[2]
					&& tileCoordinates[0] + tileCoordinates[1] == tiles[0].length - 1)
			{
				firstController = tiles[0][tiles[0][0].length - 1][0].controller;
				secondController = tiles[1][tiles[0][0].length - 2][1].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int xz = 0; xz < tiles.length; xz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xz][tiles[0].length - 1 - xz][xz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[xz][tiles[0].length - 1 - xz][xz].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 3);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
			}

			// Diagonals of the form y=z
			if (tileCoordinates[1] == tileCoordinates[2]
					&& tileCoordinates[0] + tileCoordinates[1] == tiles[0].length - 1)
			{
				firstController = tiles[tiles.length - 1][0][0].controller;
				secondController = tiles[tiles.length - 2][1][1].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int yz = 0; yz < tiles.length; yz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[tiles[0].length - 1 - yz][yz][yz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[tiles[0].length - 1 - yz][yz][yz].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 3);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
			}
		}

		private void check4dDiagonal()
		{
			int firstController, secondController, enemyController;
			int numberOfAiControlled;
			int numberEnemyControlled = 0;

			// Diagonals of the form x=y=z=q
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] == tileCoordinates[2]
					&& tileCoordinates[0] == tileCoordinates[3])
			{
				firstController = tiles[0][0][0][0].controller;
				secondController = tiles[1][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int xyzq = 0; xyzq < tiles.length; xyzq++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xyzq][xyzq][xyzq][xyzq].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[xyzq][xyzq][xyzq][xyzq].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 4);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
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
			int firstController, secondController, enemyController;
			int numberOfAiControlled;
			int numberEnemyControlled = 0;

			// Diagonals where three coordinates are the same and 1 is different
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[0] == tileCoordinates[2]
					&& tileCoordinates[3] == tiles.length - 1
							- tileCoordinates[0])
			{
				firstController = tiles[0][0][0][tiles.length - 1].controller;
				secondController = tiles[1][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				for (int xyz = 0; xyz < tiles.length; xyz++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xyz][xyz][xyz][tiles.length - 1 - xyz].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[xyz][xyz][xyz][tiles.length - 1 - xyz].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 4);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
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
			int firstController, secondController, enemyController;
			int numberOfAiControlled, numberEnemyControlled;

			// Diagonals where three coordinates are the same and 1 is different
			if (tileCoordinates[0] == tileCoordinates[1]
					&& tileCoordinates[2] == tileCoordinates[3]
					&& tileCoordinates[3] == tiles.length - 1
							- tileCoordinates[0])
			{
				firstController = tiles[0][0][tiles.length - 1][tiles.length - 1].controller;
				secondController = tiles[1][tileCoordinates[1]][tileCoordinates[2]][tileCoordinates[3]].controller;
				if (firstController == -1 && secondController == -1)
				{
					enemyController = -1; // Signal that denial of access isn't
											// relevant
				}
				else
				{
					if (firstController == -1)
						enemyController = secondController;
					else
						enemyController = firstController;
				}
				numberOfAiControlled = 0;
				numberEnemyControlled = 0;
				numberEnemyControlled = 0;
				for (int xy = 0; xy < tiles.length; xy++)
				{
					// Counts what portion of the bonus is controlled by this
					// player
					if (tiles[xy][xy][tiles.length - 1 - xy][tiles.length - 1
							- xy].controller == playerNumber)
						numberOfAiControlled++;

					// Checks what portion of the bonus is controlled by a
					// single enemy
					if (enemyController != -1
							&& tiles[xy][xy][tiles.length - 1 - xy][tiles.length
									- 1
									- xy].controller == enemyController)
						numberEnemyControlled++;
				}
				// Update the counter of complete enemy bonuses
				if (numberEnemyControlled == tiles.length)
					numberOfEnemyBonuses++;
				else if (numberEnemyControlled == tiles.length - 1)
					almostControlled[enemyController]++;

				// Update the tile value based on the number of tiles controlled
				// by this player
				if (numberOfAiControlled == tiles.length)
					contructiveTileValue += tiles.length;
				else
				{
					double valueAdded = tiles.length
							/ (double) (1 + (tiles.length - 1 - numberOfAiControlled) * 4);
					valueAdded *= valueAdded;

					contructiveTileValue += valueAdded;
				}
			}
		}
	}

	private double aggressiveness, baseAggressiveness;
	private double aggressivnessPremium;
	private LogicBoard board;
	protected Tile[][][][] tiles;
	private ArrayList<BorderTile> borderTiles;
	private ArrayList<DefensiveMove> defensiveMoves;
	private ArrayList<OffensiveMove> offensiveMoves;
	private double[] targetAttackPreference;
	int coalitionTarget;

	/**
	 * Constructs the class
	 * @param playerNumber the Ai's reference number (the unique number used to
	 *            tie players and tiles)
	 * @param board The board that the AI is playing on
	 */
	public AiSmart(int playerNumber, LogicBoard board)
	{
		super(playerNumber);
		this.board = board;

		// Initialize variables
		borderTiles = new ArrayList<BorderTile>();
		defensiveMoves = new ArrayList<DefensiveMove>();
		offensiveMoves = new ArrayList<OffensiveMove>();

		targetAttackPreference = new double[board.getNoOfPlayers()];

		getAiProperties();

		/*
		 * Determine AI "personality". Aggressiveness describes how the AI
		 * weighs attack vs. defense. The AI will NEVER attack if it is weaker
		 * than the tile it is targeting. The AI will also be more aggressive
		 * when there are more AIs (at least one AI will get lucky)
		 */
		final double NUMBER_OF_PLAYERS_FACTOR = 0.974635445; // (1.2/1.4)^(1/6)
		baseAggressiveness = board.getDefensiveBonus()
				* aggressivnessPremium
				* Math.pow(NUMBER_OF_PLAYERS_FACTOR, board.getNoComputers() - 1);
		if (baseAggressiveness < board.getDefensiveBonus())
			baseAggressiveness = board.getDefensiveBonus();

		aggressiveness = baseAggressiveness;
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
			aggressivnessPremium = 1.4;

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
							BorderTile tile = new BorderTile(coordinates);
							borderTiles.add(tile);

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
		int closestDistance = Math.abs(borderTiles.get(0).getCoordinates()[0]
				- x)
				+ Math.abs(borderTiles.get(0).getCoordinates()[1] - y)
				+ Math.abs(borderTiles.get(0).getCoordinates()[2] - z)
				+ Math.abs(borderTiles.get(0).getCoordinates()[3] - q);

		// Go through each border tile
		for (int borderTile = 1; borderTile < borderTiles.size(); borderTile++)
		{
			int distanceOfCurrentTile = Math
					.abs(borderTiles.get(borderTile).getCoordinates()[0] - x)
					+ Math.abs(borderTiles.get(borderTile).getCoordinates()[1]
							- y)
					+ Math.abs(borderTiles.get(borderTile).getCoordinates()[2]
							- z)
					+ Math.abs(borderTiles.get(borderTile).getCoordinates()[3]
							- q);

			// Update the information foe the closest border tile
			if (distanceOfCurrentTile < closestDistance)
			{
				closestDistance = distanceOfCurrentTile;
				indexOfClosestBorderTile = borderTile;
			}
		}

		return borderTiles.get(indexOfClosestBorderTile).getCoordinates();
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
	 * 
	 * Makes sure that if the AI is targeting only one player, that only they
	 * and non-controlled will be attacked
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
			if (borderTiles.get(borderTile).getCoordinates()[0] + 1 < tiles.length // Checks
					// tile
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0] + 1][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0] + 1][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller == -1
							|| tiles[borderTiles
									.get(borderTile).getCoordinates()[0] + 1][borderTiles
									.get(borderTile).getCoordinates()[1]][borderTiles
									.get(borderTile).getCoordinates()[2]][borderTiles
									.get(borderTile).getCoordinates()[3]].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0] + 1,
						borderTiles.get(borderTile).getCoordinates()[1],
						borderTiles.get(borderTile).getCoordinates()[2],
						borderTiles.get(borderTile).getCoordinates()[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile).getCoordinates()[0] - 1 >= 0 // Checks
																			// tile
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0] - 1][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0] - 1][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller == -1
							|| tiles[borderTiles.get(borderTile)
									.getCoordinates()[0] - 1][borderTiles
									.get(borderTile).getCoordinates()[1]][borderTiles
									.get(borderTile).getCoordinates()[2]][borderTiles
									.get(borderTile).getCoordinates()[3]].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0] - 1,
						borderTiles.get(borderTile).getCoordinates()[1],
						borderTiles.get(borderTile).getCoordinates()[2],
						borderTiles.get(borderTile).getCoordinates()[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
				offensiveMoves.add(newMove);
			}

			// Check if tiles adjacent to this tile in y-space are controlled by
			// another player
			if (borderTiles.get(borderTile).getCoordinates()[1] + 1 < tiles[0].length // Checks
					// tile
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1] + 1][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1] + 1][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller == -1
							|| tiles[borderTiles.get(borderTile)
									.getCoordinates()[0]][borderTiles
									.get(borderTile).getCoordinates()[1] + 1][borderTiles
									.get(borderTile).getCoordinates()[2]][borderTiles
									.get(borderTile).getCoordinates()[3]].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0],
						borderTiles.get(borderTile).getCoordinates()[1] + 1,
						borderTiles.get(borderTile).getCoordinates()[2],
						borderTiles.get(borderTile).getCoordinates()[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile).getCoordinates()[1] - 1 >= 0 // Checks
																			// tile
																			//
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1] - 1][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1] - 1][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3]].controller == -1
							|| tiles[borderTiles.get(borderTile)
									.getCoordinates()[0]][borderTiles
									.get(borderTile).getCoordinates()[1] - 1][borderTiles
									.get(borderTile).getCoordinates()[2]][borderTiles
									.get(borderTile).getCoordinates()[3]].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0],
						borderTiles.get(borderTile).getCoordinates()[1] - 1,
						borderTiles.get(borderTile).getCoordinates()[2],
						borderTiles.get(borderTile).getCoordinates()[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
				offensiveMoves.add(newMove);
			}

			// Check if tiles adjacent to this tile in z-space are controlled by
			// another player
			if (borderTiles.get(borderTile).getCoordinates()[2] + 1 < tiles[0][0].length // Checks
					// tile
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2] + 1][borderTiles
							.get(borderTile).getCoordinates()[3]].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2] + 1][borderTiles
							.get(borderTile).getCoordinates()[3]].controller == -1
							|| tiles[borderTiles.get(borderTile)
									.getCoordinates()[0]][borderTiles
									.get(borderTile).getCoordinates()[1]][borderTiles
									.get(borderTile).getCoordinates()[2] + 1][borderTiles
									.get(borderTile).getCoordinates()[3]].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0],
						borderTiles.get(borderTile).getCoordinates()[1],
						borderTiles.get(borderTile).getCoordinates()[2] + 1,
						borderTiles.get(borderTile).getCoordinates()[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile).getCoordinates()[2] - 1 >= 0 // Checks
																			// tile
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2] - 1][borderTiles
							.get(borderTile).getCoordinates()[3]].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2] - 1][borderTiles
							.get(borderTile).getCoordinates()[3]].controller == -1
							|| tiles[borderTiles.get(borderTile)
									.getCoordinates()[0]][borderTiles
									.get(borderTile).getCoordinates()[1]][borderTiles
									.get(borderTile).getCoordinates()[2] - 1][borderTiles
									.get(borderTile).getCoordinates()[3]].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0],
						borderTiles.get(borderTile).getCoordinates()[1],
						borderTiles.get(borderTile).getCoordinates()[2] - 1,
						borderTiles.get(borderTile).getCoordinates()[3] };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
				offensiveMoves.add(newMove);
			}

			// Check if tiles adjacent to this tile in q-space are controlled by
			// another player
			if (borderTiles.get(borderTile).getCoordinates()[3] + 1 < tiles[0][0][0].length // Checks
					// tile
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3] + 1].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3] + 1].controller == -1
							|| tiles[borderTiles.get(borderTile)
									.getCoordinates()[0]][borderTiles
									.get(borderTile).getCoordinates()[1]][borderTiles
									.get(borderTile).getCoordinates()[2]][borderTiles
									.get(borderTile).getCoordinates()[3] + 1].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0],
						borderTiles.get(borderTile).getCoordinates()[1],
						borderTiles.get(borderTile).getCoordinates()[2],
						borderTiles.get(borderTile).getCoordinates()[3] + 1 };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
				offensiveMoves.add(newMove);
			}
			if (borderTiles.get(borderTile).getCoordinates()[3] - 1 >= 0 // Checks
																			// tile
					// existence
					&& tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3] - 1].controller != this.playerNumber
					&& (tiles[borderTiles.get(borderTile).getCoordinates()[0]][borderTiles
							.get(borderTile).getCoordinates()[1]][borderTiles
							.get(borderTile).getCoordinates()[2]][borderTiles
							.get(borderTile).getCoordinates()[3] - 1].controller == -1
							|| tiles[borderTiles.get(borderTile)
									.getCoordinates()[0]][borderTiles
									.get(borderTile).getCoordinates()[1]][borderTiles
									.get(borderTile).getCoordinates()[2]][borderTiles
									.get(borderTile).getCoordinates()[3] - 1].controller == coalitionTarget || coalitionTarget == -1))
			{
				// Add tile to list of potential targets
				int[] porentialTarget = {
						borderTiles.get(borderTile).getCoordinates()[0],
						borderTiles.get(borderTile).getCoordinates()[1],
						borderTiles.get(borderTile).getCoordinates()[2],
						borderTiles.get(borderTile).getCoordinates()[3] - 1 };
				OffensiveMove newMove = new OffensiveMove(porentialTarget,
						borderTiles.get(borderTile).getCoordinates());
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

			// Keep and NoKeep refers to whether or not the Ai will move...

			// Determine the value generated by the AI if it takes this tile
			double tileValueKeep = findTileVlue
					.main(offensiveMoves.get(target).target, true);

			double tileValueNoKeep = findTileVlue
					.main(offensiveMoves.get(target).target, false);

			/*
			 * Determine the number of units the AI would want to use in taking
			 * the tile (# of units on tile * ai aggressiveness)
			 * 
			 * Ai aggressiveness already takes into account the defensive bonus
			 */
			double numberOfUnitsRequiredOffenceNoKeep = (tiles[offensiveMoves
					.get(target).target[0]][offensiveMoves.get(target).target[1]][offensiveMoves
					.get(target).target[2]][offensiveMoves.get(target).target[3]].noOfUnits[0]
					+ tiles[offensiveMoves.get(target).target[0]][offensiveMoves
					.get(target).target[1]][offensiveMoves.get(target).target[2]][offensiveMoves
					.get(target).target[3]].noOfUnits[1])
					* this.aggressiveness;
			double numberOfUnitsRequiredOffenceKeep = numberOfUnitsRequiredOffenceNoKeep
					+ numberOfNeighbouringEnemyUnits(offensiveMoves.get(target).target)
					/ board.getDefensiveBonus();

			double attackDesirablityKeep = tileValueKeep
					/ numberOfUnitsRequiredOffenceKeep;
			double attackDesirablityNoKeep = tileValueNoKeep
					/ numberOfUnitsRequiredOffenceNoKeep;

			// Take the more desirable between attacking to keep and attacking
			// to disrupt (the two options are mutually exclusive)
			if (attackDesirablityKeep > attackDesirablityNoKeep)
			{
				offensiveMoves.get(target).desirability = attackDesirablityKeep;
				offensiveMoves.get(target).unitsRequired = (int) Math
						.ceil(numberOfUnitsRequiredOffenceKeep);
			}
			else
			{
				offensiveMoves.get(target).desirability = attackDesirablityNoKeep;
				offensiveMoves.get(target).unitsRequired = (int) Math
						.ceil(numberOfUnitsRequiredOffenceNoKeep);
			}
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
				 * Make the best remaining offensive move if it is better than
				 * the best remaining defensive move, and the AI still has
				 * enough units to make the move
				 */

				if (defensiveMoves.get(defensiveIndex).desirability < offensiveMoves
						.get(offensiveIndex).desirability)
				{
					/*
					 * Check if the Ai has enough units to execute this move
					 * this turn. If it dosn't, still place some units, but on
					 * the expectation of attacking on some future turn
					 */
					if (offensiveMoves.get(offensiveIndex).unitsRequired <= board
							.getUnitsRemaining(playerNumber))
					{
						this.placeUnits(
								offensiveMoves.get(offensiveIndex).source,
								offensiveMoves.get(offensiveIndex).unitsRequired);
						offensiveMoveMade[offensiveIndex] = true;
					}
					else
					{
						this.placeUnits(
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
					int[] moveCoordinates = defensiveMoves.get(defensiveIndex).coordinates;
					// Make sure that the AI dosn't try to place more units
					// that it has available
					if (defensiveMoves.get(defensiveIndex).unitsRequired > board
							.getUnitsRemaining(playerNumber))
					{
						this.placeUnits(moveCoordinates,
								board.getUnitsRemaining(playerNumber));

						/*
						 * Since we don't have enough units to properly defend
						 * this tile, let's see if we can move units from any
						 * neighboring tiles
						 */
						int[] neighbouringTileCoordinates;
						if (moveCoordinates[0] < board.getTiles().length - 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0] + 1, moveCoordinates[1],
									moveCoordinates[2], moveCoordinates[3] };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
						if (moveCoordinates[1] < board.getTiles()[0].length - 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0], moveCoordinates[1] + 1,
									moveCoordinates[2], moveCoordinates[3] };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
						if (moveCoordinates[2] < board.getTiles()[0][0].length - 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0], moveCoordinates[1],
									moveCoordinates[2] + 1, moveCoordinates[3] };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
						if (moveCoordinates[3] < board.getTiles()[0][0][0].length - 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0], moveCoordinates[1],
									moveCoordinates[2], moveCoordinates[3] + 1 };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
						if (moveCoordinates[0] >= 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0] - 1, moveCoordinates[1],
									moveCoordinates[2], moveCoordinates[3] };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
						if (moveCoordinates[1] >= 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0], moveCoordinates[1] - 1,
									moveCoordinates[2], moveCoordinates[3] };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
						if (moveCoordinates[2] >= 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0], moveCoordinates[1],
									moveCoordinates[2] - 1, moveCoordinates[3] };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
						if (moveCoordinates[3] >= 1)
						{
							neighbouringTileCoordinates = new int[] {
									moveCoordinates[0], moveCoordinates[1],
									moveCoordinates[2], moveCoordinates[3] - 1 };
							rallyNeighbouringUntis(neighbouringTileCoordinates,
									moveCoordinates);
						}
					}
					else
					{
						this.placeUnits(
								defensiveMoves.get(defensiveIndex).coordinates,
								defensiveMoves.get(defensiveIndex).unitsRequired);
					}
					defensiveIndex++;
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
					this.placeUnits(
							defensiveMoves.get(defensiveIndex).coordinates,
							board.getUnitsRemaining(playerNumber));
				else
					this.placeUnits(
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
				this.placeUnits(offensiveMoves.get(offensiveIndex).source,
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
				this.placeUnits(offensiveMoves.get(0).source,
						board.getUnitsRemaining(playerNumber));
			else
			{
				if (defensiveMoves.get(0).desirability < offensiveMoves.get(0).desirability)
					// Note: no "offensiveMoveMade[offensiveIndex] = true;" is
					// necessary since we don't necessarily have enough units to
					// make the attack
					this.placeUnits(offensiveMoves.get(0).source,
							board.getUnitsRemaining(playerNumber));
				else
					this.placeUnits(defensiveMoves.get(0).coordinates,
							board.getUnitsRemaining(playerNumber));
			}

			/*
			 * System.out.println("Before units are moved"); for (int
			 * offensiveMove = 0; offensiveMove < offensiveMoves.size();
			 * offensiveMove++) System.out .printf(
			 * "%n Planning to attack %d %d %d %d with %d units. Desirability: %f Will move: %b"
			 * , offensiveMoves.get(offensiveMove).target[0],
			 * offensiveMoves.get(offensiveMove).target[1],
			 * offensiveMoves.get(offensiveMove).target[2],
			 * offensiveMoves.get(offensiveMove).target[3],
			 * offensiveMoves.get(offensiveMove).unitsRequired,
			 * offensiveMoves.get(offensiveMove).desirability,
			 * offensiveMoveMade[offensiveMove]); System.out.println();
			 */

			/*
			 * Consider rallying neighboring units for defense
			 * 
			 * Conditions:
			 * 
			 * -The neighbor has more units than it needs to defend itself from
			 * enemies OR the neighbor has a lower defensive priority
			 * 
			 * -There are not enough units on the defensive tile to adequately
			 * defend it
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
				// bordering the source tile
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
	 * Checks if we can move units between two tiles without disrupting other
	 * operations
	 * @param neighbouringTileCoordinates The tiles we are trying to move units
	 *            from
	 * @param defendingTileCoordinates The tiles we are trying to move units to
	 * @throws IllegalMoveException Only thrown if there is an error in this
	 *             code
	 * @throws InsufficientUnitsException Only thrown if there is an error in
	 *             this code
	 * @throws NotYourTurnException Only thrown if there is an error in this
	 *             code
	 */
	private void rallyNeighbouringUntis(int[] neighbouringTileCoordinates,
			int[] defendingTileCoordinates) throws IllegalMoveException,
			InsufficientUnitsException, NotYourTurnException
	{
		// Check if the neighboring tile is controlled by this AI
		if (board.getTile(neighbouringTileCoordinates).controller == this.playerNumber)
		{
			// Find the border tile information for this tile
			int currentTile = 0;
			int[] borderTileCoordinates;
			do
			{
				borderTileCoordinates = borderTiles.get(currentTile)
						.getCoordinates();
				currentTile++;
			}
			while (defendingTileCoordinates[0] != borderTileCoordinates[0]
					|| defendingTileCoordinates[1] != borderTileCoordinates[1]
					|| defendingTileCoordinates[2] != borderTileCoordinates[2]
					|| defendingTileCoordinates[3] != borderTileCoordinates[3]);

			// Determine if there are units that can be spared for the defense
			int noOfUnitsAvailable = board.getTile(neighbouringTileCoordinates).noOfUnits[1]
					- 1
					- borderTiles.get(currentTile - 1).unitsRequiredForMoves;
			if (noOfUnitsAvailable >= 1)
			{
				board.moveUnits(neighbouringTileCoordinates,
						defendingTileCoordinates, noOfUnitsAvailable, 1);
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

		// Reset global modifiers
		coalitionTarget = -1;
		aggressiveness = baseAggressiveness;

		/*
		 * Adapt risk-level to the balance of power in the game (become more
		 * aggressive if one player becomes more dominant)
		 * 
		 * This factor becomes active after one player controls 40% of the
		 * board)
		 * 
		 * If one player controls more than 45% of production, only attack them
		 * and non-controlled
		 */
		if (playerNumber != board.getStrongestPlayer())
		{
			double percentProductionOfStrongest = board.getUnitProduction(board
					.getStrongestPlayer())
					/ (double) board.getTotalProduction();
			if (percentProductionOfStrongest > 0.4)
				aggressiveness = baseAggressiveness
						/ (percentProductionOfStrongest / 0.4);

			if (percentProductionOfStrongest > 0.4)
				coalitionTarget = board.getStrongestPlayer();
		}

		/*
		 * Also adapts risk-level to balance of power but this time increases
		 * aggression if this player is falling behind
		 * 
		 * The root on this factor reduces it's impact
		 */
		double ourPercentProduction = board.getUnitProduction(playerNumber)
				/ (double) board.getTotalProduction();
		double targetPercentProduction = 1 / (double) board
				.getNumPlayersRemaining();
		if (ourPercentProduction < targetPercentProduction)
			aggressiveness = baseAggressiveness
					* Math.pow(ourPercentProduction / targetPercentProduction,
							1 / 10);

		/*
		 * Try to maintain the balance of power in the game (prefer attacking
		 * the stronger players)
		 * 
		 * The exponent makes the impact of this multiplier more considerable,
		 * particularly against very strong players
		 */
		for (int player = 0; player < board.getNoOfPlayers(); player++)
			targetAttackPreference[player] = Math.pow(
					board.getUnitProduction(player)
							/ (double) board.getTotalProduction() + 1, 5);

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
		 * "%n Planning to attack %d %d %d %d with %d units. Desirability: %f",
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

	/**
	 * Places units on the given tile and updates the number of units required
	 * on that border tile
	 * @param tile Which tile we are placing units on
	 * @param noOfUnits The number of units we are placing on this tile
	 * @throws InsufficientUnitsException When a player tries to place more
	 *             units on a tile than are available
	 * @throws NotYourTurnException If the player who's turn it is right now is
	 *             not the controller of the given tile
	 * @throws IllegalMoveException If the player tries to place negative units
	 */
	private void placeUnits(int[] tile, int noOfUnits)
			throws InsufficientUnitsException, NotYourTurnException,
			IllegalMoveException
	{
		// Place units
		board.placeUnits(tile, noOfUnits);

		// Find and update the information for the corresponding border tile
		int currentTile = 0;
		int[] borderTileCoordinates;
		do
		{
			borderTileCoordinates = borderTiles.get(currentTile)
					.getCoordinates();
			currentTile++;
		}
		while (tile[0] != borderTileCoordinates[0]
				|| tile[1] != borderTileCoordinates[1]
				|| tile[2] != borderTileCoordinates[2]
				|| tile[3] != borderTileCoordinates[3]);
		borderTiles.get(currentTile - 1).unitsRequiredForMoves += noOfUnits;
	}
}
