package entities;

import org.newdawn.slick.opengl.Texture;

public class Gun extends Shape
{
	boolean removeMe = false;
	private int shapeType = 10;
	public Gun ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height );
		this.myTextureArray = textureString;
		this.setColor( 1, 1, 1 );
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
	
	public Bullet shoot( Texture [] textureString)
	{
		
		Bullet b = new Bullet( this.getX() + (this.getWidth() / 2) - 2, this.getY(), 4, 4, textureString );
		
		return b;
		
	}
	
}
