package swb;
import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import swb.editors.EditorView;
import swb.editors.VertexTable;

public class VertexBuffer extends GLNode
{
	public final static String TAG = "vertices";
	
	private VertexAttribute _attribute = null;
	
	private float[] _buffer;
	private int _size = 0;
	private int _stride = 0;
	
	private int[] _currentShader = new int[1];
	public int[] vbo = { -1 }; // Vertex buffer
	public int[] vao = { -1 }; // Vertex array
	
	public VertexBuffer( int capacity, int stride ) 
	{ 
		super( TAG );
		_buffer = new float[ capacity * stride ];
		_stride = stride;
		_size = 0;
	}
	
	public VertexBuffer( float[] buffer, int stride )
	{
		super( TAG );
		_buffer = buffer;
		_stride = stride;
		_size = buffer.length / stride;
	}
	
	@Override
	public EditorView createEditor()
	{
		return new VertexTable( getPath(), this );
	}
	
	public void resize( int size )
	{
		float[] data = new float[size];
		for( int i = 0; i < _buffer.length && i < size; i++ )
			data[i] = _buffer[i];
		_buffer = data;
		_size = _buffer.length / _stride;
		isCompiled = false;
		isModified = true;
	}
	
	public void add( float ... vertex )
	{
		if( vertex.length % _stride != 0 )
		{
			System.err.println( "Error - Invalid vertex" );
			return;
		}
		
		int index = _buffer.length;
		resize( index + vertex.length );
		
		for( int i = 0; i < vertex.length; i++ )
			_buffer[ index + i ] = vertex[i];
	}
	
	public float get( int vertIdx, int element )
	{
		return _buffer[ vertIdx * _stride + element ];
	}
	
	public void set( int vertIdx, int element, float value )
	{
		_buffer[ vertIdx * _stride + element ] = value;
		isModified = true;
	}
	
	public void transformBy( float[] transform )
	{
		
	}
	
	@Override
	public boolean compile( GL3 gl )
	{
		if( isLoaded ) delete( gl );
		
		System.out.println( "Compiling: " + getPath() );
		gl.glGetIntegerv( GL3.GL_CURRENT_PROGRAM, _currentShader, 0 );
		
		ShaderProgram program = ShaderProgram.find( _currentShader[0] );
		if( program == null )
		{
			System.err.println( "Error - Shader Program not defined" );
			return false;
		}
		
		Shader vShader = program.getShaderComponent( GL3.GL_VERTEX_SHADER );
		if( vShader == null )
		{
			System.err.println( "Error - Vertex Shader not defined" );
			return false;
		}
		
		_attribute = VertexAttribute.parse( vShader.getCode() );
		if( _attribute == null || !_attribute.isCompatibleWith( _stride ) )
		{
			System.err.println( "Error - Incompatible attribute!" );
			return false;
		}
		
		_attribute.print();
		return super.compile( gl );
	}
	
	@Override
	public void bind( GL3 gl )
	{
		gl.glBindVertexArray( vao[0] );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
	}
	
	@Override
	public void upload( GL3 gl )
	{
		if( !isLoaded )
		{
			System.out.println( "Initializing: " + getPath() + " (" + size() + " vertices)" );
			gl.glGenVertexArrays( 1, vao, 0 );
			gl.glGenBuffers( 1, vbo, 0 );
		
			gl.glBindVertexArray( vao[0] );
			gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
			gl.glBufferData( GL3.GL_ARRAY_BUFFER, _buffer.length * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer ), GL3.GL_STATIC_DRAW );
			
			_attribute.bind( gl );
		}
		else if( isModified )
		{
			System.out.println( "Uploading: " + getPath() + " (" + size() + " vertices)" );
			gl.glBindVertexArray( vao[0] );
			gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
			gl.glBufferSubData( GL3.GL_ARRAY_BUFFER, 0, _buffer.length * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer ) );
		}
		
		super.upload( gl );
	}
	
	public <T extends Vertex> void update( GL3 gl, List<T> vertices )
	{
		int currentCapacity = vertices.size() * _stride;
		_size = vertices.size();
		
		gl.glBindVertexArray( vao[0] );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
		
		if( _buffer.length >= currentCapacity )
		{
			for( int i = 0; i < vertices.size(); i++ )
			{
				Vertex vertex = vertices.get( i );
				
				if( vertex.isModified() )
				{
					int index = i * _stride;
					vertex.writeDataBuffer( _buffer, index );
					gl.glBufferSubData( GL3.GL_ARRAY_BUFFER, index * Float.BYTES, _stride * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer, index ) );
				}
			}
		}
		else
		{
			_buffer = new float[ _buffer.length * 2 ];
			for( int i = 0; i < vertices.size(); i++ )
			{
				Vertex vertex = vertices.get( i );
				
				if( vertex.isModified() )
					vertex.writeDataBuffer( _buffer, i * _stride );
			}
			
			gl.glBufferData( GL3.GL_ARRAY_BUFFER, _buffer.length * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer ), GL3.GL_DYNAMIC_DRAW );
		}
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glBindVertexArray( vao[0] );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		delete( gl );
		super.dispose( gl );
	}
	
	private void delete( GL3 gl )
	{
		System.out.println( "Deleting: " + getPath() );
		gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( vbo ) );
		gl.glDeleteVertexArrays( 1, Buffers.newDirectIntBuffer( vao ) );
		isLoaded = false;
	}
	
	public int stride() 
	{
		return _stride;
	}
	
	public int size()
	{
		return _size;
	}
	
	public int capacity()
	{
		return _buffer.length;
	}
	
	public void print() 
	{
		// Number of vertices
		int size = size();
		
		System.out.println( size + " vertices (" + stride() + ")" );
		for( int i = 0; i < size; i++ )
		{
			int offset = i * _stride;
			String[] parts = new String[ _stride ];
			
			for( int e = 0; e < _stride; e++ )
				parts[e] = String.valueOf( _buffer[offset + e] );
			
			System.out.println( "v" + i + ": " + String.join( ", ", parts ) );
		}
	}
}
 