package entities;

import org.newdawn.slick.opengl.Texture;

public class Death extends Shape
{
	
	boolean hit = false;
	private int shapeType = 2;

	
	public Death ( double x, double y, double width, double height, int type, Texture [] textureString )
	{
		super( x, y, width, height );
		this.setColor( 1, 1, 1 );
		this.myTextureArray = textureString;
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
		if ( player.hasShield() && !this.getSeen() )
		{
			player.setShield( false ); // Lose Shield
			this.setSeen( true );
		}
		else if ( !this.getSeen())
		{
			
			player.setAlive( false );
		}
	}

}
