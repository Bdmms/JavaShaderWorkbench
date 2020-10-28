import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

public class Material extends Node
{
	public static final int DEFAULT_ID = 0;
	public static final String DEFAULT = "default";
	
	private static HashMap<String, Texture> library = new HashMap<>();
	
	// Default textures
	{
		BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
		image.setRGB( 0, 0, 0xFFFFFFFF );
		library.put( DEFAULT, new Texture( DEFAULT, image ) );
	}
	
	public Material( String name ) 
	{
		super(name);
	}
	
	public Material( String name, Texture texture, int id ) 
	{
		super(name);
		add( texture, id );
	}

	@Override
	public EditorView createEditor() 
	{
		return new MaterialTable( getPath(), this );
	}
	
	@Override
	public void render( GL3 gl )
	{
		for( Node texture : children() )
		{
			((BindedTexture)texture).bind( gl );
		}
		
		super.render( gl );
	}
	
	public void add( Texture texture, int id )
	{
		children().add( new BindedTexture( texture, id ) );
	}
	
	public void removeLast()
	{
		children().remove( children().size() - 1 );
	}
	
	public BindedTexture get( int index )
	{
		return (BindedTexture)children().get( index );
	}
	
	public static void setTexture( String name, Texture texture )
	{
		library.put( name, texture );
	}
	
	public static Texture getTexture( String key )
	{
		return library.get( key );
	}
	
	public static Texture loadTexture( File file )
	{
		Texture texture = library.get( file.getName() );
		
		if( texture == null )
		{
			texture = new Texture( file );
			library.put( file.getName(), texture );
		}
		
		return texture;
	}
	
	public static void uploadAllTextures( GL3 gl )
	{
		for( Texture texture : library.values() )
		{
			texture.load( gl );
		}
	}
	
	public static HashMap<String, Material> loadMtlFile( File file ) throws IOException
	{
		if( !file.exists() ) return null;
		
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		Material material = null;
		
		HashMap<String, Material> materials = new HashMap<>();
		String dir = file.getAbsolutePath().substring( 0, file.getAbsolutePath().lastIndexOf( '\\' ) ) + "\\";
		
		Iterator<String> iterator = reader.lines().iterator();
		while( iterator.hasNext() )
		{
			String line = iterator.next();
			String[] parts = line.split( "\\s*( |\t)\\s*" );
			
			switch( parts[0] )
			{
			case "newmtl":
				material = new Material( parts[1] );
				materials.put( parts[1], material );
				break;
				
			case "map_Kd":
				material.add( Material.loadTexture( new File( dir + parts[1] ) ), GL.GL_TEXTURE0 );
				break;
			}
		}
		
		reader.close();
		return materials;
	}
	
	public static class BindedTexture extends Node
	{
		public Texture texture;
		public int id;
		
		public BindedTexture( Texture texture, int id )
		{
			super( texture == null ? "" : texture.filename );
			this.texture = texture;
			this.id = id;
		}
		
		public void bind( GL3 gl )
		{
			if( id >= GL3.GL_TEXTURE0 && id <= GL3.GL_TEXTURE31 )
			{
				gl.glActiveTexture( id );
				gl.glBindTexture( GL3.GL_TEXTURE_2D, texture.textureID[0] );
			}
		}
		
		public String toString() 
		{
			if( id >= GL3.GL_TEXTURE0 && id <= GL3.GL_TEXTURE31 )
				return "Texture #" + (id - GL3.GL_TEXTURE0);
			else
				return "(unassigned)";
		}

		@Override
		public EditorView createEditor() 
		{
			return new TextureEditor( getPath(), texture );
		}
	}
}
