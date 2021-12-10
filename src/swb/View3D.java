package swb;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

import swb.assets.GLStream;

public class View3D extends GLJPanel implements GLEventListener
{
	private static final long serialVersionUID = 1L;
	
	private Camera _camera;
	private IRenderer _renderer;
	private int targetFrameRate;
	
	public View3D( Camera camera, IRenderer renderer, int FPS )
	{
		super( Workbench.capabilities );
		setCamera( camera );
		setRenderer( renderer );
		targetFrameRate = FPS;
		addGLEventListener( this );
	}
	
	public void setCamera( Camera camera )
	{
		_camera = camera;
		addMouseListener( _camera );
		addMouseMotionListener( _camera );
		addMouseWheelListener( _camera );
	}
	
	public void setRenderer( IRenderer renderer )
	{
		_renderer = renderer;
	}

	@Override
	public void display( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		Time.tick();
		GLStream.upload( gl );
		
		int error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Error during frame init: " + error );
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		_renderer.render( gl, _camera );
		
		gl.glBindVertexArray( 1 );
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, 0 );
		
		error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Error code " + error );
	}

	@Override
	public void dispose( GLAutoDrawable g ) 
	{
		
	}

	@Override
	public void init( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		if( !gl.isExtensionAvailable("GL_ARB_explicit_attrib_location") )
			System.err.println( "MISSING EXTENSION" );
		
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		gl.glClearDepth( 1.0f );
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		gl.glEnable( GL3.GL_DEPTH_TEST );
		gl.glDepthFunc( GL3.GL_LEQUAL );
		gl.glEnable( GL3.GL_CULL_FACE );
		gl.glCullFace( GL3.GL_BACK );
		gl.glDisable( GL3.GL_BLEND );
		gl.glBlendFunc( GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA );
		
		// Set framerate
		if( targetFrameRate > 0 )
		{
			setAnimator( new FPSAnimator( g, targetFrameRate, true ) );
			getAnimator().start();
		}
		
		_renderer.init( gl );
		
		int error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Init error code " + error );
	}

	@Override
	public void reshape( GLAutoDrawable g, int x, int y, int width, int height ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		_camera.setResolution( width, height );
		gl.glViewport( 0, 0, width, height );
	}
}
