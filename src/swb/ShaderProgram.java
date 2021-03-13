package swb;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

import swb.editors.EditorView;
import swb.editors.ShaderEditor;

public class ShaderProgram extends GLNode
{
	// Shader Global Uniforms
	public Camera view = null;
	
	private List<ShaderCode> shaders = new ArrayList<>();
	
	private int[] linkStatus = { 1 };
	private int _shaderID = -1;
	
	private int _timerLoc;
	private int _viewLoc;
	private int _posLoc;
	private int _projLoc;
	
	public ShaderProgram( String name )
	{
		super( name, ID_PROGRAM, true );
	}
	
	public void addShader( File shaderFile )
	{
		addShader( ShaderCode.loadShader( shaderFile ) );
	}
	
	public void addShader( ShaderCode shader )
	{
		shaders.add( shader );
		add( new ShaderNode( shader ) );
	}
	
	@Override
	public boolean build( Renderer renderer )
	{
		view = renderer.camera;
		if( view == null ) return false;
		
		for( ShaderCode shader : shaders )
		{
			compileFlag &= !shader.modifyFlag;
		}
		return true;
	}
	
	@Override
	public void bind( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
	}
	
	@Override
	public boolean compile( GL3 gl )
	{
		if( compileFlag ) return true;
		
		System.out.println( "Compiling: " + getPath() );
		
		int compiled = gl.glCreateProgram();
		for( ShaderCode shader : shaders )
		{
			gl.glAttachShader( compiled, shader.getID() );
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
			_posLoc = gl.glGetUniformLocation( _shaderID, "viewPos" );
			_projLoc = gl.glGetUniformLocation( _shaderID, "projection" );
			return super.compile( gl );
		}
		
		gl.glDeleteShader( compiled );
		System.err.println( ShaderUtil.getProgramInfoLog( gl, compiled ) );
		return false;
	}
	
	@Override
	public void update( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
		super.update( gl );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glUseProgram( _shaderID );
		
		// Update Global Uniforms
		gl.glUniform1f( _timerLoc, view.timer );
		view.position.upload( gl, _posLoc );
		view.view.upload( gl, _viewLoc );
		view.projection.upload( gl, _projLoc );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		super.dispose( gl );
		
		if( _shaderID != -1 )
		{
			gl.glDeleteProgram( _shaderID );
			_shaderID = -1;
		}
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
	
	public ShaderCode getShaderComponent( int type )
	{
		for( ShaderCode shader : shaders )
		{
			if( shader.getType() == type )
			{
				return shader;
			}
		}
		
		return null;
	}
	
	public static ShaderProgram generateProgram( String name, File vsFile, File fsFile )
	{
		ShaderProgram program = new ShaderProgram( name );
		program.addShader( vsFile );
		program.addShader( fsFile );
		return program;
	}
	
	public static ShaderProgram generateProgram( String name )
	{
		String prefix = "src\\swb\\svg\\" + name;
		return generateProgram( "Shader", new File( prefix + ".vs" ), new File( prefix + ".fs" ) );
	}
	
	public static class ShaderNode extends GLNode
	{
		private ShaderCode shader;
		
		public ShaderNode( ShaderCode shader ) 
		{
			super( shader.getName() );
			this.shader = shader;
		}
		
		@Override
		public EditorView createEditor() 
		{
			return new ShaderEditor( getPath(), this );
		}
		
		public ShaderCode getShader()
		{
			return shader;
		}
	}
}
