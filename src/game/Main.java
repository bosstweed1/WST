package game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import entities.Bullet;
import entities.Coin;
import entities.Death;
import entities.Effect;
import entities.Gun;
import entities.Player;
import entities.Shape;
import entities.Shield;
import entities.Wall;




public class Main 
{
	private static final int WIDTH = 540; // Width of the game window
	private static final int HEIGHT = 540; // Height of the game window
	
	enum State
	{
		INTRO, MAIN_MENU, LEADERBOARD, LEVEL, PAUSE, LOSE, EXIT;
	}
	
	private static UnicodeFont uniFont;
	
	private static State state = State.INTRO;
	
	private ArrayList<String> leaderboard = new ArrayList<String>();
	
	// Main menu variables
	
	private int MM_row = 1;
	
	// Level variables
	
	private Death [] rockObjects = new Death[5];
	private Death [] fireObjects = new Death[5];
	private Wall [] wallObjects = new Wall[2];
	private Player thePlayer;
	private Shield theShield;
	private Gun theGun;
	private Effect [] theEffect = new Effect[1];
	private Coin [] coinObjects = new Coin[5];
	private Bullet [] bulletObjects = new Bullet[100];
	private int bulletIndex = 0;
	private ArrayList<Shape[]> shapes = new ArrayList<Shape[]>();
	
	private static Map<Integer,String> coinPatternMap = createCoinPatternMap();

	
	private int previousDirection = 1;
	private double initialSpeed = 6;
	private double degenRate = .95;
	private double minSpeed = 3;
	private int playerScore = 0;
	private String aEffect = null;
	private int bonusTotal = 0;
	private int bonusStreak = 0;
	private boolean checkTotal = true;
	private int oldCoinTotal = 0;
	
	private String[] effectStrings = new String[3];
	
	
	
	// Pause variables
	
	private int P_row = 1;
	
	// Lose variables
	
	private int L_row = 1;
	
	
	private final int FONT_SIZE = 14;
	
	
	
	
	/* createCoinPatternMap()
	 * 		Create and initialize our mappings from code -> shape to 
	*/
	private static Map<Integer,String> createCoinPatternMap()
	{
		
		HashMap<Integer,String> tempMap = new HashMap<Integer,String>();
		
		tempMap.put( 0, "straight" );
		tempMap.put( 1, "left" );
		tempMap.put( 2, "right" );
		tempMap.put( 3, "longLeft" );
		tempMap.put( 4, "longRight" );
		tempMap.put( 5, "swerveLeft" );
		tempMap.put( 6, "swerveRight" );
		
		return ( Collections.unmodifiableMap(tempMap) );
		
	}
	/* Main function that will initialze the game
	 * 
	 */
	public static void main(String [] args)
	{
		new Main();
	}
	
	/* Main object that will initialze the game
	 * 
	 */
	public Main()
	{
		initGL();
		initFonts();
		initLeaderboard();
		
		// Main Game Shell Loop
		while (!Display.isCloseRequested())
		{
			glClear(GL_COLOR_BUFFER_BIT);
			render();
			input();
			update();
			Display.update();
			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);
		
	}
	
