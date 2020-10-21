import java.util.ArrayList;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

public class VertexBuffer extends ArrayList<Vertex>
{
	private static final long serialVersionUID = 1L;
	
	private int _stride = 0;
	private int[] _indices = null;
	
	public int[] ebo = new int[1]; // Element buffer
	public int[] vbo = new int[1]; // Vertex buffer
	public int[] vao = new int[1]; // Vertex array
	
	public VertexBuffer() 
	{ 
		super();
	}
	
	public VertexBuffer( int stride ) 
	{ 
		super();
		_stride = stride;
	}
	
	public VertexBuffer( int size, int stride ) 
	{ 
		super( size );
		for( int v = 0; v < size; v++ )
			add( new Vertex( new float[stride] ) );
		_stride = stride;
	}
	
	public VertexBuffer( Vertex ... vertices )
	{
		super( vertices.length );
		_stride = vertices[0].getDimension();
		for( Vertex vertex : vertices )
		{
			add( vertex );
		}
	}
	
	public void initialize( GL3 gl )
	{
		gl.glGenVertexArrays( 1, vao, 0 );
		gl.glGenBuffers( 1, vbo, 0 );
		gl.glGenBuffers( 1, ebo, 0 );
		
		gl.glBindVertexArray( vao[0] );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
		gl.glBufferData( GL3.GL_ARRAY_BUFFER, bytes(), Buffers.newDirectFloatBuffer( toBuffer() ), GL3.GL_STATIC_DRAW );
	
		if( isIndexed() )
		{
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, _indices.length * Integer.BYTES, Buffers.newDirectIntBuffer( _indices ), GL3.GL_STATIC_DRAW );
		}
	}
	
	public void dispose( GL3 gl )
	{
		if( isIndexed() ) gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( ebo ) );
		gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( vbo ) );
		gl.glDeleteVertexArrays( 1, Buffers.newDirectIntBuffer( vao ) );
	}
	
	public void set( float ... data )
	{
		int i = 0;
		for( int v = 0; v < size() && i < data.length; v++ )
		{
			float[] vertex = get( v ).getData();
			
			for( int e = 0; e < vertex.length && i < data.length; e++ )
				vertex[e] = data[i++];
		}
	}
	
	public void setIndices( int[] indices )
	{
		_indices = indices;
	}
	
	public void set( int i, float value )
	{
		get( i / _stride ).getData()[ i % _stride ] = value;
	}
	
	public float[] toBuffer()
	{
		float[] arr = new float[ size() * _stride ];
		
		int i = 0;
		for( Vertex vertex : this )
			for( float f : vertex.getData() )
				arr[i++] = f;
		
		return arr;
	}
	
	public boolean isIndexed()
	{
		return _indices != null;
	}
	
	public int[] indices() 
	{
		return _indices;
	}
	
	public int stride() 
	{
		return _stride;
	}
	
	public int bytes()
	{
		return size() * _stride * Float.BYTES;
	}
	
	public void print() 
	{
		System.out.println( size() + " vertices (" + stride() + ")" );
		for( int i = 0; i < size(); i++ )
		{
			float[] data = get( i ).getData();
			String[] parts = new String[ data.length ];
			
			for( int e = 0; e < data.length; e++ )
				parts[e] = String.valueOf( data[e] );
			
			System.out.println( "v" + i + ": " + String.join( ", ", parts ) );
		}
	}
	
	public static VertexBuffer parse( String content )
	{
		String[] lines = content.split( "\n" );
		VertexBuffer array = new VertexBuffer();
		
		try
		{
		for( String line : lines )
		{
			String[] parts = line.split( "\\s*,\\s*" );
			
			if( parts[0].equals( "i" ) )
			{
				ArrayList<String> indices = new ArrayList<String>();
				for( int i = 1; i < parts.length; i++ )
					indices.add( parts[i] );
				array.setIndices( indices.stream().mapToInt( i -> Integer.parseInt( i ) ).toArray() );
			}
			else
			{
				int vertexID = Integer.parseInt( parts[0] );
				
				ArrayList<String> elements = new ArrayList<String>();
				for( int i = 1; i < parts.length; i++ )
					elements.add( parts[i] );
				
				float[] buffer = new float[ elements.size() ];
				for( int i = 0; i < elements.size(); i++ )
					buffer[i] = Float.parseFloat( elements.get( i ) );
				
				Vertex vertex = new Vertex( buffer );
				
				if( array.isEmpty() )
					array._stride = vertex.getDimension();
				else if( array.stride() != vertex.getDimension() )
					return null;
				
				array.add( vertexID, vertex );
			}
		}
		}
		catch( NumberFormatException e ) { return null; }
		
		return array;
	}
}
 