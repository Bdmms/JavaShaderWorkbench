import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

public class ElementBuffer extends AbstractNode
{
	public final static String TAG = "elements";
	
	private int[] _buffer;
	private boolean _isLoaded = false;
	
	public int[] ebo = new int[1]; // Element buffer
	
	public ElementBuffer( int size )
	{
		this( new int[size] );
	}
	
	public ElementBuffer( int[] buffer )
	{
		super( TAG );
		_buffer = buffer;
	}
	
	@Override
	public EditorView createEditor()
	{
		return new ElementTable( getPath(), this );
	}
	
	public void replace( int[] buffer )
	{
		_buffer = buffer;
	}
	
	public void set( int triangle, int i, int value )
	{
		_buffer[ triangle * 3 + i ] = value;
	}
	
	public int get( int triangle, int i )
	{
		return _buffer[ triangle * 3 + i ];
	}
	
	public void resize( int size )
	{
		int[] data = new int[size];
		for( int i = 0; i < _buffer.length && i < size; i++ )
			data[i] = _buffer[i];
		_buffer = data;
	}
	
	public void add( int ... triangle )
	{
		if( triangle.length % 3 != 0 )
		{
			System.err.println( "Error - Invalid triangle" );
			return;
		}
		
		int index = _buffer.length;
		resize( index + triangle.length );
		
		for( int i = 0; i < triangle.length; i++ )
			_buffer[ index + i ] = triangle[i];
	}
	
	public void set( int i, int v1, int v2, int v3 )
	{
		_buffer[i++] = v1;
		_buffer[i++] = v2;
		_buffer[i  ] = v3;
	}
	
	@Override
	public boolean initialize( GL3 gl )
	{
		if( _isLoaded ) dispose( gl );
		
		gl.glGenBuffers( 1, ebo, 0 );
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
		gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, _buffer.length * Integer.BYTES, Buffers.newDirectIntBuffer( _buffer ), GL3.GL_STATIC_DRAW );
	
		_isLoaded = true;
		
		return super.initialize( gl );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
		gl.glDrawElements( GL3.GL_TRIANGLES, _buffer.length, GL3.GL_UNSIGNED_INT, 0 );
		super.render( gl );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		super.dispose( gl );
		
		if( !_isLoaded ) return;
		
		gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( ebo ) );
		_isLoaded = false;
	}
	
	public int length()
	{
		return _buffer.length;
	}
	
	public int size()
	{
		return _buffer.length / 3;
	}
}