	/* initLeaderboard
	 * 		initialize the highscores
	 *
	 */
	private void initLeaderboard()
	{
		String fileName = "leaderboard.txt";
		try
		{
			Scanner fScan = new Scanner( new File( fileName ) );
			while ( fScan.hasNextLine() )
			{
				leaderboard.add( fScan.nextLine() );
		    }       
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}

	/* initGL
	 * 		Standard GL initialization 
	 *
	 */
	public void initGL()
	{
		try
		{
			Display.setDisplayMode( new DisplayMode( WIDTH, HEIGHT ) );
			Display.setTitle( "WST" );
			Display.setVSyncEnabled( true );
			Display.create();
		}
		catch ( LWJGLException e )
		{
			e.printStackTrace();
		}

		glMatrixMode( GL_PROJECTION );
		glLoadIdentity();
		glOrtho( 0, WIDTH, HEIGHT, 0, -1, 1 );
		glMatrixMode( GL_MODELVIEW );
	}
	
	/* initFonts
	 * 		This takes time... adds textures for plain text used in conversations
	 *		TODO: can we get rid of suppress warnings
	 */
	@SuppressWarnings( "unchecked" )
	private void initFonts()
	{

		Font awtFont = new Font( "", Font.PLAIN, FONT_SIZE );

		uniFont = new UnicodeFont( awtFont, FONT_SIZE, false, false );
		uniFont.addAsciiGlyphs();
		uniFont.addGlyphs( 400, 600 );           // Setting the unicode Range
		uniFont.getEffects().add( new ColorEffect( java.awt.Color.white ) );
		
		try
		{
			uniFont.loadGlyphs();
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}

		System.out.println( "Fonts initialized!" );
	}
	
	
	/* render
	 * 		Main drawing function, handles drawing everything in the shell except text and values 
	 * 		
	 */
	private void render()
	{
		/*
		System.out.println("x: " + thePlayer.getX());
		System.out.println("y: " + thePlayer.getY());
		*/
		switch ( state )
		{
			case INTRO:
				drawIntro();
				break;
			
			case MAIN_MENU:
				drawMain();
				break;
			
			case LEADERBOARD:
				drawLeaderboard();
				break;
	
			case LEVEL:
				update();
				drawLevel();
				break;
				
			case PAUSE:
				drawPause();
				break;
				
			case LOSE:
				drawLose();
				break;
				
			case EXIT:
				Display.destroy();
				System.exit( 0 );
				break;
		}
	}

	/* drawPause
	 * 		Renders everything for the Pause Menu
	 * 		
	 */
	private void drawPause() 
	{
		
		uniFont.drawString( WIDTH / 2, ( HEIGHT / 8 ), "Pause" );
		uniFont.drawString( WIDTH / 2, ( 2 * HEIGHT / 8 ), "Resume" );
		uniFont.drawString( WIDTH / 2, ( 3 * HEIGHT / 8 ), "Restart" );
		uniFont.drawString( WIDTH / 2, ( 4 * HEIGHT / 8 ), "Quit to Main Menu" );
		
	}
	
	/* drawLose
	 * 		Renders everything for the Loss Menu
	 * 		
	 */
	private void drawLose() 
	{
		
		uniFont.drawString( WIDTH / 2, ( HEIGHT / 8 ), "You lost: Score-" + playerScore );
		uniFont.drawString( WIDTH / 2, ( 2 * HEIGHT / 8 ), "Restart" );
		uniFont.drawString( WIDTH / 2, ( 3 * HEIGHT / 8 ), "Back to Main Menu" );
		
	}
	
	/* drawIntro
	 * 		Renders everything for the intro
	 * 		
	 */
	private void drawIntro() 
	{
		uniFont.drawString( WIDTH / 2, HEIGHT / 2, "Bosscoding" );
	}

	/* drawMain
	 * 		Renders everything for the MainMenu
	 * 		
	 */
	private void drawMain()
	{
		uniFont.drawString( WIDTH / 2, ( HEIGHT / 8 ), "WST" );
		uniFont.drawString( WIDTH / 2, ( 2 * HEIGHT / 8 ), "Main Menu" );
		uniFont.drawString( WIDTH / 2, ( 3 * HEIGHT / 8 ), "Play" );
		uniFont.drawString( WIDTH / 2, ( 4 * HEIGHT / 8 ), "Leaderboard" );
		uniFont.drawString( WIDTH / 2, ( 5 * HEIGHT / 8 ), "Exit" );
		
	}

	/* drawLeaderboard
	 * 		Renders everything for the leaderboard
	 * 		
	 */
	private void drawLeaderboard() 
	{
		uniFont.drawString( WIDTH / 2, ( HEIGHT / 10 ), "Leaderboard" );
		
		for ( int i = 0; i < 5; i++ )
		{
			uniFont.drawString( WIDTH / 2, ( ( 2 + i ) * HEIGHT / 8 ), leaderboard.get( i ) );
			
		}
	}

	/* drawLevel
	 * 		Renders everything for the level
	 * 		
	 */
	private void drawLevel() 
	{
		uniFont.drawString( 32, 3, "Score: " + playerScore );
		uniFont.drawString( WIDTH / 3, 3, "Coins: " + thePlayer.getCoinTotal() );
		
		if ( thePlayer.getEffect() != -1 )
			uniFont.drawString( 2 * ( WIDTH / 3), 3, "ActiveEffect: " + aEffect );
		else
			uniFont.drawString( 2 * ( WIDTH / 3), 3, "ActiveEffect: None" );
		
		
		thePlayer.draw();
		thePlayer.drawHitbox();
		
		if ( theShield != null )
		{
			theShield.draw();
			theShield.drawHitbox();
		}
		
		if ( theGun != null )
		{
			theGun.draw();
			theGun.drawHitbox();
		}
		
		if ( theEffect[0] != null && theEffect[0].getY() > HEIGHT )
			theEffect[0] = null;
		
		if ( theEffect[0] == null )
			setNewEffect();
		
		if ( theEffect[0] != null )
		{
			theEffect[0].setY( theEffect[0].getY() + 2 );
			if ( !theEffect[0].getRemoval() )
			{
				theEffect[0].draw();
				theEffect[0].drawHitbox();
			}
		}
		
		for ( int i = 0; i < bulletObjects.length; i++ )
		{
			if ( bulletObjects[i] == null )
				break;
			
			bulletObjects[i].setY( bulletObjects[i].getY() - 4 );
			
			bulletObjects[i].draw();
			bulletObjects[i].drawHitbox();
			
			if ( bulletObjects[i].getY() > -4 )
			{
				bulletObjects[i] = null;
				bulletIndex--;
			}
			
			
		}
		
		// Manage coins and rocks
		for ( int i = 0; i < 5; i++ )
		{		
				
			// Create rock objects
			if ( rockObjects[i] == null || rockObjects[4].getY() > HEIGHT )
				setDeathShape( 1, rockObjects );
				
			// Create fire objects
			if ( fireObjects[i] == null || fireObjects[4].getY() < ( -8 - fireObjects[i].getHeight() ) )
				setDeathShape( 0, fireObjects );
			
			// Create coin objects
			if ( coinObjects[4].getY() > HEIGHT )
			{
				setCoinShape( false );
				checkTotal = true;
			}
			
			// Update object's height 
			coinObjects[i].setY( coinObjects[i].getY() + 2 );
			rockObjects[i].setY( rockObjects[i].getY() + 3 );
			fireObjects[i].setY( fireObjects[i].getY() - 1 );
			
			
			
			
			// Draw coins
			if ( !coinObjects[i].getRemoval() )
			{
				coinObjects[i].draw();
				coinObjects[i].drawHitbox();
			}
			
			// Draw rocks
			rockObjects[i].draw();
			rockObjects[i].drawHitbox();
			
			// Draw fire objects
			fireObjects[i].draw();
			fireObjects[i].drawHitbox();
		}
		
		wallObjects[0].draw();
		wallObjects[0].drawHitbox();
		
		wallObjects[1].draw();
		wallObjects[1].drawHitbox();
		
		
		if ( previousDirection == thePlayer.getDir() )
			thePlayer.setPlayerMovementAmt( thePlayer.getPlayerMovementAmt() * degenRate );
		else
			thePlayer.setPlayerMovementAmt( initialSpeed );
		
		if ( thePlayer.getPlayerMovementAmt() < minSpeed )
			thePlayer.setPlayerMovementAmt( minSpeed );
		
		/*
		System.out.println("speed: " + thePlayer.getPlayerMovementAmt() );
		System.out.println("x: " + thePlayer.getX() );
		System.out.println("dir:" + thePlayer.getDir() );
		*/
		thePlayer.setX( thePlayer.getX() + thePlayer.getPlayerMovementAmt() * thePlayer.getDir() );
		
		if ( thePlayer.getEffect() == 0 )
			theShield.setX( thePlayer.getX() );
		
		previousDirection = thePlayer.getDir();
	}
	
	
	private void setNewEffect() 
	{
		Random rng = new Random();
		
		Integer xPos = Math.abs( rng.nextInt() % WIDTH - 64 ) + 32;
		
		if ( thePlayer.getEffectStatus() )
			theEffect[0] = new Effect( xPos, -16, 16, 16, (int)(thePlayer.getX() % 2) );
			
		
	}
	private void setDeathShape( int type, Shape [] array ) 
	{
		Random rng = new Random();
		
		for ( int i = 0; i < 5; i++ )
		{
		
			Integer xPos = Math.abs( rng.nextInt() % WIDTH - 64 ) + 32;
			
			if ( xPos > WIDTH - 48 )
				xPos -= 48;
			// If we want a new random rock 
			if ( type == 1)
				array[i] = new Death(  xPos, (-16) - i * (HEIGHT / 5), 16, 16 );
			else
				array[i] = new Death(  xPos, HEIGHT + 16 + i * (HEIGHT / 5), 16, 16 );
		}
	}
	private void setCoinShape( boolean init ) 
	{
		Random rng = new Random();
		Integer pattern = Math.abs( rng.nextInt() % 7 );
		int startingX = Math.abs( ( rng.nextInt() % 468 ) ) + 32;
		
		boolean noLeft = false;
		boolean noRight = false;
		boolean noLongRight = false;
		boolean noLongLeft = false;
		
		int smallHInc = 24;
		int largeHInc = 36;
		int smallVInc = 24;
		int largeVInc = 36;
		
		
		if ( init )
			for ( int i = 0; i < coinObjects.length; i++ )
				coinObjects[i] = new Coin( ( startingX - ( 8 * i ) ), -largeHInc * i, 8, 8 );
		
		String patternString = coinPatternMap.get( pattern );
		/*
		System.out.println("pattern: " + pattern );
		System.out.println("orig: " + startingX );
		*/
		if ( ( startingX - ( smallHInc * 4 ) ) < 32 )
		{
			noLeft = true;
			//System.out.println("NOLEFT");
		}
		
		if ( ( startingX - ( largeHInc * 4 ) ) < 32 )
		{
			noLongLeft = true;
			//System.out.println("NOLONGLEFT");
		}
		
		if ( ( startingX + ( smallHInc * 4 ) ) > ( WIDTH - 32 ) )
		{
			noRight = true;
			//System.out.println("NORIGHT");
		}
		
		if ( ( startingX + ( largeHInc * 4 ) ) > ( WIDTH - 32 ) )
		{
			noLongRight = true;
			//System.out.println("NOLONGRIGHT");
		}
			
		
		if ( patternString.equals("straight") )
		{
			for ( int i = 0; i < coinObjects.length; i++ )
			{
				coinObjects[i].setX( startingX );
				coinObjects[i].setY( -smallVInc * i );
				coinObjects[i].setRemoval( false );
			}
			
		}
		else if ( patternString.equals("left") )
		{
			if ( noLeft )
				startingX += ( smallHInc * 4 );
			
			for ( int i = 0; i < coinObjects.length; i++ )
			{
				
				coinObjects[i].setX( startingX - ( smallHInc * i ) );
				coinObjects[i].setY( -largeVInc * i );
				coinObjects[i].setRemoval( false );
			}
		}
		else if ( patternString.equals("right") )
		{
			if ( noRight )
				startingX -= ( smallHInc * 4 );
			
			for ( int i = 0; i < coinObjects.length; i++ )
			{
				coinObjects[i].setX( startingX + ( smallHInc * i ) );
				coinObjects[i].setY( -largeVInc * i );
				coinObjects[i].setRemoval( false );
			}
		}
		else if ( patternString.equals("longLeft") )
		{
			if ( noLongLeft )
				startingX += ( largeHInc * 4 );
			
			for ( int i = 0; i < coinObjects.length; i++ )
			{
				
				coinObjects[i].setX( startingX - ( largeHInc * i )  );
				coinObjects[i].setY( -smallVInc * i );
				coinObjects[i].setRemoval( false );
			}
			
		}
		else if ( patternString.equals("longRight") )
		{
			if ( noLongRight )
				startingX -= ( largeHInc * 4 );
			
			for ( int i = 0; i < coinObjects.length; i++ )
			{
				coinObjects[i].setX( startingX + ( largeHInc * i )  );
				coinObjects[i].setY( -smallVInc * i );
				coinObjects[i].setRemoval( false );
			}
		}
		else if ( patternString.equals("swerveLeft") )
		{
			if ( noLeft )
				startingX += ( largeHInc * 2 );
			
			for ( int i = 0; i < coinObjects.length / 2; i++ )
			{
				
				coinObjects[i].setX( startingX - ( largeHInc * i ) );
				coinObjects[i].setY( -largeVInc * i );
				coinObjects[i].setRemoval( false );
			}
			
			coinObjects[2].setX( startingX - ( largeHInc * 2 ) );
			coinObjects[2].setY( -largeVInc * 2 );
			coinObjects[2].setRemoval( false );
			
			
			for ( int i = 3; i < coinObjects.length; i++ )
			{	
				coinObjects[i].setX( ( startingX - ( largeHInc * 2 ) ) + (largeHInc * ( i - 2 ) ) );
				coinObjects[i].setY( -largeVInc * i );
				coinObjects[i].setRemoval( false );
			}
		}
		else if ( patternString.equals("swerveRight") )
		{
			if ( noRight )
				startingX -= ( largeHInc * 2 );
				

			for ( int i = 0; i < coinObjects.length / 2; i++ )
			{
				
				coinObjects[i].setX( startingX + ( largeHInc * i ) );
				coinObjects[i].setY( -largeVInc * i );
				coinObjects[i].setRemoval( false );
			}
			
			coinObjects[2].setX( startingX + ( largeHInc * 2 ) );
			coinObjects[2].setY( -largeVInc * 2 );
			coinObjects[2].setRemoval( false );
			
			
			for ( int i = 3; i < coinObjects.length; i++ )
			{	
				coinObjects[i].setX( ( startingX + largeHInc * 2 ) - ( largeHInc * ( i - 2 ) ) );
				coinObjects[i].setY( -largeVInc * i );
				coinObjects[i].setRemoval( false );
			}
		}
		
	}
	
	
	/* input
	 * 		Input function
	 * 		
	 */
	private void input()
	{
		
	
		switch ( state )
		{
		
		
		case INTRO:
			
			if ( pressed( Keyboard.KEY_RETURN ) || pressed( Keyboard.KEY_SPACE ) )
				state = State.MAIN_MENU;
			break;
		
		case MAIN_MENU:
			
			if ( pressed( Keyboard.KEY_UP ) && MM_row > 1)
			{
				MM_row--;
				// TODO: add transparancy box around selections to move up and down for selections
			} 
			else if ( pressed( Keyboard.KEY_DOWN ) && MM_row < 3 )
			{
				MM_row++;
				// TODO: add transparancy box around selections to move up and down for selections
			} 
			else if ( pressed( Keyboard.KEY_RETURN ) )
			{
				if ( MM_row == 1 )
				{
					initLevel();
					state = State.LEVEL;
				}
				else if ( MM_row == 2 )
					state = State.LEADERBOARD;
				else
					state = State.EXIT;
				
			}
			break;
			
		case LEADERBOARD:
			
			if ( pressed( Keyboard.KEY_ESCAPE ) )
				state = State.MAIN_MENU;
			
			break;
			
			
		case LEVEL:
			if ( pressed( Keyboard.KEY_LEFT) )
				move( -1 );
			
			if ( pressed( Keyboard.KEY_RIGHT) )
				move( 1 );
			
			if ( pressed( Keyboard.KEY_ESCAPE) )
			{
				state = State.PAUSE;
				P_row = 1;
			}
			
			if ( pressed( Keyboard.KEY_SPACE) && theGun != null )
			{
				bulletObjects[bulletIndex] = theGun.shoot();
				bulletIndex++;
			}
				
			
		case PAUSE:
			if ( pressed( Keyboard.KEY_UP ) && P_row > 1)
			{
				P_row--;
				// TODO: add transparancy box around selections to move up and down for selections
			} 
			else if ( pressed( Keyboard.KEY_DOWN ) && P_row < 3 )
			{
				P_row++;
				// TODO: add transparancy box around selections to move up and down for selections
			} 
			else if ( pressed( Keyboard.KEY_RETURN ) )
			{
				if ( P_row == 1 )
				{
					state = State.LEVEL;
				}
				else if ( P_row == 2 )
				{
					initLevel();
					state = State.LEVEL;
				}
				else
					state = State.MAIN_MENU;
				
			}
			
		case LOSE:
			if ( pressed( Keyboard.KEY_UP ) && L_row > 1)
			{
				L_row--;
				// TODO: add transparancy box around selections to move up and down for selections
			} 
			else if ( pressed( Keyboard.KEY_DOWN ) && L_row < 2 )
			{
				L_row++;
				// TODO: add transparancy box around selections to move up and down for selections
			} 
			else if ( pressed( Keyboard.KEY_RETURN ) )
			{
				if ( L_row == 1 )
				{
					initLevel();
					state = State.LEVEL;
				}
				else
				{
					state = State.MAIN_MENU;
					thePlayer = null;
				}
				
			}
			
		}

	}
	
	private void initLevel() 
	{
		// Initialize Level objects
		thePlayer = new Player( (WIDTH / 2) - 16, ( ( 3 * HEIGHT ) / 5 ), 32, 32 );
		wallObjects[0] = new Wall( 0, 0, 32, HEIGHT );
		wallObjects[1] = new Wall( WIDTH - 32, 0, 32, HEIGHT );
		theEffect[0] = null;
		setCoinShape( true );
		theShield = null;
		
		effectStrings[0] = "Shield"; //MY SHIELD...FOR ARGUS!
		effectStrings[1] = "Gun"; //I've got a HUGE gun
		effectStrings[2] = "Ink"; // I FEEL ICKY!
		
		// Reset death objects
		setDeathShape( 1, rockObjects );
		setDeathShape( 0, fireObjects );
		
		// Reset scores
		playerScore = 0;
		bonusTotal = 0;
		bonusStreak = 0;
		oldCoinTotal = 0;
		
		shapes.add( rockObjects );
		shapes.add( fireObjects );
		shapes.add( wallObjects );
		shapes.add( coinObjects ); 
		shapes.add( theEffect );
		
		
	}

	/* move
	 * 		moves the player
	 * 		
	 */
	private void move( int direction )
	{		
		thePlayer.setDir( direction );
	}
	
	/* pressed
	 * 		Cleans up the input method, fixes keyboard when we need to.
	 * 		
	 */
	private boolean pressed( int keypress )
	{
		
		boolean isPressed = Keyboard.isKeyDown( keypress );
		
		if ( isPressed && keypress != Keyboard.KEY_LEFT && keypress != Keyboard.KEY_RIGHT )
			fixKeyboard();
		
		return ( isPressed );
		
	}
	
	
	// TODO : can this be done in a cleaner way?
	/* fixKeyboard
	 * 		Destroys and creates a new keyboard to disallow for holding down keys
	 * 		when we don't want to let the user do that
	 */
	private void fixKeyboard()
	{
		Keyboard.destroy();
		try
		{
			Keyboard.create();
		}
		catch ( LWJGLException e )
		{
			e.printStackTrace();
		}
	}
	
	/* update
	 * 		any updates that need to happen in the game
	 * 		
	 */
	private void update()
	{
		if ( thePlayer != null && thePlayer.getAlive() )
		{
			
			// Bonus logic, if you get all coins in a bunch you get 5 extra points, you get 5 additional points for each consec streak
			if ( checkTotal)
			{
				if ( thePlayer.getCoinTotal() - oldCoinTotal == 5 )
				{
					bonusStreak++;
					bonusTotal += bonusStreak * 5;
				}
				else
					bonusStreak = 0;
				
				oldCoinTotal = thePlayer.getCoinTotal();
				checkTotal = false;
			}
				
			// Iterate through each shape array
			for ( Shape[] s : shapes )
				// Iterate through each object in the current shape array
				for ( Shape thisShape : s )
				{
					boolean intersected = thePlayer.intersects( thisShape );
					
					if ( thisShape != null && intersected )
					{
						if ( thisShape.getShapeType() == 3 )
							theShield = new Shield ( thePlayer.getX() - 3 , thePlayer.getY() - 3, thePlayer.getWidth() + 6, thePlayer.getHeight() + 6 );
						else if ( thisShape.getShapeType() == 4 )
							theGun = new Gun ( thePlayer.getX() - 3 , thePlayer.getY() - 3, thePlayer.getWidth() + 6, thePlayer.getHeight() + 6 );

					}
				}
			
			playerScore = thePlayer.getCoinTotal() + bonusTotal;
			
			if ( playerScore > 1 && theEffect[0] == null)
			{
				thePlayer.setEffectStatus( true );
				
			}
			else
			{
				if ( thePlayer.getEffect() != -1 )
				{
					aEffect = effectStrings[ thePlayer.getEffect() ];
				}
				else if ( thePlayer.getEffect () == 0 )
				{
					theShield = null;
				}
				else if ( thePlayer.getEffect() == 1 )
				{
					theGun = null;
				}
						
			}
			
		}
		else if ( thePlayer != null && !thePlayer.getAlive() && state != State.LOSE )
		{
			state = State.LOSE;
			L_row = 1;
		}
		
	}
	
}


