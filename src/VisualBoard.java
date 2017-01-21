/* A set of classes to parse, represent and display 3D wireframe models
   represented in Wavefront .obj format. 
   This code I pulled out of another file and modified - Ridout
   http://infohost.nmt.edu/~armiller/java/wirefram/
   Modified for the propose of this project
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The representation of a 3D model Source
 * http://infohost.nmt.edu/~armiller/java/wirefram/
 * @author Alan R. Miller(Original Wireframe), G. Ridout, Eric Chee
 */
public class VisualBoard
{
	//Variable declaration
	float[] vert;
	int[] transVert;
	int nvert, maxvert;
	ArrayList<Surface> surfaces;
	boolean transformed;
	Matrix3D mat;
	Point position;
	Color[] colours;
	int lastSelectedCube;
	Surface lastSelectedSurface;

	float xmin, xmax, ymin, ymax, zmin, zmax;

	public VisualBoard()
	{
		mat = new Matrix3D();
		mat.xrot(20);
		mat.yrot(30);
		surfaces = new ArrayList<Surface>();
	}

	/**
	 * Creates a new 3d object
	 * @param newPos Screen position
	 * @param colour Default colour of the cube
	 * @param size Side length of object in cubes (any odd number)
	 * @param shift The spacing between the cube
	 */
	public VisualBoard(Point newPos, Color colour, int size, float shift)
	{
		this();
		this.position = new Point(newPos);
		colours = new Color[729];// 125
		lastSelectedCube = -1;
		lastSelectedSurface = null;

		// Create the cubes
		int offSet = 0;
		int boardSize = (int) (((size - 3) / 2) * shift);
		System.out.println(boardSize);
		for (float dx = -shift - boardSize; dx <= shift + boardSize; dx += shift)
			for (float dy = -shift - boardSize; dy <= shift + boardSize; dy += shift)
				for (float dz = -shift - boardSize; dz <= shift + boardSize; dz += shift)
				{

					addVert(1.0f + dx, -1.0f + dy, -1.0f + dz);
					addVert(1.0f + dx, -1.0f + dy, 1.0f + dz);
					addVert(-1.0f + dx, -1.0f + dy, 1.0f + dz);
					addVert(-1.0f + dx, -1.0f + dy, -1.0f + dz);
					addVert(1.0f + dx, 1.0f + dy, -1.0f + dz);
					addVert(1.0f + dx, 1.0f + dy, 1.0f + dz);
					addVert(-1.0f + dx, 1.0f + dy, 1.0f + dz);
					addVert(-1.0f + dx, 1.0f + dy, -1.0f + dz);

					// Create surfaces with default blue colour
					int cubeNo = offSet / 8;
					colours[cubeNo] = Color.GRAY;
					addSurface(0 + offSet, 1 + offSet, 2 + offSet, 3 + offSet,
							cubeNo);
					addSurface(4 + offSet, 7 + offSet, 6 + offSet, 5 + offSet,
							cubeNo);
					addSurface(0 + offSet, 4 + offSet, 5 + offSet, 1 + offSet,
							cubeNo);
					addSurface(2 + offSet, 6 + offSet, 7 + offSet, 3 + offSet,
							cubeNo);
					addSurface(1 + offSet, 5 + offSet, 6 + offSet, 2 + offSet,
							offSet / 8);
					addSurface(4 + offSet, 0 + offSet, 3 + offSet, 7 + offSet,
							cubeNo);
					offSet += 8;

				}
	}

	/** Add a vertex to this model */
	int addVert(float x, float y, float z)
	{
		int i = nvert;
		if (i >= maxvert)
			if (vert == null)
			{
				maxvert = 100;
				vert = new float[maxvert * 3];
			}
			else
			{
				maxvert *= 2;
				float nv[] = new float[maxvert * 3];
				System.arraycopy(vert, 0, nv, 0, vert.length);
				vert = nv;
			}
		i *= 3;
		vert[i] = x;
		vert[i + 1] = y;
		vert[i + 2] = z;
		return nvert++;
	}

	/** Add a surface from 4 vertexes Assume they are in proper order */
	void addSurface(int p1, int p2, int p3, int p4, int cube)
	{
		// Check if valid
		if (p1 >= nvert || p2 >= nvert || p3 >= nvert || p4 >= nvert)
			return;

		surfaces.add(new Surface(p1, p2, p3, p4, cube));
	}

	// Inner class for each surface
	class Surface implements Comparable<Surface>
	{
		int p1;
		int p2;
		int p3;
		int p4;
		int cube;

		public Surface(int newP1, int newP2, int newP3, int newP4, int cube)
		{
			this.p1 = newP1;
			this.p2 = newP2;
			this.p3 = newP3;
			this.p4 = newP4;
			this.cube = cube;
		}

		// Used for sorting to draw surfaces with a higher z last (so they are
		// on top)
		public int zScore()
		{

			return transVert[p1 * 3 + 2] + transVert[p2 * 3 + 2]
					+ transVert[p3 * 3 + 2] + transVert[p4 * 3 + 2];

		}

		public int compareTo(Surface other)
		{

			return this.zScore() - other.zScore();
		}

	}

