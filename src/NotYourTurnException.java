/**
 * An exception for a player trying to make a move on a tile that is not theirs
 * @author Logan Grier
 * @version 25/12/2014
 */
public class NotYourTurnException extends Exception
{
	public NotYourTurnException(int correctPlayer, int attemptedPlayer)
	{
		System.out.printf(
				"Attempted move on player %d's tile during player %d's turn.",
				attemptedPlayer, correctPlayer);
	}
}
