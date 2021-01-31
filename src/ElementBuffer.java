import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import math.vec3i;

public class ElementBuffer extends LeafNode
{
	public final static String TAG = "elements";
	
	private int[] _buffer;
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
	
	public void replaceWith( List<vec3i> elements )
	{
		_buffer = new int[ elements.size() * 3 ];
		int idx = 0;
		for( vec3i vec : elements )
		{
			_buffer[idx++] = vec.x;
			_buffer[idx++] = vec.y;
			_buffer[idx++] = vec.z;
		}
		
		isCompiled = false;
		isModified = true;
	}
	
	public void set( int triangle, int i, int value )
	{
		_buffer[ triangle * 3 + i ] = value;
		isModified = true;
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
		
		isCompiled = false;
		isModified = true;
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
	
	@Override
	public boolean compile( GL3 gl )
	{
		if( isLoaded ) delete( gl );
		return super.compile( gl );
	}
	
	@Override
	public void bind( GL3 gl )
	{
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
	}
	
	@Override
	public void upload( GL3 gl )
	{
		if( !isLoaded )
		{
			System.out.println( "Initializing: " + getPath() + " (" + size() + " tris)" );
			gl.glGenBuffers( 1, ebo, 0 );
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, _buffer.length * Integer.BYTES, Buffers.newDirectIntBuffer( _buffer ), GL3.GL_STATIC_DRAW );
		}
		else if( isModified )
		{
			System.out.println( "Uploading: " + getPath() + " (" + size() + " tris)" );
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, _buffer.length * Integer.BYTES, Buffers.newDirectIntBuffer( _buffer ), GL3.GL_STATIC_DRAW );
		}
		
		super.upload( gl );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
		gl.glDrawElements( GL3.GL_TRIANGLES, _buffer.length, GL3.GL_UNSIGNED_INT, 0 );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		if( !isLoaded ) return;
		delete( gl );
		super.dispose( gl );
	}
	
	private void delete( GL3 gl )
	{
		System.out.println( "Deleting: " + getPath() );
		gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( ebo ) );
		isLoaded = false;
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
