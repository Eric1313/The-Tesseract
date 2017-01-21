/**
 * Throws an exception for illegal moves
 * @author Logan Grier
 * @version 25/12/2014
 *
 */
public class IllegalMoveException extends Exception
{
	public IllegalMoveException(String string)
	{
		System.out.println(string);
	}
}
