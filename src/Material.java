import java.io.File;
import java.util.HashMap;

import com.jogamp.opengl.GL3;

public class Material extends AbstractNode
{
	public static final int DEFAULT_ID = 0;
	private static HashMap<String, Texture> library = new HashMap<>();
	
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
	
	public static class BindedTexture extends AbstractNode
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
