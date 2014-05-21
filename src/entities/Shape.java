package entities;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2d;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public abstract class Shape
{
	
	private double dx, dy, x, y, width, height;
	private double r = 1, g = 1, b = 1;
	
	private int timer, textureDuration;
	
	private String textureString;
	
	private Texture pic;					// for drawing in the level editor
	
	private Texture [] myTextureArray;

	public Shape ( double x, double y, double width, double height )
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		//this.myTextureArray = textureString;
	}

	public abstract void interact( Player player );

	public int draw()
	{
		textureStart();
		/*
		// If we are beyond the bounds of the animation array, loop TODO: to keep the last image we have to keep adding and subtracting 1, this blows
		if ( ( timer / textureDuration ) >= myTextureArray.length  )
		{
			timer = 0;
		}
		
		setPic ( myTextureArray[timer / textureDuration] );
		
		if ( ( timer / textureDuration ) < myTextureArray.length )
			this.timer++;
		*/
		textureVertices();
		
		//return ( timer / textureDuration );
		return 0;
		
	}

	public abstract boolean intersects( Shape other );
	
	public abstract int getShapeType( );
	
	public void setTextureString( String textString )
	{
		this.textureString = textString;
	}
	
	public void setPic( Texture tex )
	{
		pic = tex;
	}

	public void setTextureArray( Texture[] newArray )
	{
		this.myTextureArray = newArray;
	}

	public void textureStart()
	{
		glEnable( GL_BLEND );
		glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP );//this magically stops 
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,GL11.GL_CLAMP );	//pixel bleeding, gg opengl
		
		glColor4d( this.r, this.g, this.b, 1 );
		
		GL11.glEnable( GL11.GL_TEXTURE_2D );
	}

	public void textureVertices()
	{
		
		//pic.bind();
		glBegin( GL_QUADS );
		
		glTexCoord2f( 0, 0 );
		glVertex2d( this.x, this.y );
		glTexCoord2f( 1, 0 );
		glVertex2d( this.x + this.width, this.y );
		glTexCoord2f( 1, 1 );
		glVertex2d( this.x + this.width, this.y + this.height );
		glTexCoord2f( 0, 1 );
		glVertex2d( this.x, this.y + this.height );
		glEnd();
		
		GL11.glDisable( GL11.GL_TEXTURE_2D );
	}

	public void drawHitbox()
	{
		glBegin( GL_LINE_LOOP );
		glVertex2d( x, y );
		glVertex2d( x + width, y );
		glVertex2d( x + width, y + height );
		glVertex2d( x, y + height );
		glEnd();
	}

	public boolean contains( Shape other )
	{
		return ( (this.getX() < other.getX() )
				&& ( (this.getX() + this.getWidth()) > (other.getX() + other
						.getWidth())) && this.getY() < other.getY() && (this
				.getY() + this.getHeight()) > (other.getY() + other.getHeight()));
	}

	// ---- From AbstractMoveableEntity

	public void update( int delta )
	{
		this.x += delta * dx;
		this.y += delta * dy;
	}

	public double getHeight()
	{
		return height;
	}

	public double getWidth()
	{
		return width;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public void setHeight( double height )
	{
		this.height = height;
	}

	public void setWidth( double width )
	{
		this.width = width;
	}

	public void setX( double x )
	{
		this.x = x;
	}

	public void setY( double y )
	{
		this.y = y;
	}

	public void setDX( double dx )
	{
		this.dx = dx;
	}

	public void setDY( double dy )
	{
		this.dy = dy;
	}

	public void setPosition( double x, double y )
	{
		this.x = x;
		this.y = y;
	}
	
	public void setColor( double rIn, double gIn, double bIn )
	{
		this.r = rIn;
		this.g = gIn;
		this.b = bIn;
	}

	
	
	//-- End AbstractMoveableEntity

}
