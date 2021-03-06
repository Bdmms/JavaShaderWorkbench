package swb;

import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.jogamp.opengl.GL3;

public abstract class ITexture 
{
	public static final int DEFAULT_ID = 0;
	public static final String DEFAULT_DIF = "default_dif";
	public static final String DEFAULT_NRM = "default_nrm";
	
	/* All loaded textures */
	protected static HashMap<String, ITexture> library = new HashMap<>();
	
	public final String name;
	public final int[] textureID;
	public final int type;
	
	protected boolean loaded = false;
	
	public ITexture( String name, int type, int numTextures )
	{
		this.name = name;
		this.type = type;
		this.textureID = new int[numTextures];
	}
	
	/**
	 * Deletes the texture
	 * @param gl - {@link GL3} instance
	 */
	public void delete( GL3 gl )
	{
		if( !loaded ) return;
		
		gl.glDeleteTextures( 1, textureID, 0 );
		loaded = false;
	}
	
	/**
	 * Gets the icon for this texture
	 * @return {@link ImageIcon}
	 */
	public abstract ImageIcon getIcon();
	
	/**
	 * Loads the texture to the GPU
	 * @param gl - {@link GL3} instance
	 */
	public abstract void load( GL3 gl );
	
	/**
	 * Removes all loaded textures
	 */
	public static void deleteTextures( GL3 gl )
	{
		for( ITexture texture : library.values() )
		{
			texture.delete( gl );
		}
		
		library.clear();
	}
	
	/**
	 * Removes a loaded texture and deletes it from the GPU
	 */
	public static void deleteTexture( GL3 gl, String name )
	{
		ITexture texture = library.get( name );
		if( texture != null )
		{
			texture.delete( gl );
			library.remove( name );
		}
	}
	
	/**
	 * Deletes the existing texture with the same name and replaces it
	 */
	public static void replaceTexture( GL3 gl, String name, ITexture texture )
	{
		deleteTexture( gl, name );
		library.put( name, texture );
	}
	
	/**
	 * Returns a texture with the given name; will read file if the texture has not been loaded
	 */
	public static ITexture loadTexture( String name )
	{
		ITexture texture = library.get( name );
		
		if( texture == null )
		{
			texture = new Texture( new File( name ) );
			library.put( name, texture );
		}
		
		return texture;
	}
	
	/**
	 * Returns a texture with the given name; will read file if the texture has not been loaded
	 */
	public static ITexture loadTexture( File file )
	{
		ITexture texture = library.get( file.getName() );
		
		if( texture == null )
		{
			texture = new Texture( file );
			library.put( file.getName(), texture );
		}
		
		return texture;
	}
	
	/**
	 * Send all unloaded textures to the GPU.
	 */
	public static void uploadTextures( GL3 gl )
	{
		for( ITexture texture : library.values() )
		{
			texture.load( gl );
		}
	}
}