	/** Transform all the points in this model */
	void transform()
	{
		if (transformed || nvert <= 0)
			return;
		if (transVert == null || transVert.length < nvert * 3)
			transVert = new int[nvert * 3];
		mat.transform(vert, transVert, nvert);
		transformed = true;
	}

	/**
	 * Paint this model to a graphics context. It uses the matrix associated
	 * with this model to map from model space to screen space.
	 */
	void paint(Graphics g)
	{
		if (vert == null || nvert <= 0)
			return;
		transform();

		// Sort surfaces so that the surface with the higher z value is drawn
		// last (on top)
		Collections.sort(surfaces);
		for (Surface nextSurface : surfaces)
		{

			// Get the 4 points indexes out of the surfaces List
			int p1 = nextSurface.p1 * 3;
			int p2 = nextSurface.p2 * 3;
			int p3 = nextSurface.p3 * 3;
			int p4 = nextSurface.p4 * 3;

			// Create points for surface polygon
			int[] xPoints = new int[] { transVert[p1], transVert[p2],
					transVert[p3], transVert[p4] };

			int[] yPoints = new int[] { transVert[p1 + 1], transVert[p2 + 1],
					transVert[p3 + 1], transVert[p4 + 1] };

			// Shift the points relative to x, y position
			for (int i = 0; i < xPoints.length; i++)
			{
				xPoints[i] += position.x;
				yPoints[i] += position.y;
			}

			// Surface
			g.setColor(colours[nextSurface.cube]);
			g.fillPolygon(xPoints, yPoints, 4);

			// Highlight last selected cube
			if (lastSelectedCube == nextSurface.cube)
			{
				g.setColor(new Color(255, 255, 255, 100));
				g.fillPolygon(xPoints, yPoints, 4);
			}
			g.setColor(Color.BLACK);
			g.drawPolygon(xPoints, yPoints, 4);

		}

	}

	/**
	 * Checks if a cube has been clicked
	 * @param x Mouse X position
	 * @param y Mouse y position
	 * @return The number of the cube clicked (-1 if none)
	 */
	public int checkClicked(int x, int y)
	{
		// Sort in regular order to check front cubes first
		Collections.sort(surfaces, Collections.reverseOrder());
		for (Surface nextSurface : surfaces)
		{
			// Get the 4 points indexes out of the surfaces List
			int p1 = nextSurface.p1 * 3;
			int p2 = nextSurface.p2 * 3;
			int p3 = nextSurface.p3 * 3;
			int p4 = nextSurface.p4 * 3;

			// Create polygons to see if point is in polygon
			// This is done while drawing too
			// May want to eliminate the duplicate code?
			Polygon poly = new Polygon();
			int[] xPoints = new int[] { transVert[p1], transVert[p2],
					transVert[p3], transVert[p4] };

			int[] yPoints = new int[] { transVert[p1 + 1], transVert[p2 + 1],
					transVert[p3 + 1], transVert[p4 + 1] };

			// Shift the points relative to x, y position
			for (int i = 0; i < xPoints.length; i++)
			{
				xPoints[i] += position.x;
				yPoints[i] += position.y;
				poly.addPoint(xPoints[i], yPoints[i]);
			}

			// Toggle the colour of the selected cube
			if (poly.contains(x, y))
			{
				int selectedCube = nextSurface.cube;
				return selectedCube;
			}
		}
		return -1;
	}

	/**
	 * Checks if a cube has been clicked and highlights it
	 * @param x Mouse X position
	 * @param y Mouse y position
	 * @return The number of the cube clicked (-1 if none)
	 */
	public int leftClickCube(int x, int y)
	{
		// Sort in regular order to check front cubes first
		Collections.sort(surfaces, Collections.reverseOrder());
		for (Surface nextSurface : surfaces)
		{
			// Get the 4 points indexes out of the surfaces List
			int p1 = nextSurface.p1 * 3;
			int p2 = nextSurface.p2 * 3;
			int p3 = nextSurface.p3 * 3;
			int p4 = nextSurface.p4 * 3;

			// Create polygons to see if point is in polygon
			// This is done while drawing too
			// May want to eliminate the duplicate code?
			Polygon poly = new Polygon();
			int[] xPoints = new int[] { transVert[p1], transVert[p2],
					transVert[p3], transVert[p4] };

			int[] yPoints = new int[] { transVert[p1 + 1], transVert[p2 + 1],
					transVert[p3 + 1], transVert[p4 + 1] };

			// Shift the points relative to x, y position
			for (int i = 0; i < xPoints.length; i++)
			{
				xPoints[i] += position.x;
				yPoints[i] += position.y;
				poly.addPoint(xPoints[i], yPoints[i]);

			}

			// Selects the cube to be highlighted
			if (poly.contains(x, y))
			{
				lastSelectedSurface = nextSurface;
				int selectedCube = nextSurface.cube;
				lastSelectedCube = selectedCube;
				return selectedCube;
			}
		}
		lastSelectedSurface = null;
		lastSelectedCube = -1;
		return -1;
	}

	/**
	 * Un-highlights all cubes
	 */
	public void unSelectCube()
	{
		lastSelectedCube = -1;
	}

}
