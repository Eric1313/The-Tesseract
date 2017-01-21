/**
 * Stores all data related to a border tile
 * @author Logan Grier
 * @version 03/01/2015
 */
public class BorderTile
{
	private int[] coordinates;
	public int unitsRequiredForMoves;

	/**
	 * Stores the information related to a border tile
	 * @param coordinates The location of the border tile
	 */
	public BorderTile(int[] coordinates)
	{
		this.coordinates = coordinates;
		this.unitsRequiredForMoves=0;
	}

	/**
	 * 
	 * @return The coordinates of the border tile
	 */
	public int[] getCoordinates()
	{
		return coordinates;
	}
}
