import com.jogamp.opengl.GL3;

public class Mesh 
{
	public static final int NULL = 0;
	
	private VertexBuffer _vertices;
	
	public Mesh( GL3 gl, VertexBuffer vertices, VertexAttribute atr )
	{
		_vertices = vertices;
		_vertices.initialize( gl );
		
		atr.bind( gl );
		
		gl.glBindVertexArray( NULL );
	}
	
	public void dispose( GL3 gl )
	{
		_vertices.dispose( gl );
	}
	
	public void render( GL3 gl, ShaderProgram shader )
	{
		gl.glBindVertexArray( _vertices.vao[0] );
		gl.glDrawElements( GL3.GL_TRIANGLES, _vertices.indices().length, GL3.GL_UNSIGNED_INT, 0 );
		gl.glBindVertexArray( NULL );
	}
}
