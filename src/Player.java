/**
 * Stores all data related to the player of the game
 * @author Logan Grier
 * @version 04/01/2015
 */
public class Player
{
	protected int playerNumber;
	public int noOfTilesControlled, unitProduction, unitsRemaining;

	/**
	 * Creates the object
	 * @param playerNumber An index that matches this player's tile.controller
	 *            id
	 */
	public Player(int playerNumber)
	{
		this.playerNumber = playerNumber;
	}

	/**
	 * Allows the player to place all of their units then move their units
	 */
	public void makeNextMove()
	{
	}
	
	/**
	 * Finds and determines the aggressiveness of this player
	 * @return The aggressiveness of this player
	 */
	public double getAggressivnessPremium()
	{
		return (Double) null;
	}
}
