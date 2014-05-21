package entities;

import org.newdawn.slick.opengl.Texture;

public class Effect extends Shape
{
	
	boolean removeMe = false;
	private int effectType = -1;
	private int shapeType = 3;
	public Effect ( double x, double y, double width, double height, int type )
	{
		super( x, y, width, height );
		this.setColor( 0, 1, 1 );
		this.effectType = type;
		/* Type list
		 * 0- shield
		 * 1- ink
		 * 2- laser
		 * 3- time // if theres time ;)
		 */
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}
	
	@Override
	public int getShapeType()
	{
		
		return this.shapeType + this.effectType;
	}

	@Override
	public void interact(Player player)
	{
		if ( !this.getRemoval() )
		{
			setRemoval( true );
			player.setEffect(this.effectType);
		}
	}
	
	public boolean getRemoval()
	{
		return removeMe;
	}
	
	public void setRemoval( boolean removal )
	{
		this.removeMe = removal;
	}
}