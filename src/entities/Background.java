package entities;

import org.newdawn.slick.opengl.Texture;

public class Background extends Shape
{
	
	private int shapeType = 9;
	public Background ( double x, double y, double width, double height, Texture[] textureString )
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
		return;
	}

}