import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class Shader extends AbstractNode
{
	private String[] _code = new String[1];
	private int _type;
	private int _id = -1;
	
	public Shader( String name, String code, int type )
	{
		super( name );
		setCode( code );
		_type = type;
	}
	
	public boolean compile( GL3 gl )
	{
		_id = gl.glCreateShader( _type );
		gl.glShaderSource( _id, 1, _code, null );
		gl.glCompileShader( _id );
		
		final int[] compileStatus = { 1 };
		gl.glGetShaderiv( _id, GL3.GL_COMPILE_STATUS, compileStatus, 0 );
		
		if ( compileStatus[0] == GL3.GL_TRUE )
		{
			return super.initialize( gl );
		}
		else
		{
			System.err.println( ShaderUtil.getShaderInfoLog( gl, _id ) );
			return false;
		}
	}
	
	public void dispose( GL3 gl )
	{
		if( _id != -1 )
		{
			gl.glDeleteShader( _id );
			_id = -1;
		}
		
		super.dispose( gl );
	}
	
	public void setCode( String code )
	{
		_code[0] = code;
	}
	
	public String getCode()
	{
		return _code[0];
	}
	
	public int getID() 
	{ 
		return _id; 
	}

	@Override
	public EditorView createEditor() 
	{
		return new ShaderEditor( getPath(), this );
	}
}
