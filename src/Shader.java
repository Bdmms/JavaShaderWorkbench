import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class Shader 
{
	private String[] _code = new String[1];
	private int _type;
	private int _id = -1;
	
	public Shader( String code, int type )
	{
		setCode( code );
		_type = type;
	}
	
	public boolean compile( GL3 gl )
	{
		int id = gl.glCreateShader( _type );
		gl.glShaderSource( id, 1, _code, null );
		gl.glCompileShader( id );
		
		final int[] compileStatus = { 1 };
		gl.glGetShaderiv( id, GL3.GL_COMPILE_STATUS, compileStatus, 0 );
		
		if ( compileStatus[0] == GL3.GL_TRUE )
		{
			// Delete old shader
			if( _id != -1 )
				gl.glDeleteShader( _id );
			_id = id;
			return true;
		}
		else
		{
			System.err.println( ShaderUtil.getShaderInfoLog( gl, id ) );
			gl.glDeleteShader( id );
			return false;
		}
	}
	
	public void dispose( GL3 gl )
	{
		gl.glDeleteShader( _id );
	}
	
	public void setCode( String code )
	{
		_code[0] = code;
	}
	
	public int getID() { return _id; }
}
