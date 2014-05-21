package entities;

public class Wall extends Shape
{
	
	private int shapeType = 9;
	public Wall ( double x, double y, double width, double height )
	{
		super( x, y, width, height );
		this.setColor( 1, 1, 0 );
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
		player.setPlayerMovementAmt( 1 );
		player.setDir( player.getDir() * -1 );
		player.setX( player.getX() + 10 * player.getDir() );
	}

}