/**
 * An exception for having not enough players
 * @author Logan Grier
 * @version 03/01/2015
 */
public class NotEnoughPlayersException extends Exception
{
	public NotEnoughPlayersException(int numberOfPlayers)
	{
		System.out.printf("Tried to create a game with %d players. At least 2 players needed to create a game.",numberOfPlayers);
	}
}
