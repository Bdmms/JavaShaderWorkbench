import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

public class Model extends AbstractNode
{
	public Model( String name )
	{
		super( name );
	}
	
	@Override
	public EditorView createEditor()
	{
		return null;
	}
	
	private static class Vector3D
	{
		public float x;
		public float y;
		public float z;
		
		public Vector3D( String[] elements )
		{
			x = Float.parseFloat( elements[1] );
			y = Float.parseFloat( elements[2] );
			
			if( elements.length > 3 )
				z = Float.parseFloat( elements[3] );
			else
				z = 0.0f;
		}
	}
	
	public static Model readObjFile( File file, EditorTabs editor ) throws IOException
	{
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		
		List<Vector3D> position = new ArrayList<>();
		List<Vector3D> normals = new ArrayList<>();
		List<Vector3D> textCoord = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		Model model = new Model( file.getName() );
		Mesh mesh = null;
		
		ShaderProgram program = new ShaderProgram( "Shader" );
		Shader vertex = new Shader( "shader.vs", fileToString( new File( "shaders\\template.vs" ) ), GL3.GL_VERTEX_SHADER );
		Shader fragment = new Shader( "shader.fs", fileToString( new File( "shaders\\template.fs" ) ), GL3.GL_FRAGMENT_SHADER );
		program.add( vertex );
		program.add( fragment );
		model.add( program );
		
		UniformList uniforms = new UniformList( "Model Uniforms" );
		uniforms.add( "diffuse", GLDataType.SAMP2D, "0" );
		model.add( uniforms );
		
		Iterator<String> iterator = reader.lines().iterator();
		while( iterator.hasNext() )
		{
			String line = iterator.next();
			String[] parts = line.split( "\\s*( |\t)\\s*" );
			
			switch( parts[0] )
			{
			case "g":
				if( parts.length > 1 )
				{
					mesh = new Mesh( line.substring( line.indexOf( ' ' ) + 1 ) );
					model.add( mesh );
				}
				else if( mesh != null )
				{
					mesh.add( new ElementBuffer( indices.stream().mapToInt( i -> i ).toArray() ) );
					indices.clear();
				}
				break;
			
			case "v":
				position.add( new Vector3D( parts ) );
				break;
				
			case "vn": 
				normals.add( new Vector3D( parts ) );
				break;
				
			case "vt":
				textCoord.add( new Vector3D( parts ) );
				break;
				
			case "f": 
				indices.add( Integer.parseInt( parts[1].substring( 0, parts[1].indexOf( '/' ) ) ) - 1 );
				indices.add( Integer.parseInt( parts[2].substring( 0, parts[2].indexOf( '/' ) ) ) - 1 );
				indices.add( Integer.parseInt( parts[3].substring( 0, parts[3].indexOf( '/' ) ) ) - 1 );
				break;
				
			case "usemtl":
				String filename = line.substring( line.lastIndexOf( '\\' ) + 1 );
				String name = filename.substring( 0, filename.lastIndexOf( '.' ) );
				Texture texture = Material.loadTexture( new File( "assets\\" + filename ) );
				mesh.add( new Material( name, texture, GL.GL_TEXTURE0 ) );
			default: break;
			}
		}
		
		float[] buffer = new float[position.size() * 8];
		int idx = 0;
		for( int i = 0; i < position.size(); i++ )
		{
			Vector3D pos = position.get( i );
			Vector3D nrm = normals.get( i );
			Vector3D txt = textCoord.get( i );
			buffer[idx++] = pos.x;
			buffer[idx++] = pos.y;
			buffer[idx++] = pos.z;
			buffer[idx++] = nrm.x;
			buffer[idx++] = nrm.y;
			buffer[idx++] = nrm.z;
			buffer[idx++] = txt.x;
			buffer[idx++] = txt.y;
		}
		
		model.add( new VertexBuffer( buffer, 8 ) );
		model.update();
		
		reader.close();
		
		return model;
	}
	
	public static String fileToString( File file )
	{
		try 
		{
			byte[] data = new byte[(int)file.length()];
			FileInputStream stream = new FileInputStream( file );
			stream.read( data );
			stream.close();
			return new String( data );
		} 
		catch (IOException e) 
		{
			return "";
		}
	}
}
