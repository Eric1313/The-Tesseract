/**
 * Stores information related to a possible attack
 * @author Logan Grier
 * @version 17/12/2015
 */
public class OffensiveMove implements Comparable<OffensiveMove>
{
	public int[] target;
	public int[] source;
	public double desirability;
	public int unitsRequired;
	
	/**
	 * Creates a new attack
	 * @param target The tile that might be attacked
	 * @param source The place where the tile is being attacked from
	 */
	public OffensiveMove(int[] target, int[] source)
	{
		this.target=target;
		this.source=source;
	}

	/**
	 * Make the object comparable based on the coordinates of its target
	 */
	public int compareTo(OffensiveMove anotherMove)
	{
		if(anotherMove.target[0]>this.target[0])
			return 1;
		else if (anotherMove.target[0]<this.target[0])
			return -1;
		else
			if(anotherMove.target[1]>this.target[1])
				return 1;
			else if (anotherMove.target[1]<this.target[1])
				return -1;
			else
				if(anotherMove.target[2]>this.target[2])
					return 1;
				else if (anotherMove.target[2]<this.target[2])
					return -1;
				else
					if(anotherMove.target[3]>this.target[3])
						return 1;
					else if (anotherMove.target[3]<this.target[3])
						return -1;
					else
						return 0;
	}
}
