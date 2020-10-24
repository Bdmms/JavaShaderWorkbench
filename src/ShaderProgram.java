import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class ShaderProgram extends AbstractNode
{
	private int _shaderID = -1;
	
	public ShaderProgram( String name )
	{
		super( name );
	}
	
	@Override
	public boolean initialize( GL3 gl )
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
			return super.initialize( gl );
		}
		else
		{
			System.err.println( ShaderUtil.getProgramInfoLog( gl, _shaderID ) );
			return false;
		}
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
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
