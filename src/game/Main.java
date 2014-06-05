package game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.awt.Font;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

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

import entities.Background;
import entities.Bullet;
import entities.Coin;
import entities.Death;
import entities.Effect;
import entities.Gun;
import entities.Ink;
import entities.Inker;
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
	
	// All the textures used in the game
	
	public static Texture[] fireball = new Texture[1];
	public static Texture[] rock00 = new Texture[1];
	public static Texture[] rock10 = new Texture[1];
	public static Texture[] rock20 = new Texture[1];
	public static Texture[] shield = new Texture[1];
	public static Texture[] bullet = new Texture[1];
	public static Texture[] mana = new Texture[1];
	public static Texture[] wall = new Texture[1];
	public static Texture[] background = new Texture[1];
	public static Texture[] ink = new Texture[1];
	public static Texture[] octopus = new Texture [1];
	public static Texture[] gun = new Texture[1];
	public static Texture[] inker = new Texture[1];
	public static Texture[] shieldEffect = new Texture[1];
	public static Texture[] gunEffect = new Texture[1];
	public static Texture[] inkEffect = new Texture[1];
	
	
	public static Texture[][] rock = new Texture[3][1];
	public static Texture[][] effect = new Texture[3][1];
	
	
	public static Map<String,Texture[]> textureMap = createTextureMap();
	
	
	private ArrayList<Integer> leaderboard = new ArrayList<Integer>();
	private ArrayList<String> leaderboardNames = new ArrayList<String>();
	
	// Main menu variables
	
	private int MM_row = 1;
	
	// Level variables
	
	private Background theBackground;
	private Death [] rockObjects = new Death[5];
	private Death [] fireObjects = new Death[5];
	private Wall [] wallObjects = new Wall[2];
	private Player thePlayer;
	private Shield theShield;
	private Gun theGun;
	private Inker theInker;
	private Effect [] theEffect = new Effect[1];
	private Coin [] coinObjects = new Coin[5];
	private Bullet [] bulletObjects = new Bullet[5];
	private Ink [] inkObjects = new Ink[5];
	private int inkIndex = 0;
	private int bulletIndex = 0;
	private ArrayList<Shape[]> shapes = new ArrayList<Shape[]>();
	
	//Sounds
	private Audio[] shieldSounds = new Audio[3];
	private Audio[] gunSounds = new Audio[2];
	private Audio[] inkSounds = new Audio[1];
	private Audio[] deathSounds = new Audio[1];
	private Audio[] introSound = new Audio[1];
	private Audio fireSound;
	private Audio rockSound;
	
	//instruction sounds
	
	
	private static Map<Integer,String> coinPatternMap = createCoinPatternMap();

	// Movement Variables
	private int previousDirection = 1;
	private double initialSpeed = 6;
	private double degenRate = .95;
	private double minSpeed = 3;
	
	// Score Variables
	private int playerScore = 0;
	private int bonusTotal = 0;
	private int bonusStreak = 0;
	private boolean checkTotal = true;
	private int oldCoinTotal = 0;
	
	private String[] effectStrings = new String[3];
	
	
	// Pause variables
	
	private int P_row = 1;
	
	// Lose variables
	private int L_row = 1;
	private boolean enteredName = false;
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
	
	/* createTextureMap()
	 * 		Create and initialize our texture map and textures
	*/
	private static Map<String,Texture[]> createTextureMap()
	{
		
		HashMap<String,Texture[]> tempMap = new HashMap<String,Texture[]>();
		
		tempMap.put( "fireball", fireball);
		tempMap.put( "rock0", rock00);
		tempMap.put( "rock1", rock10);
		tempMap.put( "rock2", rock20);
		tempMap.put( "shield", shield);
		tempMap.put( "bullet", bullet);
		tempMap.put( "mana", mana);
		tempMap.put( "wall", wall);
		tempMap.put( "background", background );
		tempMap.put( "ink", ink );
		tempMap.put( "octopus", octopus );
		tempMap.put( "gun", gun );
		tempMap.put( "inker", inker );
		tempMap.put( "shieldEffect", shieldEffect );
		tempMap.put( "gunEffect", gunEffect );
		tempMap.put( "inkEffect", inkEffect );
			
		return ( Collections.unmodifiableMap(tempMap) );
	}
	
	
	/* loadAllTextures()
	 * 		This will load the textures in an efficient way into the map
	 */
	private static void loadAllTextures( Map<String,Texture[]> textMap )
	{
		for ( Map.Entry<String, Texture[]> entry : textMap.entrySet() )
		{
			int size = entry.getValue().length;
			String currEntry = entry.getKey();
			
			for ( int i = 0; i < size; i++ )
				entry.getValue()[i] = loadTexture(currEntry + "/" + currEntry + i);
		}
	}
	
	/* loadTexture
	 * 		Use TextureLoader to load all of the games images
	 * 
	 */
	public static Texture loadTexture( String key )
	{
		try
		{
			return TextureLoader.getTexture( "png", new FileInputStream( new File( "res/img/" + key + ".png" ) ) );
		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		return null;
	}
	
	
	/* Main object that will initialze the game
	 * 
	 */
	public Main()
	{
		initSounds();
		initGL();
		initFonts();
		initLeaderboard();
		loadAllTextures( textureMap );
		
		rock[0] = rock00;
		rock[1] = rock10;
		rock[2] = rock20;
		
		effect[0] = shieldEffect;
		effect[1] = gunEffect;
		effect[2] = inkEffect;
		
		
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
	
	
	/* initSounds
	 * 		Loads the sound effects and music
	 * 		TODO: add more sounds
	 */
	private void initSounds() 
	{
		try 
		{
			
			//Sounds
			shieldSounds[0] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/shieldForArgus.wav"));
			shieldSounds[1] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/shieldsUp.wav"));
			shieldSounds[2] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/truthIsMyShield.wav"));
			
			gunSounds[0] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/hugeGun.wav"));
			
			gunSounds[1] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/lockedAndLoaded.wav"));
			
			inkSounds[0] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/iFeelIcky.wav"));
			
			deathSounds[0] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/millhouseDeath.wav"));

			introSound[0] = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/wellMet.wav"));
			
			fireSound = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/playWithFire.wav"));
			
			rockSound = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sounds/wav/handleIt.wav"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

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
				leaderboard.add( Integer.parseInt( fScan.nextLine() ) );
		    }       
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		fileName = "leaderboardNames.txt";
		try
		{
			Scanner fScan = new Scanner( new File( fileName ) );
			while ( fScan.hasNextLine() )
			{
				leaderboardNames.add( fScan.nextLine() );
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
		
		checkHighScore();
		
		
		
	}
	
	/* checkHighScore
	 * 		checks if the previous game played was a high score, if so adds the score and username to the leaderboard
	 * 
	 */
	
	private void checkHighScore()
	{
		
		leaderboard.add(playerScore);
		
		// Find out if the score was a highscore, and where it falls
		if ( playerScore > leaderboard.get(4) && !enteredName )
		{
			enteredName = true;
			// pop up to add name
			String name = JOptionPane.showInputDialog("Congrats! You've Made the Leaderboard! Enter your name: ");
			if ( name == null )
			{
				name = "iCantType";
			}
			
			leaderboard.set(4, playerScore );
			leaderboardNames.set(4, name );
			
			// I'm a child...
			if ( playerScore > leaderboard.get(3) )
			{
				leaderboard.set(4, leaderboard.get(3) );
				leaderboardNames.set(4, leaderboardNames.get(3) );
				
				leaderboardNames.set(3, name );
				leaderboard.set(3, playerScore);
				
				if ( playerScore > leaderboard.get(2) )
				{
					leaderboard.set(3, leaderboard.get(2) );
					leaderboardNames.set(3, leaderboardNames.get(2) );
					
					leaderboardNames.set(2, name );
					leaderboard.set(2, playerScore);
					if ( playerScore > leaderboard.get(1) )
					{
						leaderboard.set(2, leaderboard.get(1) );
						leaderboardNames.set(2, leaderboardNames.get(1) );
						
						leaderboardNames.set(1, name );
						leaderboard.set(1, playerScore);
						if ( playerScore > leaderboard.get(0) )
						{
							leaderboard.set(1, leaderboard.get(0) );
							leaderboardNames.set(1, leaderboardNames.get(0) );
							
							leaderboardNames.set(0, name );
							leaderboard.set(0, playerScore);
						}
					}
				}
			}
			
			saveLeaderboard();
		}
		
		
	}
	
	/* saveLeaderboard
	 * 		saves current leaderboard to appropriate files
	 * 		
	 */
	private void saveLeaderboard() 
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream( "leaderboard.txt" );
		    DataOutputStream dos = new DataOutputStream( fos );
		    
		    FileOutputStream fos1 = new FileOutputStream( "leaderboardNames.txt" );
		    DataOutputStream dos1 = new DataOutputStream( fos1 );
		    
		    // Write main information
		    for( int i = 0; i < 5; i++ )
		    	dos.writeBytes( leaderboard.get(i) + "\n" );
		    
		    for( int i = 0; i < 5; i++ )
		    	dos1.writeBytes( leaderboardNames.get(i) + "\n" );
		    
		    
		    dos.close();
		    fos.close();
		    dos1.close();
		    fos1.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
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
			uniFont.drawString( WIDTH / 2, ( ( 2 + i ) * HEIGHT / 8 ), leaderboardNames.get(i) + " - " + leaderboard.get( i ));
			
		}
	}

	/* drawLevel
	 * 		Renders everything for the level
	 * 		
	 */
	private void drawLevel() 
	{
		theBackground.draw();
		drawInfoBar();
		
		drawAndUpdatePlayerAndEffects();
		
		drawAndUpdateGameShapes();
	}
	
	/* drawInfoBar
	 * 		Renders the top info during the game
	 * 		
	 */
	private void drawInfoBar()
	{
		uniFont.drawString( 32, 3, "Score: " + playerScore );
		uniFont.drawString( WIDTH / 3, 3, "Coins: " + thePlayer.getCoinTotal() );
		
		uniFont.drawString( 2 * ( WIDTH / 3), 3, "ActiveEffects: ");
		
		if ( thePlayer.hasShield() )
			uniFont.drawString( 2 * ( WIDTH / 3), 20, "Shield: 1");
		else
			uniFont.drawString( 2 * ( WIDTH / 3), 20, "Shield: 0");
		
		if ( thePlayer.hasGun() )
			uniFont.drawString( 2 * ( WIDTH / 3), 37, "Gun: 1");
		else
			uniFont.drawString( 2 * ( WIDTH / 3), 37, "Gun: 0");
		
		if ( thePlayer.hasInk() )
			uniFont.drawString( 2 * ( WIDTH / 3), 54, "Ink: 1");
		else
			uniFont.drawString( 2 * ( WIDTH / 3), 54, "Ink: 0");
		
	}
	
	
	
	/* drawPlayerAndEffects
	 * 		Renders the player and any active effects
	 * 		
	 */
	private void drawAndUpdatePlayerAndEffects()
	{
		
		thePlayer.draw();
		thePlayer.drawHitbox();
		
		if ( theShield != null )
		{
			theShield.draw();
			
		}
		
		/*
		if ( theGun != null )
		{
			theGun.draw();
		}

		if ( theInker != null )
		{
			theInker.draw();
		}
		*/
		
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
		
		thePlayer.setX( thePlayer.getX() + thePlayer.getPlayerMovementAmt() * thePlayer.getDir() );
		
		if ( thePlayer.hasShield() )
			theShield.setX( thePlayer.getX() - 3 );
		else
			theShield = null;
		
		if ( thePlayer.hasGun() )
			theGun.setX( thePlayer.getX() );
		else
			theGun = null;
		
		if ( thePlayer.hasInk() )
			theInker.setX( thePlayer.getX() - 3 );
		else
			theInker = null;
		
		previousDirection = thePlayer.getDir();
		
		
		if ( previousDirection == thePlayer.getDir() )
			thePlayer.setPlayerMovementAmt( thePlayer.getPlayerMovementAmt() * degenRate );
		else
			thePlayer.setPlayerMovementAmt( initialSpeed );
		
		if ( thePlayer.getPlayerMovementAmt() < minSpeed )
			thePlayer.setPlayerMovementAmt( minSpeed );
		
	}
	
	/* drawAndUpdateGameShapes
	 * 		Renders rocks, fire, bullets, ink, walls, and updates positions
	 * 		
	 */
	private void drawAndUpdateGameShapes()
	{
		// Manage coins and rocks
		for ( int i = 0; i < 5; i++ )
		{		
			if ( bulletObjects[i] != null )
			{
				bulletObjects[i].setY( bulletObjects[i].getY() - 4 );
				
				bulletObjects[i].draw();
				
				
				if ( bulletObjects[i].getY() < -4 )
				{
					bulletObjects[i] = null;
					bulletIndex--;
				}
			}
			
			if ( inkObjects[i] != null )
			{
				
				inkObjects[i].setY( inkObjects[i].getY() + 1 );
				
				inkObjects[i].draw();
				
				
				if ( inkObjects[i].getY() > HEIGHT + 16 )
				{
					inkObjects[i] = null;
					inkIndex--;
				}
			}
			
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
				coinObjects[i].draw();
			
			// Draw rocks
			if ( !rockObjects[i].getSeen() )
				rockObjects[i].draw();
			
			// Draw fire objects
			if ( !fireObjects[i].getSeen() )
				fireObjects[i].draw();
			
		}
		
		wallObjects[0].draw();
		wallObjects[0].drawHitbox();
		
		wallObjects[1].draw();
		wallObjects[1].drawHitbox();
		
	}
	
	
	/* setNewEffect
	 * 		creates a new effect block in the game
	 * 		
	 */
	private void setNewEffect() 
	{
		Random rng = new Random();
		
		Integer xPos = Math.abs( rng.nextInt() % (WIDTH - 80 ) );
		
		if ( thePlayer.getEffectStatus() )
		{
			int rand = Math.abs( rng.nextInt() % 3 );
			theEffect[0] = new Effect( xPos + 32, -16, 16, 16, rand, effect[rand] );
		}
			
		
	}
	
	/* setDeathShape
	 * 		Creates a new shape of rocks/fire for the game TODO: add massive here
	 * 		
	 */
	private void setDeathShape( int type, Shape [] array ) 
	{
		Random rng = new Random();
		int startX = 80;
		int inc = 96;
		
		if ( thePlayer.hasGun() && type == 1 && rng.nextInt() % 2 == 1 )
		{
			// HANDLE IT!
			rockSound.playAsSoundEffect(1, 1, false);
			array[0] = new Death(  startX , -16, 16, 16, 1, rock[0] );
			array[1] = new Death(  startX + inc, -16, 16, 16, 1, rock[0] );
			array[2] = new Death(  startX + inc * 2, -16, 16, 16, 1, rock[0] );
			array[3] = new Death(  startX + inc * 3, -16, 16, 16, 1, rock[0] );
			array[4] = new Death(  startX + inc * 4, -16, 16, 16, 1, rock[0] );
			
		}
		else if ( thePlayer.hasInk() && type == 0 && rng.nextInt() % 2 == 1 )
		{
			
			// DO YOU LIKE TO PLAY WITH FIRE!!!
			fireSound.playAsSoundEffect(1, 1, false);
			array[0] = new Death(  startX , HEIGHT + 16, 16, 16, 1, fireball );
			array[1] = new Death(  startX + inc, HEIGHT + 16, 16, 16, 1, fireball );
			array[2] = new Death(  startX + inc * 2, HEIGHT + 16, 16, 16, 1, fireball );
			array[3] = new Death(  startX + inc * 3, HEIGHT + 16, 16, 16, 1, fireball );
			array[4] = new Death(  startX + inc * 4, HEIGHT + 16, 16, 16, 1, fireball );
		}
		else
		{
			for ( int i = 0; i < 5; i++ )
			{
				Integer rand = rng.nextInt();
				Integer xPos = Math.abs( rand % ( WIDTH - 80 ) );
				
				// If we want a new random rock 
				if ( type == 1)
					array[i] = new Death(  xPos + 32, (-16) - i * (HEIGHT / 5), 16, 16, 1, rock[ Math.abs(rng.nextInt() %3)] );
				else
					array[i] = new Death(  xPos + 32, HEIGHT + 16 + i * (HEIGHT / 5), 16, 16, 0, fireball);
					
			}
		}
	}
	
	/* setCoinShape
	 * 		adds a new coin pattern shape in the game
	 * 		
	 */
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
				coinObjects[i] = new Coin( ( startingX - ( 8 * i ) ), -largeHInc * i, 8, 8, mana );
		
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
				// TODO: add transparency box around selections to move up and down for selections
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
					introSound[0].playAsSoundEffect(1, 1, false);
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
			
			if ( pressed( Keyboard.KEY_SPACE) )
			{
				if ( theGun != null && bulletIndex < 4 )
				{
					if ( bulletObjects[bulletIndex] == null )
					{
						bulletObjects[bulletIndex] = theGun.shoot( bullet );
						bulletIndex++;
					}
				}
				
				if ( theInker != null && inkIndex < 4 )
				{
					if ( inkObjects[inkIndex] == null )
					{
						inkObjects[inkIndex] = theInker.shoot( ink );
						inkIndex++;
					}
				}
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
		
		theBackground = new Background( 0, 0, WIDTH, HEIGHT, background );
		thePlayer = new Player( (WIDTH / 2) - 16, ( ( 3 * HEIGHT ) / 5 ), 32, 32, octopus );
		wallObjects[0] = new Wall( 0, 0, 32, HEIGHT, wall );
		wallObjects[1] = new Wall( WIDTH - 32, 0, 32, HEIGHT, wall );
		theEffect[0] = null;
		setCoinShape( true );
		theShield = null;
		theGun = null;
		theInker = null;
		inkIndex = 0;
		enteredName = false;
		
		bulletIndex = 0;
		bulletObjects[0] = null;
		bulletObjects[1] = null;
		bulletObjects[2] = null;
		bulletObjects[3] = null;
		bulletObjects[4] = null;
		
		inkObjects[0] = null;
		inkObjects[1] = null;
		inkObjects[2] = null;
		inkObjects[3] = null;
		inkObjects[4] = null;
				
		
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
			{
				// Iterate through each object in the current shape array
				for ( Shape thisShape : s )
				{
					boolean intersected = thePlayer.intersects( thisShape );
					
					if ( thisShape != null && intersected )
					{
						Random r = new Random();
						if ( thisShape.getShapeType() == 3 && theShield == null )
						{
							theShield = new Shield ( thePlayer.getX() - 3 , thePlayer.getY() - 3, thePlayer.getWidth() + 6, thePlayer.getHeight() + 6, shield );
							
							shieldSounds[Math.abs(r.nextInt()%3)].playAsSoundEffect(1, 1, false);
						}
						else if ( thisShape.getShapeType() == 4 && theGun == null )
						{

							gunSounds[Math.abs(r.nextInt()%2)].playAsSoundEffect(1, 1, false);
							theGun = new Gun ( thePlayer.getX() , thePlayer.getY(), thePlayer.getWidth(), thePlayer.getHeight(), gun );
							thePlayer.setTextureArray( gun );
							theInker = null;
							thePlayer.setInk( false );
						}
						else if ( thisShape.getShapeType() == 5 && theInker == null)
						{
							theInker = new Inker ( thePlayer.getX() , thePlayer.getY(), thePlayer.getWidth(), thePlayer.getHeight(), inker );
							inkSounds[0].playAsSoundEffect(1, 1, false);
							thePlayer.setTextureArray( inker );
							theGun = null;
							thePlayer.setGun( false );
						}
						thisShape = null;
							
					}
					
					// Check bullet intersections
					if ( thisShape != null && thisShape.getShapeType() == 2 )
					{
						// Used to decrement bullet index after the loop
						int counter = 0;
						for ( int j = 0; j < 5; j++ )
						{
							if ( bulletObjects[j] != null && !thisShape.getSeen() && 
									 bulletObjects[j].intersects( thisShape ) )
							{
								thisShape.setSeen( true );
								bulletObjects[j] = null;
								counter++;
							}
						}
						bulletIndex -= counter;
						
						counter = 0;
						for ( int j = 0; j < 5; j++ )
						{
							if ( inkObjects[j] != null && !thisShape.getSeen() &&
									 inkObjects[j].intersects( thisShape ) )
							{
								System.out.println("Removed object");
								thisShape.setSeen( true );
								inkObjects[j] = null;
								counter++;
							}
						}
						inkIndex -= counter;
					}
					
				}
			}
			// points based on rocks/fire destroyed?
			playerScore = thePlayer.getCoinTotal() + bonusTotal;
			
			// TODO: better condition for this
			if ( playerScore > 1 && theEffect[0] == null)
			{
				thePlayer.setEffectStatus( true );
			}
			
		}
		else if ( thePlayer != null && !thePlayer.getAlive() && state != State.LOSE )
		{
			state = State.LOSE;
			L_row = 1;
			deathSounds[0].playAsSoundEffect(1, 1, false);
		}
		
	}
	
}


