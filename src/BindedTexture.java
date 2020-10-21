import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class BindedTexture 
{
	public static final TextureID DEFAULT_ID = new TextureID( 0 );
	
	public final String filename;
	private BufferedImage _image = null;
	private TextureData _texture = null;
	private ImageIcon _icon;
	private TextureID _binded;
	
	private int[] _textureID = new int[1];
	private boolean loaded = false;
	
	public BindedTexture( File file )
	{
		try { _image = ImageIO.read( file ); } catch (IOException e) { e.printStackTrace(); }
		
		filename = file.getName();
		_icon = new ImageIcon( _image );
		_binded = DEFAULT_ID;
	}
	
	public BindedTexture( String file )
	{
		this( new File( file ) );
	}
	
	public void load( GL3 gl )
	{
		_texture =  AWTTextureIO.newTextureData( gl.getGLProfile(), _image, true );
		
		gl.glGenTextures( 1, _textureID, 0);
		gl.glBindTexture( GL3.GL_TEXTURE_2D, _textureID[0] );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT );
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR );
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR );
		
		gl.glTexImage2D( GL3.GL_TEXTURE_2D, 0, _texture.getInternalFormat(), _texture.getWidth(), _texture.getHeight(), 0, 
				_texture.getPixelFormat(), _texture.getPixelType(), _texture.getBuffer() );
		gl.glGenerateMipmap( GL3.GL_TEXTURE_2D );
		
		loaded = true;
	}
	
	public void bind( GL3 gl )
	{
		if( !loaded )
			load( gl );
		
		if( _binded.id >= GL3.GL_TEXTURE0 && _binded.id <= GL3.GL_TEXTURE31 )
		{
			gl.glActiveTexture( _binded.id );
			gl.glBindTexture( GL3.GL_TEXTURE_2D, _textureID[0] );
		}
	}
	
	public void dispose( GL3 gl )
	{
		gl.glDeleteTextures( 1, _textureID, 0 );
	}
	
	public ImageIcon getIcon()
	{
		return _icon;
	}
	
	public void setBinding( TextureID id )
	{
		_binded = id;
	}
	
	public TextureID getBindingID()
	{
		return _binded;
	}
	
	public static class TextureID
	{
		public int id;
		
		public TextureID( int i )
		{
			id = i;
		}
		
		public String toString() 
		{
			if( id >= GL3.GL_TEXTURE0 && id <= GL3.GL_TEXTURE31 )
				return "Texture #" + (id - GL3.GL_TEXTURE0);
			else
				return "(unassigned)";
		}
	}
}
