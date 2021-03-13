package swb;

import com.jogamp.opengl.GL3;

public class VertexBufferDirect extends VertexBuffer
{
	public VertexBufferDirect( float[] buffer ) 
	{
		super( buffer, 3 );
	}
	
	public VertexBufferDirect( int size ) 
	{
		super( size, 3 );
	}
	
	public VertexBufferDirect( String name, float[] buffer )
	{
		super( name, buffer, 3 );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glBindVertexArray( vao[0] );
		gl.glDrawArrays( GL3.GL_TRIANGLES, 0, _size );
	}
}
