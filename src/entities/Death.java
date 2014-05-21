package entities;

import org.newdawn.slick.opengl.Texture;

public class Death extends Shape
{
	
	boolean hit = false;
	private int shapeType = 2;
	private boolean alreadySeen = false;
	public Death ( double x, double y, double width, double height )
	{
		super( x, y, width, height );
		this.setColor( 1, 0, 0 );
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}
	
	@Override
	public int getShapeType()
	{
		return this.shapeType;
	}

	@Override
	public void interact(Player player)
	{
		if ( player.getEffect() == 0 || this.alreadySeen )
		{
			System.out.println("Shield LOST");
			player.setEffect( -1 ); // Lose Shield
			this.alreadySeen = true;
		}
		else
		{
			
			player.setAlive( false );
		}
	}

}
