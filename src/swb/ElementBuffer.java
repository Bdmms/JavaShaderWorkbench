package swb;
import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import swb.editors.EditorView;
import swb.editors.ElementTable;
import swb.math.vec3i;

/**
 * Stores indices in a buffer used to render vertices in a specified order.
 * 
 * Dependencies:
 * 	- VertexBuffer
 */
public class ElementBuffer extends GLNode
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
		super( TAG, ID_BUFFER, true );
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
			vec.copyTo( _buffer, idx );
			idx += 3;
		}
		
		compileFlag = false;
		modifyFlag = true;
	}
	
	public void set( int triangle, int i, int value )
	{
		_buffer[ triangle * 3 + i ] = value;
		modifyFlag = true;
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
		modifyFlag = true;
		deleteFlag = true;
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
	public void bind( GL3 gl )
	{
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
	}
	
	@Override
	public boolean build( Renderer renderer )
	{
		return renderer.instances[LAST_ARRAY] instanceof VertexBuffer;
	}
	
	@Override
	public void update( GL3 gl )
	{
		if( deleteFlag ) delete( gl );
		
		if( !initFlag )
		{
			System.out.println( "Initializing: " + getPath() + " (" + length() + " tris)" );
			gl.glGenBuffers( 1, ebo, 0 );
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, _buffer.length * Integer.BYTES, Buffers.newDirectIntBuffer( _buffer ), GL3.GL_STATIC_DRAW );
		}
		else if( modifyFlag )
		{
			System.out.println( "Uploading: " + getPath() + " (" + length() + " tris)" );
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, _buffer.length * Integer.BYTES, Buffers.newDirectIntBuffer( _buffer ), GL3.GL_STATIC_DRAW );
		}
		
		super.update( gl );
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
		if( !initFlag ) return;
		delete( gl );
		super.dispose( gl );
	}
	
	private void delete( GL3 gl )
	{
		System.out.println( "Deleting: " + getPath() );
		gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( ebo ) );
		deleteFlag = false;
		initFlag = false;
	}
	
	public int length()
	{
		return _buffer.length / 3;
	}
	
	public boolean isRenderable()
	{
		return true;
	}
}
