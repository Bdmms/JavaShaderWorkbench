package swb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class CubeMap extends ITexture
{
	public static final int CUBE_MAP_FACES = 6;
	
	private BufferedImage[] _images = null;
	private ImageIcon _icon;
	
	/**
		Loads cube map images from right, left, top, bottom, front, back
	 * @param name
	 * @param filepaths
	 */
	public CubeMap( String name, String[] filepaths )
	{
		super( name, GL3.GL_TEXTURE_CUBE_MAP, 1 );
		
		_images = new BufferedImage[filepaths.length];
		
		for( int i = 0; i < filepaths.length; i++ )
		{
			try 
			{ 
				_images[i] = ImageIO.read( new File( filepaths[i] ) ); 
			} 
			catch (IOException e) { System.err.println( filepaths[i] ); }
		}
		
		_icon = new ImageIcon( _images[0] );
	}
	
	@Override
	public void load( GL3 gl )
	{
		if( loaded ) return;
		
		System.out.println( "Uploading: " + name );
		gl.glGenTextures( 1, textureID, 0 );
	    gl.glBindTexture( GL3.GL_TEXTURE_CUBE_MAP, textureID[0] );
	    
	    for ( int i = 0; i < CUBE_MAP_FACES; i++ )
	    {
	    	TextureData texture = AWTTextureIO.newTextureData( gl.getGLProfile(), _images[i], false );
	        gl.glTexImage2D( GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, texture.getInternalFormat(), texture.getWidth(), texture.getHeight(), 0, 
					texture.getPixelFormat(), texture.getPixelType(), texture.getBuffer() );
	    }
	    
	    gl.glGenerateMipmap( GL3.GL_TEXTURE_CUBE_MAP );
	    gl.glTexParameteri( GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR );
	    gl.glTexParameteri( GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR );
	    gl.glTexParameteri( GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE );
	    gl.glTexParameteri( GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE );
	    gl.glTexParameteri( GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE );
	    
	    loaded = true;
	}
	
	@Override
	public ImageIcon getIcon()
	{
		return _icon;
	}
	
	/**
	 * Returns a cube map with the given name; will read file if the cube map has not been loaded
	 */
	public static ITexture loadCubeMap( String name, String ext )
	{
		ITexture texture = library.get( name );
		
		if( texture == null )
		{
			String[] paths = new String[CUBE_MAP_FACES];
			for( int i = 0; i < CUBE_MAP_FACES; i++ )
			{
				paths[i] = String.format( "%s%d%s", name, i, ext );
			}
			
			texture = new CubeMap( name, paths );
			library.put( name, texture );
		}
		
		return texture;
	}
	
	public static GLNode generateSkybox( String name, String ext )
	{
		UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "skyboxMap", GLDataType.SAMP2D, "8" );
		
		GLNode node = new GLNode( "Skybox" );
		node.add( ShaderProgram.generateProgram( "cubemap" ) );
		node.add( uniforms );
		node.add( new Material( "Cube Material", loadCubeMap( name, ext ), GL3.GL_TEXTURE8 ) );
		node.add( new CubeMapVertexBuffer( CUBE_VERTICES ) );
		return node;
	}
	
	private static class CubeMapVertexBuffer extends VertexBuffer
	{
		public CubeMapVertexBuffer( float[] buffer ) 
		{
			super( buffer, 3 );
		}
		
		@Override
		public void render( GL3 gl )
		{
			gl.glDepthMask( false );
			gl.glBindVertexArray( vao[0] );
			gl.glDrawArrays( GL3.GL_TRIANGLES, 0, _size );
			gl.glDepthMask( true );
		}
	}
	
	private static float CUBE_VERTICES[] = 
	{
	    -1.0f,  1.0f, -1.0f,
	    -1.0f, -1.0f, -1.0f,
	     1.0f, -1.0f, -1.0f,
	     1.0f, -1.0f, -1.0f,
	     1.0f,  1.0f, -1.0f,
	    -1.0f,  1.0f, -1.0f,

	    -1.0f, -1.0f,  1.0f,
	    -1.0f, -1.0f, -1.0f,
	    -1.0f,  1.0f, -1.0f,
	    -1.0f,  1.0f, -1.0f,
	    -1.0f,  1.0f,  1.0f,
	    -1.0f, -1.0f,  1.0f,

	     1.0f, -1.0f, -1.0f,
	     1.0f, -1.0f,  1.0f,
	     1.0f,  1.0f,  1.0f,
	     1.0f,  1.0f,  1.0f,
	     1.0f,  1.0f, -1.0f,
	     1.0f, -1.0f, -1.0f,

	    -1.0f, -1.0f,  1.0f,
	    -1.0f,  1.0f,  1.0f,
	     1.0f,  1.0f,  1.0f,
	     1.0f,  1.0f,  1.0f,
	     1.0f, -1.0f,  1.0f,
	    -1.0f, -1.0f,  1.0f,

	    -1.0f,  1.0f, -1.0f,
	     1.0f,  1.0f, -1.0f,
	     1.0f,  1.0f,  1.0f,
	     1.0f,  1.0f,  1.0f,
	    -1.0f,  1.0f,  1.0f,
	    -1.0f,  1.0f, -1.0f,

	    -1.0f, -1.0f, -1.0f,
	    -1.0f, -1.0f,  1.0f,
	     1.0f, -1.0f, -1.0f,
	     1.0f, -1.0f, -1.0f,
	    -1.0f, -1.0f,  1.0f,
	     1.0f, -1.0f,  1.0f
	};
}
