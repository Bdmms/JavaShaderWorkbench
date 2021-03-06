package swb;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import swb.editors.EditorView;
import swb.editors.MaterialTable;
import swb.editors.TextureEditor;

public class Material extends GLNode
{
	public static final int DEFAULT_ID = 0;
	public static final String DEFAULT_DIF = "default_dif";
	public static final String DEFAULT_NRM = "default_nrm";
	
	/* All loaded textures */
	private static HashMap<String, ITexture> library = new HashMap<>();
	
	// Default textures
	{
		BufferedImage image0 = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
		image0.setRGB( 0, 0, 0xFFFFFFFF );
		library.put( DEFAULT_DIF, new Texture( DEFAULT_DIF, image0 ) );
		
		BufferedImage image1 = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
		image1.setRGB( 0, 0, 0xFF0000FF );
		library.put( DEFAULT_NRM, new Texture( DEFAULT_NRM, image1 ) );
	}
	
	public Material( String name ) 
	{
		super(name);
	}
	
	public Material( String name, ITexture texture, int id ) 
	{
		super(name);
		addTexture( texture, id );
	}

	@Override
	public EditorView createEditor() 
	{
		return new MaterialTable( getPath(), this );
	}
	
	public void addTexture( ITexture texture, int id )
	{
		children().add( new BindedTexture( texture, id ) );
	}
	
	public BindedTexture get( int index )
	{
		return (BindedTexture)children().get( index );
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
				material.addTexture( ITexture.loadTexture( new File( dir + parts[1] ) ), GL.GL_TEXTURE0 );
				break;
			}
		}
		
		reader.close();
		return materials;
	}
	
	public static class BindedTexture extends GLNode
	{
		public ITexture texture;
		public int id;
		
		public BindedTexture( ITexture texture, int id )
		{
			super( texture == null ? "" : texture.name, ID_TEXTURE, true );
			this.texture = texture;
			this.id = id;
		}
		
		@Override
		public void bind( GL3 gl )
		{
			if( id >= GL3.GL_TEXTURE0 && id <= GL3.GL_TEXTURE31 )
			{
				gl.glActiveTexture( id );
				gl.glBindTexture( texture.type, texture.textureID[0] );
			}
		}
		
		@Override
		public void render( GL3 gl )
		{
			if( id >= GL3.GL_TEXTURE0 && id <= GL3.GL_TEXTURE31 )
			{
				gl.glActiveTexture( id );
				gl.glBindTexture( texture.type, texture.textureID[0] );
			}
		}
		
		@Override
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
