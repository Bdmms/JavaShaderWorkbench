import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

public class VertexBuffer extends Node
{
	public final static String TAG = "vertices";
	
	private float[] _buffer;
	private int _stride = 0;
	private boolean _isLoaded = false;
	
	public int[] vbo = new int[1]; // Vertex buffer
	public int[] vao = new int[1]; // Vertex array
	
	public VertexBuffer( int size, int stride ) 
	{ 
		this( new float[ size * stride ], stride );
	}
	
	public VertexBuffer( float[] buffer, int stride )
	{
		super( TAG );
		_buffer = buffer;
		_stride = stride;
	}
	
	@Override
	public EditorView createEditor()
	{
		return new VertexTable( getPath(), this );
	}
	
	public void replace( float[] buffer, int stride )
	{
		_buffer = buffer;
		_stride = stride;
	}
	
	public void resize( int size )
	{
		float[] data = new float[size];
		for( int i = 0; i < _buffer.length && i < size; i++ )
			data[i] = _buffer[i];
		_buffer = data;
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
	}
	
	@Override
	public boolean initialize( GL3 gl, CompileStatus status )
	{
		if( _isLoaded ) dispose( gl );
		
		gl.glGenVertexArrays( 1, vao, 0 );
		gl.glGenBuffers( 1, vbo, 0 );
		
		gl.glBindVertexArray( vao[0] );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
		gl.glBufferData( GL3.GL_ARRAY_BUFFER, _buffer.length * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer ), GL3.GL_STATIC_DRAW );

		// TODO: Make linking less arbitrary
		VertexAttribute attribute = VertexAttribute.parse( ((Shader)status.shader.children().get( 0 )).getCode() );
		if( attribute == null || !attribute.isCompatibleWith( _stride ) )
		{
			System.err.println("Error - Incompatible attribute!");
			return false;
		}
		
		attribute.print();
		attribute.bind( gl );
		
		_isLoaded = true;
		
		System.out.println( size() + " vertices loaded");
		
		return super.initialize( gl, status );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glBindVertexArray( vao[0] );
		
		super.render( gl );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		super.dispose( gl );
		
		if( !_isLoaded ) return;
		
		gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( vbo ) );
		gl.glDeleteVertexArrays( 1, Buffers.newDirectIntBuffer( vao ) );
		_isLoaded = false;
	}
	
	public void set( int i, float ... vertex )
	{
		for( float value : vertex )
			_buffer[i++] = value;
	}
	
	public int stride() 
	{
		return _stride;
	}
	
	public int size()
	{
		return _buffer.length / _stride;
	}
	
	public int length()
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
 