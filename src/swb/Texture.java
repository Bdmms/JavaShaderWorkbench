package swb;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Texture extends ITexture
{
	public static boolean FLAG_VERTICAL_FLIP = false;
	
	// Default textures
	static {
		BufferedImage image0 = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
		image0.setRGB( 0, 0, 0xFFFFFFFF );
		library.put( DEFAULT_DIF, new Texture( DEFAULT_DIF, image0 ) );
		
		BufferedImage image1 = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
		image1.setRGB( 0, 0, 0xFF0000FF );
		library.put( DEFAULT_NRM, new Texture( DEFAULT_NRM, image1 ) );
	}
	
	private BufferedImage _image = null;
	private ImageIcon _icon;
	
	public Texture( File file )
	{
		super( file.getName(), GL3.GL_TEXTURE_2D, 1 );
		
		try { _image = ImageIO.read( file ); } catch (IOException e) { System.err.println( file.getAbsolutePath() ); }
		
		// TODO: Support texture flipping
		if( FLAG_VERTICAL_FLIP )
		{
			Graphics g = _image.getGraphics();
			
			g.drawImage( 
					_image, 0, 0, _image.getWidth(), _image.getHeight(), 
					0, _image.getHeight() - 1, _image.getWidth(), 0, null
			);
			g.dispose();
		}
		
		_icon = new ImageIcon( _image );
	}
	
	public Texture( String file )
	{
		this( new File( file ) );
	}
	
	public Texture( String name, BufferedImage image )
	{
		super( name, GL3.GL_TEXTURE_2D, 1 );
		_image = image;
		_icon = new ImageIcon( _image );
	}
	
	@Override
	public void load( GL3 gl )
	{
		if( loaded ) return;
		
		System.out.println( "Uploading: " + name );
		TextureData texture = AWTTextureIO.newTextureData( gl.getGLProfile(), _image, true );
		
		gl.glGenTextures( 1, textureID, 0);
		gl.glBindTexture( GL3.GL_TEXTURE_2D, textureID[0] );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT );
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR );
		gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR );
		
		gl.glTexImage2D( GL3.GL_TEXTURE_2D, 0, texture.getInternalFormat(), texture.getWidth(), texture.getHeight(), 0, 
				texture.getPixelFormat(), texture.getPixelType(), texture.getBuffer() );
		gl.glGenerateMipmap( GL3.GL_TEXTURE_2D );
		
		loaded = true;
	}
	
	public BufferedImage getImage()
	{
		return _image;
	}
	
	@Override
	public ImageIcon getIcon()
	{
		return _icon;
	}
}
