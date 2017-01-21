/**
 * Stores all of the information related to making a defensive move
 * @author Logan Grier
 * @version 03/01/2015
 */
public class DefensiveMove
{
	public double desirability;
	public int unitsRequired;
	public int[] coordinates;
	
	/**
	 * Creates a defensive move
	 */
	public DefensiveMove(int[] coordinates)
	{
		this.coordinates=coordinates;
	}
}
