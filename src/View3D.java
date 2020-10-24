import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

public class View3D extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private static final long serialVersionUID = 1L;
	
	private boolean _recompile = false;
	private boolean _valid = false;
	private ProjectTree _tree;
	
	// Shader uniforms
	private float timer = 0.0f;
	private float[] view = new float[16];
	
	// Shader uniform locations
	private int timer_loc;
	private int view_loc;
	
	// Mouse variables
	private int lastX;
	private int lastY;
	private float sensitivity = 5.0f;
	private float rotX = 0.0f;
	private float rotY = 0.0f;
	private float rotZ = 0.0f;
	private float scale = 0.1f;
	
	public View3D( GLCapabilities capabilities, ProjectTree editor )
	{
		super( capabilities );
		_tree = editor;
		
		view[ 0] = scale;
		view[ 5] = scale;
		view[10] = scale;
		view[15] = 1.0f;
		
		addMouseListener( this );
		addMouseMotionListener( this );
		addMouseWheelListener( this );
		addGLEventListener( this );
	}
	
	private boolean recompileAll( GL3 gl )
	{
		_recompile = false;
		
		// TODO: automate
		Model model = (Model)_tree.get( 0 );
		ShaderProgram program = (ShaderProgram)model.children().get( 0 );
		Shader vShader = (Shader)program.children().get( 0 );
		UniformList uniforms = (UniformList)model.children().get( model.children().size() - 1 );
		
		VertexAttribute attribute = VertexAttribute.parse( vShader.getCode() );
		if( attribute == null || !attribute.isCompatibleWith( 8 ) )
		{
			System.err.println("Error - Incompatible attribute!");
			return false;
		}
		
		_tree.dispose( gl );
		if( !_tree.initialize( gl ) )
			return false;
		
		attribute.print();
		attribute.bind( gl );
		
		// Bind uniforms
		gl.glUseProgram( program.getID() );
		
		// Timer that can be used in shader
		timer_loc = gl.glGetUniformLocation( program.getID(), "time" );
		view_loc = gl.glGetUniformLocation( program.getID(), "view" );
		
		// Initialize parameters
		gl.glUniform1f( timer_loc, timer );
		gl.glUniformMatrix4fv( view_loc, 1, false, view, 0);
		
		uniforms.initialize( gl, program );
		
		Material.uploadAllTextures( gl );
		System.out.println( "SUCCESS" );
		return true;
	}
	
	private void render( GL3 gl ) 
	{
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		_tree.render( gl );
		gl.glBindVertexArray( 0 );
		
		// Set timer parameter used by shader
		gl.glUniform1f( timer_loc, timer );
		gl.glUniformMatrix4fv( view_loc, 1, false, view, 0);
		
		int error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Error on render: " + error );
	}
	
	@Override
	public void display( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		if( _recompile )
			_valid = recompileAll( gl );
		
		if( _valid )
			render( gl );
		
		timer += 0.01f;
		timer += Math.floor( timer );
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
		
		setAnimator( new FPSAnimator( g, 30, true ) );
		getAnimator().start();
	}

	@Override
	public void reshape( GLAutoDrawable g, int x, int y, int width, int height ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		gl.glViewport( 0, 0, width, height );
	}
	
	public void recompile()
	{
		_recompile = true;
	}

	@Override
	public void mouseDragged( MouseEvent e ) 
	{
		float xOffset = (e.getX() - lastX) * sensitivity / getWidth();
		float yOffset = (lastY - e.getY()) * sensitivity / getHeight();
		lastX = e.getX();
		lastY = e.getY();
		
		rotX += xOffset;
		rotY += yOffset;
		
		float sinX = (float)Math.sin( rotX );
		float sinY = (float)Math.sin( rotY );
		float sinZ = (float)Math.sin( rotZ );
		float cosX = (float)Math.cos( rotX );
		float cosY = (float)Math.cos( rotY );
		float cosZ = (float)Math.cos( rotZ );

		view[0] = scale * (cosX * cosY);
		view[1] = scale * (cosX * sinY * sinZ - sinX * cosZ);
		view[2] = scale * (cosX * sinY * cosZ + sinX * sinZ);

		view[4] = scale * (sinX * cosY);
		view[5] = scale * (sinX * sinY * sinZ + cosX * cosZ);
		view[6] = scale * (sinX * sinY * cosZ - cosX * sinZ);
		
		view[8] = scale * (-sinY);
		view[9] = scale * (cosY * sinZ);
		view[10] = scale * (cosY * cosZ);
	}

	@Override
	public void mouseMoved( MouseEvent e ) 
	{
		
	}

	@Override
	public void mouseClicked( MouseEvent e ) 
	{
		
	}

	@Override
	public void mouseEntered( MouseEvent e ) 
	{
		
	}

	@Override
	public void mouseExited( MouseEvent e )
	{
		
	}

	@Override
	public void mousePressed( MouseEvent e ) 
	{
		lastX = e.getX();
		lastY = e.getY();
	}

	@Override
	public void mouseReleased( MouseEvent e ) 
	{
		
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		scale += (e.getPreciseWheelRotation() * 0.01);
	}
}
