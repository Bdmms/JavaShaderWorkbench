package swb;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

/**
 * TODO: find a way to swap the buffers of the vertices
 * @author mmsra
 *
 * @param <T>
 */
public class DynamicVertexBuffer<T extends Vertex> extends VertexBuffer
{
	List<T> vertices = new ArrayList<>();
	
	public DynamicVertexBuffer( int stride ) 
	{
		super( 0, stride );
	}
	
	public DynamicVertexBuffer( int initCapacity, int stride ) 
	{
		super( initCapacity, stride );
	}

	public void addVertex( T vertex )
	{
		vertices.add( vertex );
	}
	
	public T getVertex( int i )
	{
		return vertices.get( i );
	}
	
	public int getOffset()
	{
		return _size * _stride;
	}
	
	public float[] getData()
	{
		return _buffer;
	}
	
	public void updateDynamic( GL3 gl )
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
					vertex.copyTo( _buffer, index );
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
					vertex.copyTo( _buffer, i * _stride );
			}
			
			gl.glBufferData( GL3.GL_ARRAY_BUFFER, _buffer.length * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer ), GL3.GL_DYNAMIC_DRAW );
		}
	}
	
	public String toString()
	{
		String[] parts = new String[vertices.size()];
		for( int i = 0; i < vertices.size(); i++ )
			parts[i] = vertices.get( i ).toString();
		return String.join( "\n", parts );
	}
}
