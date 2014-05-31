package entities;

import org.newdawn.slick.opengl.Texture;

public class Ink extends Shape
{
	boolean removeMe = false;
	private int effectType = -1;
	private int shapeType = 13;
	public Ink ( double x, double y, double width, double height )
	{
		super( x, y, width, height );
		this.setColor( 1, 1, 1 );
	}

	@Override
	public boolean intersects(Shape other)
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
				//other.interact( this );
			} 
		}
		else
		{
			intersect = false;
		}

		return intersect;
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
}