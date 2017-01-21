/**
 * An object that keeps track of tile-specific data
 * @author Logan Grier
 * @date 30/11/2014
 */
public class Tile
{
	public int controller = -1; // -1 is reserved for uncontrolled, controllers
								// then ordered humans first, computers second
	public int[] noOfUnits;
	public boolean isBorderTile = false;
	public boolean bordersEnemy = false;

	public Tile(int startingUnits, int movesPerTurn, int tileX, int tileY,
			int tileZ, int tileQ, int boardX, int boardY, int boardZ, int boardQ)
	{
		noOfUnits = new int[movesPerTurn + 1];
		noOfUnits[1] = startingUnits;
	}
}
