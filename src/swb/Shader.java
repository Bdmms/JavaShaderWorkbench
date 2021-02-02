package swb;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

import swb.editors.EditorView;
import swb.editors.ShaderEditor;

public class Shader extends GLNode
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
	
	@Override
	public boolean compile( GL3 gl )
	{
		System.out.println( "Compiling: " + getPath() );
		int compiled = gl.glCreateShader( _type );
		gl.glShaderSource( compiled, 1, _code, null );
		gl.glCompileShader( compiled );
		
		final int[] compileStatus = { 1 };
		gl.glGetShaderiv( compiled, GL3.GL_COMPILE_STATUS, compileStatus, 0 );
		
		if ( compileStatus[0] == GL3.GL_TRUE )
		{
			if( _id != -1 ) 
				gl.glDeleteShader( _id );
			
			_id = compiled;
			return super.compile( gl );
		}
		else
		{
			gl.glDeleteShader( compiled );
			System.err.println( ShaderUtil.getShaderInfoLog( gl, compiled ) );
			return false;
		}
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		if( _id != -1 )
		{
			gl.glDeleteShader( _id );
			_id = -1;
		}
	}
	
	@Override
	public EditorView createEditor() 
	{
		return new ShaderEditor( getPath(), this );
	}
	
	public void setCode( String code )
	{
		isModified = true;
		isCompiled = false;
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
	
	public int getType() 
	{ 
		return _type; 
	}
}
