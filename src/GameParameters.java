/**
 * Stores game parameters
 * @author Logan Grier
 * @version 30/11/2014
 */
public class GameParameters
{
	private double defensiveBonus; // Is 1-based (a bonus of 1.1 is a 10%
											// bonus; a bonus of 0.1 is a 90%
											// penalty)
	private int movesPerTurn, noOfPlayers;

	public GameParameters(double defensiveBonus, int movesPerTurn,
			int noOfPlayers)
	{
		this.defensiveBonus = defensiveBonus;
		this.movesPerTurn = movesPerTurn;
		this.noOfPlayers = noOfPlayers;
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
}
