package entities;

import org.newdawn.slick.opengl.Texture;

public class Inker extends Shape
{
	boolean removeMe = false;
	private int effectType = -1;
	private int shapeType = 12;
	public Inker ( double x, double y, double width, double height )
	{
		super( x, y, width, height );
		this.setColor( 1, .5, .5 );
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
		/*if ( !this.getRemoval() )
		{
			player.goldCount++;
			setRemoval( true );
			player.setEffect(this.effectType);
			System.out.println("effect:" + player.getEffect() );
		}
		*/
	}
	
	public boolean getRemoval()
	{
		return removeMe;
	}
	
	public void setRemoval( boolean removal )
	{
		this.removeMe = removal;
	}
	
	public Ink shoot()
	{
		
		Ink i = new Ink( this.getX() + (this.getWidth() / 2) - 8 , this.getY(), 16, 16 );
		System.out.println("created ink");
		return i;
		
	}
}
	