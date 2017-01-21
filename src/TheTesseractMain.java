import java.awt.*;
import javax.swing.*;

/**
 * Creates the frame and panel for the game
 * 
 * @author Eric Chee
 * @version 1/17/2015
 */
public class TheTesseractMain extends JFrame// implements ActionListener
{
	private TheTesserectPanel gamePanel;

	/**
	 * Constructs a new TheTesseract frame (sets up the Game)
	 */
	public TheTesseractMain()
	{
		// Sets up the frame for the game
		super("Final Project");
		setResizable(false);
		setUndecorated(true);
		setFocusable(false);

		// Sets up the game board that plays most of the game
		// and add it to the centre of this frame
		gamePanel = new TheTesserectPanel();
		add(gamePanel, BorderLayout.CENTER);
		gamePanel.setFocusable(true);

	}

	/**
	 * Starts up the ConnectFourMain frame
	 * 
	 * @param args An array of Strings (ignored)
	 */
	public static void main(String[] args)
	{
		// Starts up the ConnectFourMain frame
		TheTesseractMain frame = new TheTesseractMain();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	} // main method
}