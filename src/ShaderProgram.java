import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class ShaderProgram 
{
	private Shader _vertex;
	private Shader _fragment;
	private int _shaderID = -1;
	
	private boolean _valid = false;
	
	public ShaderProgram( Shader vShader, Shader fShader )
	{
		_vertex = vShader;
		_fragment = fShader;
	}
	
	public boolean compile( GL3 gl )
	{
		if( !_vertex.compile( gl ) || !_fragment.compile( gl ) ) 
		{
			_valid = false;
			return false;
		}
		
		int id = gl.glCreateProgram();
		
		gl.glAttachShader( id, _vertex.getID() );
		gl.glAttachShader( id, _fragment.getID() );
		gl.glLinkProgram( id );
		
		final int[] linkStatus = { 1 };
		gl.glGetProgramiv( id, GL3.GL_LINK_STATUS, linkStatus, 0 );
		
		if( linkStatus[0] == GL3.GL_TRUE )
		{
			// Delete old program
			if( _shaderID != -1 )
				gl.glDeleteProgram( _shaderID );
			_shaderID = id;
			_valid = true;
			return true;
		}
		else
		{
			System.err.println( ShaderUtil.getProgramInfoLog( gl, _shaderID ) );
			gl.glDeleteProgram( id );
			_valid = false;
			return false;
		}
	}
	
	public void dispose( GL3 gl )
	{
		gl.glDeleteProgram( _shaderID );
		_vertex.dispose( gl );
		_fragment.dispose( gl );
	}
	
	public void invalidate()
	{
		_valid = false;
	}
	
	public int getID()
	{
		return _shaderID;
	}
	
	public boolean isValid()
	{
		return _valid;
	}
}
