/**
 * An exception for giving the board constructor an improper number of dimensions
 * @author Logan Grier
 * @version 03/10/2014
 */
public class UnsuportedDimentionException extends Exception
{
	public UnsuportedDimentionException(int noOfDimentions)
	{
		System.out
				.printf("Tried to create a game with %d dimentions. The game may only have 2-4 dimension.",
						noOfDimentions);
	}
}
