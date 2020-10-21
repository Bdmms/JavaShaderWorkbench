import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

public class View3D extends GLJPanel implements GLEventListener
{
	private static final long serialVersionUID = 1L;
	
	private boolean _recompile = false;
	private ShaderProgram _program;
	private ShaderEditor _editor;
	private VertexAttribute _attribute;
	private VertexBuffer _vertices;
	private Mesh _mesh;
	
	private int timer_loc;
	private float timer = 0.0f;
	
	public View3D( GLCapabilities capabilities, ShaderProgram program, ShaderEditor editor )
	{
		super( capabilities );
		
		_program = program;
		_editor = editor;
		
		addGLEventListener( this );
	}
	
	private void recompileAll( GL3 gl )
	{
		_recompile = false;
		
		if( !_program.compile( gl ) )
			return;
		
		_attribute = VertexAttribute.parse( _editor.getCode( ShaderEditor.VERT_SHDR ) );
		_vertices = VertexBuffer.parse( _editor.getCode( ShaderEditor.VERT_BUFF ) );

		if( _vertices == null || _vertices.indices() == null || _attribute == null || !_attribute.isCompatibleWith( _vertices ) )
		{
			_program.invalidate();
			System.err.println("Error - Incompatible vertices!");
			return;
		}
		
		_attribute.print();
		_vertices.print();
		
		if( _mesh != null )
			_mesh.dispose( gl );
		_mesh = new Mesh( gl, _vertices, _attribute );
		
		// Timer that can be used in shader
		timer_loc = gl.glGetUniformLocation( _program.getID(), "gl_time" );
		
		// Bind uniforms and textures
		gl.glUseProgram( _program.getID() );
		((UniformTable)_editor.getEditor( ShaderEditor.UNIFORMS )).bindAll( gl, _program );
		((TextureTable)_editor.getEditor( ShaderEditor.TEXTURES )).bindAll( gl, _program );
		
		System.out.println( "SUCCESS" );
	}
	
	private void render( GL3 gl ) 
	{
		gl.glUseProgram( _program.getID() );
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		// Set timer parameter used by shader
		gl.glUniform1f( timer_loc, timer );
		
		_mesh.render( gl, _program );
		
		int error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Error on render: " + error );
	}
	
	@Override
	public void display(GLAutoDrawable g) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		if( _recompile )
			recompileAll( gl );
		
		if( _program.isValid() )
			render( gl );
		
		timer += 0.01f;
		timer += Math.floor( timer );
	}

	@Override
	public void dispose(GLAutoDrawable g) 
	{
		final GL3 gl = g.getGL().getGL3();
		_mesh.dispose( gl );
		_program.dispose( gl );
	}

	@Override
	public void init(GLAutoDrawable g) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		if( !gl.isExtensionAvailable("GL_ARB_explicit_attrib_location") )
			System.err.println( "MISSING EXTENSION" );
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		
		gl.glEnable( GL3.GL_TEXTURE_2D );
		
		gl.glEnable( GL3.GL_DEPTH_TEST );
		gl.glDepthFunc( GL3.GL_LESS );
		
		setAnimator( new FPSAnimator( g, 30, true ) );
		getAnimator().start();
	}

	@Override
	public void reshape(GLAutoDrawable g, int x, int y, int width, int height) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		gl.glViewport( 0, 0, width, height );
	}
	
	public void recompile()
	{
		_recompile = true;
	}
}
