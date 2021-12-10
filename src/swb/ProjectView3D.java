package swb;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

public class ProjectView3D extends GLJPanel implements GLEventListener
{
	private static final long serialVersionUID = 1L;
	
	private ActiveCamera _camera;
	private ProjectTree _tree;
	private Renderer _renderer;
	
	private int _resWidth = 0;
	private int _resHeight = 0;
	
	// Flags
	private boolean _isValid = false;
	private boolean _isAnimated = false;
	
	private Queue<RenderAction> _actions = new LinkedList<>();
	
	public ProjectView3D( GLCapabilities capabilities, ProjectTree editor )
	{
		super( capabilities );
		_tree = editor;
		
		_camera = new ActiveCamera( this );
		_renderer = new Renderer( _camera );
		
		addMouseListener( _camera );
		addMouseMotionListener( _camera );
		addMouseWheelListener( _camera );
		addGLEventListener( this );
	}
	
	public void queueEvent( RenderAction action )
	{
		_actions.add( action );
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
	
	private void export( GL3 gl, ExportInfo info )
	{
		if( !_isValid ) return;
		
		IntBuffer buffer = IntBuffer.wrap( new int[info.width * info.height] );
		int[] fbo = new int[1];
		int[] rbo = new int[2];
		
		// Set-up frame buffer
		gl.glGenFramebuffers( 1, fbo, 0 );
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, fbo[0] );
		
		// Set-up Color buffer
		gl.glGenRenderbuffers( 2, rbo, 0 );
		gl.glBindRenderbuffer( GL3.GL_RENDERBUFFER, rbo[0] );
		gl.glRenderbufferStorage( GL3.GL_RENDERBUFFER, GL3.GL_RGBA, info.width, info.height );
		gl.glFramebufferRenderbuffer( GL3.GL_DRAW_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_RENDERBUFFER, rbo[0] );
		
		// Set-up Depth buffer
		gl.glBindRenderbuffer( GL3.GL_RENDERBUFFER, rbo[1] );
		gl.glRenderbufferStorage( GL3.GL_RENDERBUFFER, GL3.GL_DEPTH_COMPONENT16, info.width, info.height );
		gl.glFramebufferRenderbuffer( GL3.GL_DRAW_FRAMEBUFFER, GL3.GL_DEPTH_ATTACHMENT, GL3.GL_RENDERBUFFER, rbo[1] );
		
		// Render to buffer
		_camera.setResolution( info.width, info.height );
		gl.glViewport( 0, 0, info.width, info.height );
		gl.glReadBuffer( GL3.GL_COLOR_ATTACHMENT0 );
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		_renderer.render( gl );
		gl.glReadPixels( 0, 0, info.width, info.height, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, buffer );
		
		gl.glBindVertexArray( 0 );
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, 0 );
		gl.glBindRenderbuffer( GL3.GL_RENDERBUFFER, 0 );
		
		int error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Error code " + error );
		
		// Clean-up
		gl.glReadBuffer( GL3.GL_BACK );
		gl.glDeleteFramebuffers( 1, fbo, 0 );
		gl.glDeleteRenderbuffers( 2, rbo, 0 );
		_camera.setResolution( _resWidth, _resHeight );
		gl.glViewport( 0, 0, _resWidth, _resHeight );
		
		DataBufferInt iBuffer = new DataBufferInt( buffer.array(), info.width * info.height );
		ColorModel cm = new DirectColorModel( 32, 0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000 );
		SampleModel sm = cm.createCompatibleSampleModel( info.width, info.height );
		WritableRaster raster = Raster.createWritableRaster( sm, iBuffer, null );
		BufferedImage image = new BufferedImage( cm, raster, false, null );
		
		try 
		{
			ImageIO.write( image, "png", info.file );
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void display( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		if( _tree.recompile ) _isValid = compile( gl );
		
		while( !_actions.isEmpty() )
		{
			switch( _actions.peek().id )
			{
			case ExportInfo.EXPORT_ID: export( gl, (ExportInfo)_actions.poll() ); break;
			default: 	_actions.poll(); break;
			}
		}
		
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
		_camera.timer += 0.01f;
		_camera.timer -= (float)Math.floor( _camera.timer );
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
		
		_resWidth = width;
		_resHeight = height;
		
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
	
	public static abstract class RenderAction
	{
		public final int id;
		
		public RenderAction( int id )
		{
			this.id = id;
		}
	}
	
	public static class ExportInfo extends RenderAction
	{
		public final static int EXPORT_ID = 0x1;
		public final int width;
		public final int height;
		public final File file;
		
		public ExportInfo( File file, int w, int h )
		{
			super( EXPORT_ID );
			this.file = file;
			this.width = w;
			this.height = h;
		}
	}
}
