package entities;

import org.newdawn.slick.opengl.Texture;

public class Player extends Shape
{
	public int goldCount = 0;
	public boolean alive = true, win = false;
	public double startX, startY;
	public int lastDIR = 1;
	public double movementSpeed = 4;
	private boolean readyForEffect = false;
	private int currEffect = -1;
	private int shapeType = 7;

	public Shape groundPiece;

	public Player ( double x, double y, double width, double height )
	{
		super( x, y, width, height );
		this.setColor( 0, 1, 0 );
	}

	public boolean intersects( Shape other )
	{
		
		boolean intersect = false;
		if ( other != null )
		{
			double p1y = this.getY();
			double p1x = this.getX();
			double p2x = this.getX() + this.getWidth();
			double p2y = this.getY() + this.getHeight();
			double p3y = other.getY();
			double p3x = other.getX();
			double p4x = other.getX() + other.getWidth();
			double p4y = other.getY() + other.getHeight();
			
			if ( ! ( p2y < p3y || p1y > p4y || p2x < p3x || p1x > p4x ) )
			{
				intersect = true;
				other.interact( this );
			} 
		}
		else
		{
			intersect = false;
		}

		return intersect;
	}

	public boolean inMiddle ( Shape other )
	{
		
		return( ( this.getX() > other.getX() ) && 
				( this.getX() + this.getWidth() < other.getX() + other.getWidth() ) &&  
				( this.getY() <= other.getY() + other.getHeight() ) );
		
	}
	
	/* getDirection
	 * 		Allows us a nice way to set the current texture's direction, less if's
	 */
	public String getDirection()
	{
		if ( this.lastDIR == 1 )
			return "Right";
		else
			return "Left";
	}
	
	@Override
	public int getShapeType()
	{
		return this.shapeType;
	}
	
	/* deathAnimation
	 * 		Allows for a nice way for the player to die in a nice way
	 */
	public void deathAnimation()
	{
		//	TODO: tweak this 
		//double distance = -( .02 * this.timer * this.timer ) + ( .7 * this.timer) - 2.25;
		//this.setY( this.getY() - distance );
		
	}
	
	@Override
	public void interact(Player player)
	{
		// nothing
	}
	
	public void setDir( int dir )
	{
		this.lastDIR = dir;
	}
	
	public void setAlive( boolean life )
	{
		this.alive = life;
	}
	
	public boolean getAlive()
	{
		return( this.alive );
	}
	
	public int getDir()
	{
		return( this.lastDIR );
	}
	
	public void setPlayerMovementAmt( double move )
	{
		this.movementSpeed = move;
	}

	public double getPlayerMovementAmt()
	{
		return( this.movementSpeed );
	}
	
	public void setCoinTotal( int total )
	{
		this.goldCount = total;
	}

	public int getCoinTotal()
	{
		return( this.goldCount );
	}
	
	public void setEffectStatus( boolean status)
	{
		this.readyForEffect = status;
	}
	
	public boolean getEffectStatus()
	{
		return( this.readyForEffect );
	}
	
	public void setEffect( int type )
	{
		this.currEffect = type;
		
	}
	
	public int getEffect()
	{
		return this.currEffect;
	}
	
	
}
