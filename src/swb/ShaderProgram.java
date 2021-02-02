package swb;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

import swb.editors.EditorView;

public class ShaderProgram extends GLNode
{
	private static List<ShaderProgram> SHADERS = new ArrayList<>();
	
	// Shader Global Uniforms
	public static float timer = 0.0f;
	public static float[] view = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	public static float[] projection = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	
	private int[] linkStatus = { 1 };
	private int _shaderID = -1;
	
	private int _timerLoc;
	private int _viewLoc;
	private int _projLoc;
	
	public ShaderProgram( String name )
	{
		super( name );
		SHADERS.add( this );
	}
	
	@Override
	public void bind( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
	}
	
	@Override
	public boolean compile( GL3 gl )
	{
		if( super.compile( gl ) )
		{
			System.out.println( "Compiling: " + getPath() );
			int compiled = gl.glCreateProgram();
			
			for( GLNode node : children() )
			{
				if( node instanceof Shader )
				{
					Shader shader = (Shader)node;
					gl.glAttachShader( compiled, shader.getID() );
				}
			}
			
			gl.glLinkProgram( compiled );
			gl.glGetProgramiv( compiled, GL3.GL_LINK_STATUS, linkStatus, 0 );
			
			if( linkStatus[0] == GL3.GL_TRUE )
			{
				if( _shaderID != -1 ) 
					gl.glDeleteProgram( _shaderID );
				
				_shaderID = compiled;
				
				gl.glUseProgram( _shaderID );
				_timerLoc = gl.glGetUniformLocation( _shaderID, "time" );
				_viewLoc = gl.glGetUniformLocation( _shaderID, "view" );
				_projLoc = gl.glGetUniformLocation( _shaderID, "projection" );
				return true;
			}
			
			gl.glDeleteShader( compiled );
			System.err.println( ShaderUtil.getProgramInfoLog( gl, compiled ) );
		}
		
		return false;
	}
	
	@Override
	public void upload( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
		super.upload( gl );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
		
		// Update Global Uniforms
		gl.glUniform1f( _timerLoc, timer );
		gl.glUniformMatrix4fv( _viewLoc, 1, true, view, 0);
		gl.glUniformMatrix4fv( _projLoc, 1, true, projection, 0);
		
		// Avoid rendering components in Program
		//super.render( gl );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		if( _shaderID != -1 )
		{
			gl.glDeleteProgram( _shaderID );
			_shaderID = -1;
		}
		
		super.dispose( gl );
	}
	
	@Override
	public EditorView createEditor() 
	{
		return null;
	}
	
	public int getID()
	{
		return _shaderID;
	}
	
	public Shader getShaderComponent( int type )
	{
		for( GLNode node : children() )
		{
			if( node instanceof Shader && ((Shader)node).getType() == type )
			{
				return (Shader)node;
			}
		}
		
		return null;
	}
	
	public static ShaderProgram find( final int shaderID )
	{
		return SHADERS.stream().filter( shader -> shader._shaderID == shaderID ).findFirst().get();
	}
	
	public static ShaderProgram createFrom( String name, File vsFile, File fsFile )
	{
		ShaderProgram program = new ShaderProgram( name );
		Shader vertex = new Shader( "shader.vs", ModelUtils.fileToString( vsFile ), GL3.GL_VERTEX_SHADER );
		Shader fragment = new Shader( "shader.fs", ModelUtils.fileToString( fsFile ), GL3.GL_FRAGMENT_SHADER );
		program.add( vertex );
		program.add( fragment );
		return program;
	}
}
