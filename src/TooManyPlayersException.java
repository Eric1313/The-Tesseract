/**
 * An exception for having too many players
 * @author Logan Grier
 * @version 03/01/2015
 */
public class TooManyPlayersException extends Exception
{
	public TooManyPlayersException(int numberOfPlayers, int maxNumOfPlayers,int noOfDimentions)
	{
		System.out.printf("Tried to create a game with %d players. No more than %d players are aloud in a game with %d dimentions.",numberOfPlayers,maxNumOfPlayers,noOfDimentions);
	}
}
