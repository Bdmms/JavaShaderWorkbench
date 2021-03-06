package swb;

import java.io.File;
import java.util.HashMap;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class ShaderCode 
{
	/* All loaded shaders */
	private static HashMap<String, ShaderCode> library = new HashMap<>();
	
	private String _name;
	private String[] _code = new String[1];
	private int[] compileStatus = new int[1];
	private int _type;
	private int _id = -1;
	
	public boolean modifyFlag = true;
	
	public ShaderCode( String name, String code, int type )
	{
		_name = name;
		_code[0] = code;
		_type = type;
	}
	
	public ShaderCode( File file, int type )
	{
		this( file.getName(), ModelUtils.fileToString( file ), type );
	}
	
	public boolean compile( GL3 gl )
	{
		if( !modifyFlag ) return true;
		
		int compiled = gl.glCreateShader( _type );
		gl.glShaderSource( compiled, 1, _code, null );
		gl.glCompileShader( compiled );
		
		gl.glGetShaderiv( compiled, GL3.GL_COMPILE_STATUS, compileStatus, 0 );
		
		if ( compileStatus[0] == GL3.GL_TRUE )
		{
			dispose( gl );
			_id = compiled;
			modifyFlag = false;
			return true;
		}
		else
		{
			gl.glDeleteShader( compiled );
			System.err.println( ShaderUtil.getShaderInfoLog( gl, compiled ) );
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
	}
	
	public void setCode( String code )
	{
		modifyFlag = true;
		_code[0] = code;
	}
	
	public String getName()
	{
		return _name;
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
	
	/**
	 * Determines the shader type based on the path extension
	 */
	public static int pathToShaderType( String name )
	{
		switch( name.substring( name.lastIndexOf( '.' ) + 1 ).toLowerCase() )
		{
		default:
		case "vs": return GL3.GL_VERTEX_SHADER;
		case "fs": return GL3.GL_FRAGMENT_SHADER;
		case "gs": return GL3.GL_GEOMETRY_SHADER;
		}
	}
	
	/**
	 * Removes all loaded shader
	 */
	public static void deleteShaders( GL3 gl )
	{
		for( ShaderCode shader : library.values() )
		{
			shader.dispose( gl );
		}
		
		library.clear();
	}
	
	/**
	 * Returns a shader with the given name
	 */
	public static ShaderCode loadShader( String filepath )
	{
		ShaderCode shader = library.get( filepath );
		
		if( shader == null )
		{
			shader = new ShaderCode( new File( filepath ), pathToShaderType( filepath ) );
			library.put( filepath, shader );
		}
		
		return shader;
	}
	
	/**
	 * Returns a shader with the given name; will read file if the shader has not been loaded
	 */
	public static ShaderCode loadShader( File file )
	{
		String path = file.getPath();
		ShaderCode shader = library.get( path );
		
		if( shader == null )
		{
			shader = new ShaderCode( file, pathToShaderType( path ) );
			library.put( path, shader );
		}
		
		return shader;
	}
	
	/**
	 * Compiles all shader code
	 */
	public static void compileShaders( GL3 gl )
	{
		for( ShaderCode shader : library.values() )
		{
			shader.compile( gl );
		}
	}
}
