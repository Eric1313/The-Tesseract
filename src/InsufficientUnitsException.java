/**
 * An exception thrown when a player tried to place more units on a tile than
 * they have to place
 * @author Logan Grier
 * @version 25/12/2014
 */
public class InsufficientUnitsException extends Exception
{
	public InsufficientUnitsException(int numberOfUnitsAvailable,
			int numberOfUnitsPlaced, int player)
	{
		System.out
				.printf("Player %d tried to place/move %d units while there were only %d units available.",
						player, numberOfUnitsPlaced, numberOfUnitsAvailable);
	}
}
