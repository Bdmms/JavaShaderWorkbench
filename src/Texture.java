import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Texture
{
	public final String filename;
	private BufferedImage _image = null;
	private TextureData _texture = null;
	private ImageIcon _icon;
	
	public int[] textureID = new int[1];
	private boolean loaded = false;
	
	public Texture( File file )
	{
		try { _image = ImageIO.read( file ); } catch (IOException e) { System.err.println( file.getAbsolutePath() ); }
		
		filename = file.getName();
		_icon = new ImageIcon( _image );
	}
	
	public Texture( String file )
	{
		this( new File( file ) );
	}
	
	public void load( GL3 gl )
	{
		if( loaded ) return;
		
		_texture = AWTTextureIO.newTextureData( gl.getGLProfile(), _image, true );
		
		gl.glGenTextures( 1, textureID, 0);
		gl.glBindTexture( GL3.GL_TEXTURE_2D, textureID[0] );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT );
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR );
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR );
		
		gl.glTexImage2D( GL3.GL_TEXTURE_2D, 0, _texture.getInternalFormat(), _texture.getWidth(), _texture.getHeight(), 0, 
				_texture.getPixelFormat(), _texture.getPixelType(), _texture.getBuffer() );
		gl.glGenerateMipmap( GL3.GL_TEXTURE_2D );
		
		loaded = true;
	}
	
	public void delete( GL3 gl )
	{
		if( !loaded ) return;
		
		gl.glDeleteTextures( 1, textureID, 0 );
		loaded = false;
	}
	
	public ImageIcon getIcon()
	{
		return _icon;
	}
	
	public BufferedImage getImage()
	{
		return _image;
	}
}
