import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class ShaderProgram extends Node
{
	// Shader Global Uniforms
	public static float timer = 0.0f;
	public static float[] view = new float[16];
	
	private int _shaderID = -1;
	
	private int timer_loc;
	private int view_loc;
	
	public ShaderProgram( String name )
	{
		super( name );
	}
	
	@Override
	public boolean initialize( GL3 gl, CompileStatus status )
	{
		_shaderID = gl.glCreateProgram();
		
		// Compile and attach Shader nodes
		for( Node node : children() )
		{
			if( node instanceof Shader )
			{
				Shader shader = (Shader)node;
				
				if( !shader.compile( gl ) )
					return false;
				
				System.out.println( "Compiled: " + shader );
				gl.glAttachShader( _shaderID, shader.getID() );
			}
		}
		
		gl.glLinkProgram( _shaderID );
		
		final int[] linkStatus = { 1 };
		gl.glGetProgramiv( _shaderID, GL3.GL_LINK_STATUS, linkStatus, 0 );
		
		if( linkStatus[0] == GL3.GL_TRUE )
		{
			setGlobalUniformLocations( gl );
			status.shader = this;
			return super.initialize( gl, status );
		}
		else
		{
			System.err.println( ShaderUtil.getProgramInfoLog( gl, _shaderID ) );
			return false;
		}
	}
	
	private void setGlobalUniformLocations( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
		timer_loc = gl.glGetUniformLocation( _shaderID, "time" );
		view_loc = gl.glGetUniformLocation( _shaderID, "view" );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
		
		// Update Global Uniforms
		gl.glUniform1f( timer_loc, timer );
		gl.glUniformMatrix4fv( view_loc, 1, false, view, 0);
		
		super.render( gl );
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
}
