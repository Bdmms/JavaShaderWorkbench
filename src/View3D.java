
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;

public class View3D extends GLJPanel implements GLEventListener
{
	private static final long serialVersionUID = 1L;
	
	private Camera _camera;
	private ProjectTree _tree;
	
	// Flags
	private boolean _recompile = false;
	private boolean _valid = false;
	
	public View3D( GLCapabilities capabilities, ProjectTree editor )
	{
		super( capabilities );
		_tree = editor;
		
		_camera = new Camera( this );
		addKeyListener( _camera );
		addMouseListener( _camera );
		addMouseMotionListener( _camera );
		addMouseWheelListener( _camera );
		addGLEventListener( this );
	}
	
	private boolean recompileAll( GL3 gl )
	{
		_recompile = false;
		
		// Rebuilds tree
		gl.glUseProgram( 0 );
		
		System.out.println("Compiling...");
		if( !_tree.build( gl ) ) return false;
		
		System.out.println("Uploading...");
		_tree.upload( gl );
		
		Material.uploadTextures( gl );
		System.out.println( "SUCCESS" );
		return true;
	}
	
	private void render( GL3 gl ) 
	{
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		_tree.render( gl );
		gl.glBindVertexArray( 0 );
		
		int error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Error code " + error );
	}
	
	@Override
	public void display( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		if( _recompile ) _valid = recompileAll( gl );
		if( _valid ) render( gl );
		
		// Increment timer
		//ShaderProgram.timer += 0.01f;
		//ShaderProgram.timer += Math.floor( ShaderProgram.timer );
	}

	@Override
	public void dispose( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		_tree.dispose( gl );
	}

	@Override
	public void init( GLAutoDrawable g ) 
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
		
		// Set framerate
		//setAnimator( new FPSAnimator( g, 30, true ) );
		//getAnimator().start();
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
		_recompile = true;
		repaint();
	}
	
	@Override
	public void repaint()
	{
		
		super.repaint();
	}
	
	/*
	float sinX = (float)Math.sin( rotX );
	float sinY = (float)Math.sin( rotY );
	float sinZ = (float)Math.sin( rotZ );
	float cosX = (float)Math.cos( rotX );
	float cosY = (float)Math.cos( rotY );
	float cosZ = (float)Math.cos( rotZ );

	ShaderProgram.view[0] = scale * (cosX * cosY);
	ShaderProgram.view[1] = scale * (cosX * sinY * sinZ - sinX * cosZ);
	ShaderProgram.view[2] = scale * (cosX * sinY * cosZ + sinX * sinZ);

	ShaderProgram.view[4] = scale * (sinX * cosY);
	ShaderProgram.view[5] = scale * (sinX * sinY * sinZ + cosX * cosZ);
	ShaderProgram.view[6] = scale * (sinX * sinY * cosZ - cosX * sinZ);
	
	ShaderProgram.view[8] = scale * (-sinY);
	ShaderProgram.view[9] = scale * (cosY * sinZ);
	ShaderProgram.view[10] = scale * (cosY * cosZ);*/
}
