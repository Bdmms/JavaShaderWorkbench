package swb;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

public class View3D extends GLJPanel implements GLEventListener
{
	private static final long serialVersionUID = 1L;
	
	private Camera _camera;
	private ProjectTree _tree;
	private Renderer _renderer;
	
	// Flags
	private boolean _isValid = false;
	private boolean _isAnimated = false;
	
	public View3D( GLCapabilities capabilities, ProjectTree editor )
	{
		super( capabilities );
		_tree = editor;
		
		_camera = new Camera( this );
		_renderer = new Renderer( _camera );
		
		addMouseListener( _camera );
		addMouseMotionListener( _camera );
		addMouseWheelListener( _camera );
		addGLEventListener( this );
	}
	
	private boolean compile( GL3 gl )
	{
		_tree.recompile = false;
		
		// Compile the renderer
		if( !_renderer.compile( gl, _tree.root() ) )
		{
			System.out.println( "FAILED" );
			return false;
		}
		
		return true;
	}
	
	@Override
	public void display( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		if( _tree.recompile ) _isValid = compile( gl );
		
		if( _isValid )
		{
			gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
			
			_renderer.render( gl );
			
			gl.glBindVertexArray( 0 );
			gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, 0 );
			
			int error = gl.glGetError();
			if( error != 0 )
				System.err.println( "Error code " + error );
		}
		
		// Increment timer
		ShaderProgram.timer += 0.01f;
		ShaderProgram.timer -= Math.floor( ShaderProgram.timer );
	}

	@Override
	public void dispose( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		_tree.root().dispose( gl );
		
		ITexture.deleteTextures( gl );
		ShaderCode.deleteShaders( gl );
	}

	@Override
	public void init( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		if( !gl.isExtensionAvailable("GL_ARB_explicit_attrib_location") )
			System.err.println( "MISSING EXTENSION" );
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		gl.glClearDepth( 1.0f );
		
		gl.glEnable( GL3.GL_TEXTURE_2D );
		gl.glEnable( GL3.GL_DEPTH_TEST );
		//gl.glEnable( GL3.GL_CULL_FACE );
		
		gl.glDisable( GL3.GL_BLEND );
		//gl.glCullFace( GL3.GL_BACK );
		gl.glDepthFunc( GL3.GL_LEQUAL );
		gl.glBlendFunc( GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA );
		
		// Set framerate
		setAnimator( new FPSAnimator( g, 60, true ) );
		getAnimator().start();
		_isAnimated = true;
	}

	@Override
	public void reshape( GLAutoDrawable g, int x, int y, int width, int height ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		_camera.setResolution( width, height );
		gl.glViewport( 0, 0, width, height );
	}
	
	public void recompile()
	{
		_tree.recompile = true;
		repaint();
	}
	
	@Override
	public void repaint()
	{
		if( !_isAnimated )
			super.repaint();
	}
}
