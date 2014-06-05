package entities;

import org.newdawn.slick.opengl.Texture;

public class Coin extends Shape
{
	
	boolean removeMe = false;
	private int shapeType = 1;
	public Coin ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height );
		this.myTextureArray = textureString;
		this.setColor( 0, 0, 1 );
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}

	@Override
	public void interact(Player player)
	{
		if ( !this.getRemoval() )
		{
			player.setCoinTotal( player.getCoinTotal() + 1 );
			setRemoval( true );
			//playsound
		}
	}
	
	@Override
	public int getShapeType()
	{
		return this.shapeType;
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